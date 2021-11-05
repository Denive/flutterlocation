package com.lyokone.location

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.lyokone.location.hadler.MethodCallHandlerImpl
import com.lyokone.location.hadler.StreamHandlerImpl
import com.lyokone.location.service.FlutterLocationService
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.FlutterPlugin.FlutterPluginBinding


/**
 * LocationPlugin
 */
class LocationPlugin : FlutterPlugin {
    companion object {
        private const val TAG = "LocationPlugin"
    }

    private var methodCallHandler: MethodCallHandlerImpl? = null
    private var streamHandlerImpl: StreamHandlerImpl? = null

    private var locationService: FlutterLocationService? = null

    override fun onAttachedToEngine(binding: FlutterPluginBinding) {
        Log.d(TAG, "onAttachedToEngine called")
        binding.applicationContext.bindService(
            Intent(binding.applicationContext, FlutterLocationService::class.java),
            serviceConnection,
            Context.BIND_AUTO_CREATE
        )

        methodCallHandler = MethodCallHandlerImpl().apply {
            startListening(binding.binaryMessenger)
        }

        streamHandlerImpl = StreamHandlerImpl().apply {
            startListening(binding.binaryMessenger)
        }
    }

    override fun onDetachedFromEngine(binding: FlutterPluginBinding) {
        Log.d(TAG, "onDetachedFromEngine called")
        methodCallHandler?.stopListening()
        methodCallHandler = null

        streamHandlerImpl?.stopListening()
        streamHandlerImpl = null
    }

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            Log.d(TAG, "Service connected: $name")

            locationService = ((service as FlutterLocationService.LocalBinder).service)

            methodCallHandler?.locationService = locationService
            streamHandlerImpl?.locationService = locationService
        }

        override fun onServiceDisconnected(name: ComponentName) {
            Log.d(TAG, "Service disconnected:$name")
        }
    }
}
