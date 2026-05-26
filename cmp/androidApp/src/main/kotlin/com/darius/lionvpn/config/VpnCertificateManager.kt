package com.darius.lionvpn.config

import android.content.Context
import android.widget.Toast
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.darius.lionvpn.ProxyService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File

class VpnCertificateManager(private val context: Context) {

    suspend fun checkAndGenerateCertificate(): File = withContext(Dispatchers.IO) {
        val caCertFile = try {
            if (!Python.isStarted()) {
                Python.start(AndroidPlatform(context.applicationContext))
            }
            val py = Python.getInstance()
            try {
                py.getModule("android_entry")
            } catch (e: Exception) {
                Timber.w(e, "Error pre-loading android_entry module")
            }
            val mitmModule = py.getModule("proxy.mitm")
            val path = mitmModule.get("CA_CERT_FILE")?.toString()
            if (path != null) {
                val file = File(path)
                if (!file.exists()) {
                    ProxyService.addLogLine("CA certificate not found. Generating it on-demand...")
                    try {
                        mitmModule.callAttr("MITMCertManager")
                        ProxyService.addLogLine("CA certificate generated successfully on-demand at: ${file.absolutePath}")
                    } catch (genEx: Exception) {
                        Timber.e(genEx, "Failed to generate CA certificate on-demand")
                        ProxyService.addLogLine("Error generating CA certificate: ${genEx.message}")
                    }
                }
                file
            } else {
                File(context.cacheDir, "ca/ca.crt")
            }
        } catch (e: Exception) {
            Timber.e(e, "Error retrieving cert path from Python")
            File(context.cacheDir, "ca/ca.crt")
        }
        caCertFile
    }

    fun saveCertificateUri(uri: android.net.Uri, certFile: File) {
        try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                certFile.inputStream().use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            ProxyService.addLogLine("CA Certificate saved successfully to storage.")
            Toast.makeText(context, "Certificate saved successfully!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Timber.e(e, "Failed to save certificate file")
            ProxyService.addLogLine("Error saving certificate: ${e.message}")
            Toast.makeText(context, "Failed to save certificate: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
