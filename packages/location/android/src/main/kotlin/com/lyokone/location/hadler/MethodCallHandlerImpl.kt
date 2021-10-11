package com.lyokone.location.hadler

import android.util.Log
import com.lyokone.location.model.LocationSettings
import com.lyokone.location.service.FlutterLocationService
import com.lyokone.location.service.NotificationOptions
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler

internal class MethodCallHandlerImpl : MethodCallHandler {
    companion object {
        private const val TAG = "MethodCallHandlerImpl"
        private const val METHOD_CHANNEL_NAME = "lyokone/location"
    }

    var locationService: FlutterLocationService? = null

    private var channel: MethodChannel? = null

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "changeSettings" -> onChangeSettings(call, result)
            "getLocation" -> onGetLocation(result)
            "hasPermission" -> onHasPermission(result)
            "serviceEnabled" -> onServiceEnabled(result)
            "isBackgroundModeEnabled" -> isForegroundServiceEnabled(result)
            "enableBackgroundMode" -> enableForegroundServiceMode(result, call)
            "changeNotificationOptions" -> onChangeNotificationOptions(call, result)
            "requestPermission" -> result.notImplemented()
            "requestService" -> result.notImplemented()
            else -> result.notImplemented()
        }
    }

    private fun enableForegroundServiceMode(result: MethodChannel.Result, call: MethodCall) {
        if (locationService != null) {
            locationService?.isForegroundLocationEnabled = call.argument<Boolean>("enable") == true

            result.success(if (locationService?.isForegroundLocationEnabled == true) 1 else 0)
        } else {
            result.error(
                "SERVICE_STATUS_ERROR",
                "Location service didn't start",
                null
            )
        }
    }

    /**
     * Registers this instance as a method call handler on the given
     * `messenger`.
     */
    fun startListening(messenger: BinaryMessenger) {
        if (channel != null) {
            Log.wtf(TAG, "Setting a method call handler before the last was disposed.")
            stopListening()
        }

        channel = MethodChannel(messenger, METHOD_CHANNEL_NAME).apply {
            setMethodCallHandler(this@MethodCallHandlerImpl)
        }
    }

    /**
     * Clears this instance from listening to method calls.
     */
    fun stopListening() {
        if (channel == null) {
            Log.d(TAG, "Tried to stop listening when no MethodChannel had been initialized.")
            return
        }

        channel?.setMethodCallHandler(null)
        channel = null
    }

    private fun onChangeSettings(call: MethodCall, result: MethodChannel.Result) {
        try {
            locationService?.changeSettings(LocationSettings(call))

            result.success(1)
        } catch (e: Exception) {
            result.error(
                "CHANGE_SETTINGS_ERROR",
                "An unexcepted error happened during location settings change:" + e.message,
                null,
            )
        }
    }

    private fun onGetLocation(result: MethodChannel.Result) {
        if (locationService?.checkPermissions() == true) {
            locationService?.getCurrentLocation(result)
        } else {
            result.error("PERMISSION_DENIED", "Location permission denied", null)
        }
    }

    private fun onHasPermission(result: MethodChannel.Result) {
        if (locationService?.checkPermissions() == true) {
            result.success(1)
        } else {
            result.success(0)
        }
    }

    private fun onServiceEnabled(result: MethodChannel.Result) {
        try {
            result.success(locationService?.checkServiceEnabled() ?: 0)
        } catch (e: Exception) {
            result.error(
                "SERVICE_STATUS_ERROR",
                "Location service status couldn't be determined",
                null
            )
        }
    }

    private fun isForegroundServiceEnabled(result: MethodChannel.Result) {
        if (locationService?.isForegroundLocationEnabled == true) {
            result.success(1)
        } else {
            result.success(0)
        }
    }

    private fun onChangeNotificationOptions(call: MethodCall, result: MethodChannel.Result) {
        try {
            val options = NotificationOptions(call)

            val notificationMeta: Map<String, Any>? =
                locationService?.changeNotificationOptions(options)

            if (notificationMeta != null) {
                result.success(notificationMeta)
            } else {
                result.error(
                    "CHANGE_NOTIFICATION_OPTIONS_ERROR",
                    "An unexpected error happened during notification options change: notificationMeta is Null",
                    options
                )
            }
        } catch (e: Exception) {
            result.error(
                "CHANGE_NOTIFICATION_OPTIONS_ERROR",
                "An unexpected error happened during notification options change:" + e.message,
                null
            )
        }
    }

}
