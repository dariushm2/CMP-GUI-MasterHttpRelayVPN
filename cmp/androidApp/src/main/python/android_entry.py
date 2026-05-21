import sys
import os
import json
import asyncio
import logging

# Ensure the synced "src" directory is in sys.path
current_dir = os.path.dirname(os.path.abspath(__file__))
src_dir = os.path.join(current_dir, "src")
if src_dir not in sys.path:
    sys.path.insert(0, src_dir)

from proxy.proxy_server import ProxyServer

# Logcat logging handler
class LogcatHandler(logging.Handler):
    def emit(self, record):
        message = self.format(record)
        try:
            from android.util import Log
            tag = f"Python:{record.name}"
            # Map Python logging levels to Android Log levels
            if record.levelno >= logging.ERROR:
                Log.e(tag, message)
            elif record.levelno >= logging.WARNING:
                Log.w(tag, message)
            elif record.levelno >= logging.INFO:
                Log.i(tag, message)
            else:
                Log.d(tag, message)
        except ImportError:
            # Fallback for local unit testing
            print(f"[{record.levelname}] {record.name}: {message}")
            
        # Push logs to KMP UI log stream via Kotlin ProxyService
        try:
            from com.darius.lionvpn import ProxyService
            ProxyService.addLogLine(message)
        except ImportError:
            pass

_current_server = None
_current_loop = None
_server_task = None

def start_proxy(config_json_str):
    global _current_server, _current_loop, _server_task
    
    # Configure Logcat logger
    logger = logging.getLogger()
    logger.setLevel(logging.INFO)
    for h in list(logger.handlers):
        logger.removeHandler(h)
    logger.addHandler(LogcatHandler())
    
    log = logging.getLogger("AndroidEntry")
    log.info("Starting Python VPN proxy backend from Kotlin...")
    
    try:
        config = json.loads(config_json_str)
        log.info(f"Loaded config: http_port={config.get('http_port')}, socks5_port={config.get('socks5_port')}")
        
        # Log CA certificate information
        try:
            from proxy.mitm import CA_DIR, CA_CERT_FILE
            log.info(f"Python mitm CA_DIR: {CA_DIR}")
            log.info(f"Python mitm CA_CERT_FILE: {CA_CERT_FILE}")
            log.info(f"Python CA certificate exists: {os.path.exists(CA_CERT_FILE)}")
        except Exception as cert_err:
            log.error(f"Error inspecting CA cert in Python: {cert_err}")

        # SOCKS5 is mandatory at runtime in main.py, force set it.
        config["socks5_enabled"] = True
        
        # Instantiate proxy server
        server = ProxyServer(config)
        _current_server = server
        
        # Run asyncio loop in the current thread (already spawned by Android Service)
        loop = asyncio.new_event_loop()
        asyncio.set_event_loop(loop)
        _current_loop = loop
        
        log.info("Starting proxy server listeners...")
        loop.run_until_complete(server.start())
        
    except Exception as e:
        log.exception(f"Fatal error running proxy server: {e}")
    finally:
        log.info("Proxy server has stopped.")

def stop_proxy():
    global _current_server, _current_loop
    log = logging.getLogger("AndroidEntry")
    log.info("Stopping Python proxy server...")
    
    if _current_server and _current_loop:
        try:
            # Safely schedule stop on the running asyncio loop
            _current_loop.call_soon_threadsafe(
                lambda: asyncio.create_task(_current_server.stop())
            )
            log.info("Stop scheduled successfully.")
        except Exception as e:
            log.error(f"Error scheduling proxy stop: {e}")
        finally:
            _current_server = None
            _current_loop = None
    else:
        log.warning("Proxy server is not active or loop is not running.")
