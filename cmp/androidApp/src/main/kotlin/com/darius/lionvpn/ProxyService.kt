package com.darius.lionvpn

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.ProxyInfo
import android.net.VpnService
import android.os.Build
import android.os.IBinder
import android.os.ParcelFileDescriptor
import androidx.core.app.NotificationCompat
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.darius.lionvpn.ui.home.ConnectionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber

@SuppressLint("VpnServicePolicy")
class ProxyService : VpnService() {

    private var pythonThread: Thread? = null
    private var vpnInterface: ParcelFileDescriptor? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        Timber.i("ProxyService onStartCommand action: $action")

        if (intent == null || action == null) {
            Timber.i("ProxyService restarted by system with null intent. Restoring VPN state...")
            val prefs = getSharedPreferences(Constants.Prefs.NAME, Context.MODE_PRIVATE)
            val rawConfig = prefs.getString(Constants.Prefs.KEY_RAW_CONFIG_JSON, "") ?: ""
            if (rawConfig.isNotBlank()) {
                startProxy(rawConfig)
            } else {
                stopProxy()
                stopSelf()
                return START_NOT_STICKY
            }
        } else if (action == ACTION_STOP) {
            stopProxy()
            stopSelf()
            return START_NOT_STICKY
        } else if (action == ACTION_START) {
            val configJson = intent.getStringExtra(EXTRA_CONFIG) ?: "{}"
            startProxy(configJson)
        } else if (action == ACTION_REPOST_NOTIFICATION) {
            if (_isVpnRunning.value) {
                Timber.i("Notification swiped away while VPN is running. Re-posting...")
                val notification = createNotification()
                startForeground(NOTIFICATION_ID, notification)
            }
        }

        return START_STICKY
    }

    private fun startProxy(configJson: String) {
        if (_isVpnRunning.value) {
            Timber.w("Proxy is already running, ignoring start request.")
            return
        }

        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)

        _vpnState.value = ConnectionState.CONNECTING
        _isVpnRunning.value = true

        // Log starting message instantly in English only matching log timestamp pattern
        _vpnLogs.value = listOf(VpnLogger.formatInfo("VPN process is starting... warming up"))

        establishVpn()

        pythonThread = Thread {
            try {
                // Initialize Python if not already started
                if (!Python.isStarted()) {
                    Python.start(AndroidPlatform(applicationContext))
                }

                val py = Python.getInstance()
                val entry = py.getModule("android_entry")
                Timber.i("Launching Python proxy server thread...")
                entry.callAttr("start_proxy", configJson)
            } catch (e: Exception) {
                Timber.e(e, "Error running Python proxy server")
                addLogLine(VpnLogger.formatInfo("Error: ${e.message}"))
            } finally {
                Timber.i("Python proxy thread terminated.")
                stopVpn()
                _vpnState.value = ConnectionState.DISCONNECTED
                _isVpnRunning.value = false
            }
        }.apply {
            isDaemon = true
            start()
        }
    }

    private fun establishVpn() {
        try {
            if (vpnInterface != null) {
                return
            }
            val builder = Builder()
            builder.setSession("Lion VPN Session")
                .addAddress("10.8.0.2", VPN_ADDRESS_PREFIX_LENGTH)
                // Dummy route to satisfy establish requirement without blackholing internet
                .addRoute("10.8.0.0", VPN_ADDRESS_PREFIX_LENGTH)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val proxyInfo = ProxyInfo.buildDirectProxy("127.0.0.1", PROXY_PORT)
                builder.setHttpProxy(proxyInfo)
            }

            vpnInterface = builder.establish()
            addLogLine(VpnLogger.formatInfo("System-wide VPN proxy established successfully"))
        } catch (e: Exception) {
            Timber.e(e, "Failed to establish VPN interface")
            addLogLine(VpnLogger.formatInfo("Error establishing VPN: ${e.message}"))
        }
    }

    private fun stopVpn() {
        try {
            vpnInterface?.close()
        } catch (e: Exception) {
            Timber.e(e, "Error closing VPN interface")
        }
        vpnInterface = null
        addLogLine(VpnLogger.formatInfo("System-wide VPN proxy tunnel closed"))
    }

    private fun stopProxy() {
        Timber.i("Stopping ProxyService...")
        stopVpn()

        val threadToStop = pythonThread
        pythonThread = null
        _vpnState.value = ConnectionState.DISCONNECTED
        _isVpnRunning.value = false

        // Run the shutdown sequence in a separate background thread to keep the main UI thread responsive
        Thread {
            try {
                if (Python.isStarted()) {
                    val py = Python.getInstance()
                    val entry = py.getModule("android_entry")
                    Timber.i("Triggering Python stop_proxy...")
                    entry.callAttr("stop_proxy")
                }
            } catch (e: Exception) {
                Timber.e(e, "Error stopping Python proxy server")
            }

            if (threadToStop != null && threadToStop.isAlive) {
                try {
                    Timber.i("Waiting for Python proxy thread to join in background...")
                    threadToStop.join(THREAD_JOIN_TIMEOUT_MS) // Wait up to 2 seconds for clean unbind/shutdown
                    if (threadToStop.isAlive) {
                        Timber.w("Python proxy thread did not exit within 2 seconds. Interrupting...")
                        threadToStop.interrupt()
                    }
                } catch (e: InterruptedException) {
                    Timber.e(e, "Interrupted while waiting for Python proxy thread to join")
                }
            }
            Timber.i("Proxy Service background shutdown complete.")
        }.start()
    }

    override fun onDestroy() {
        stopProxy()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return if (intent != null && VpnService.SERVICE_INTERFACE == intent.action) {
            super.onBind(intent)
        } else {
            null
        }
    }

    override fun onRevoke() {
        Timber.i("VPN Revoked by system/user.")
        addLogLine(VpnLogger.formatInfo("VPN connection revoked by system"))
        stopProxy()
        stopSelf()
        super.onRevoke()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "VPN Proxy Server Status",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Shows the status of the local VPN proxy server."
        }
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    private fun createNotification(): Notification {
        val stopIntent = Intent(this, ProxyService::class.java).apply {
            action = ACTION_STOP
        }

        // Handle API 31+ pending intent flags
        val pendingIntentFlags =
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE

        val stopPendingIntent = PendingIntent.getService(
            this, 0, stopIntent, pendingIntentFlags
        )

        val mainIntent = Intent(this, MainActivity::class.java)
        val mainPendingIntent = PendingIntent.getActivity(
            this, 0, mainIntent, pendingIntentFlags
        )

        val deleteIntent = Intent(this, ProxyService::class.java).apply {
            action = ACTION_REPOST_NOTIFICATION
        }
        val deletePendingIntent = PendingIntent.getService(
            this, 1, deleteIntent, pendingIntentFlags
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Lion VPN Server Active")
            .setContentText("HTTP & SOCKS5 proxy running in background")
            .setSmallIcon(android.R.drawable.ic_menu_share) // Using standard system icon
            .setContentIntent(mainPendingIntent)
            .setDeleteIntent(deletePendingIntent)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Disconnect", stopPendingIntent)
            .build()

        notification.flags = notification.flags or Notification.FLAG_NO_CLEAR or Notification.FLAG_ONGOING_EVENT
        return notification
    }

    companion object {
        const val CHANNEL_ID = "VPN_PROXY_SERVICE_CHANNEL"
        const val NOTIFICATION_ID = 101

        const val ACTION_START = "START"
        const val ACTION_STOP = "STOP"
        const val ACTION_REPOST_NOTIFICATION = "REPOST_NOTIFICATION"
        const val EXTRA_CONFIG = "CONFIG"

        private const val VPN_ADDRESS_PREFIX_LENGTH = 24
        private const val PROXY_PORT = 8085
        private const val THREAD_JOIN_TIMEOUT_MS = 2000L

        private val _isVpnRunning = MutableStateFlow(false)
        val isVpnRunning: StateFlow<Boolean> = _isVpnRunning.asStateFlow()

        private val _vpnState = MutableStateFlow(ConnectionState.DISCONNECTED)
        val vpnState: StateFlow<ConnectionState> = _vpnState.asStateFlow()

        private val _vpnLogs = MutableStateFlow(emptyList<String>())
        val vpnLogs: StateFlow<List<String>> = _vpnLogs.asStateFlow()

        // Exposed static method for Chaquopy Python code to stream logs to UI
        @JvmStatic
        fun addLogLine(line: String) {
            Timber.d("[Python log] %s", line)

            // Watch for HTTP proxy start logs to transition to CONNECTED state on Android
            if (_vpnState.value == ConnectionState.CONNECTING) {
                if (VpnLogger.isConnectionSuccessLog(line)) {
                    _vpnState.value = ConnectionState.CONNECTED
                }
            }

            _vpnLogs.update { it.appendCappedLog(line) }
        }

        @JvmStatic
        fun clearLogs() {
            _vpnLogs.value = emptyList()
        }
    }
}
