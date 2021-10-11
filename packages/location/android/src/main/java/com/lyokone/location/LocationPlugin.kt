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
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding


/**
 * LocationPlugin
 */
class LocationPlugin : FlutterPlugin, ActivityAware {
    companion object {
        private const val TAG = "LocationPlugin"
    }

    private var methodCallHandler: MethodCallHandlerImpl? = null
    private var streamHandlerImpl: StreamHandlerImpl? = null

    private var locationService: FlutterLocationService? = null
    private var activityBinding: ActivityPluginBinding? = null

    override fun onAttachedToEngine(binding: FlutterPluginBinding) {
        methodCallHandler = MethodCallHandlerImpl().apply {
            startListening(binding.binaryMessenger)
        }

        streamHandlerImpl = StreamHandlerImpl().apply {
            startListening(binding.binaryMessenger)
        }
    }

    override fun onDetachedFromEngine(binding: FlutterPluginBinding) {
        methodCallHandler?.stopListening()
        methodCallHandler = null

        streamHandlerImpl?.stopListening()
        streamHandlerImpl = null
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        attachToActivity(binding)
    }

    override fun onDetachedFromActivity() {
        detachActivity()
    }

    override fun onDetachedFromActivityForConfigChanges() {
        detachActivity()
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        attachToActivity(binding)
    }

    private fun attachToActivity(binding: ActivityPluginBinding) {
        activityBinding = binding
        activityBinding!!.activity.bindService(
            Intent(
                binding.activity,
                FlutterLocationService::class.java
            ), serviceConnection, Context.BIND_AUTO_CREATE
        )
    }

    private fun detachActivity() {
        locationService?.removeLocationUpdates()
        activityBinding!!.activity.unbindService(serviceConnection)
        activityBinding = null
    }


    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            Log.d(TAG, "Service connected: $name")
            locationService = ((service as FlutterLocationService.LocalBinder).service)
            methodCallHandler?.locationService = locationService
        }

        override fun onServiceDisconnected(name: ComponentName) {
            Log.d(TAG, "Service disconnected:$name")
        }
    }
}
