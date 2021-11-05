package com.lyokone.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.lyokone.location.model.*
import io.flutter.plugin.common.EventChannel.EventSink
import io.flutter.plugin.common.MethodChannel

class FlutterLocation(
    private val context: Context,
    private val lopper: Looper,
    private val mainLooper: Looper
) {
    companion object {
        private const val ONE_LOCATION_CALLBACK = 1
        private const val STREAM_LOCATION_CALLBACK = 2

        private const val TAG = "FlutterLocation"
    }

    private val locationManager =
        context.applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager?

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    private var settings = LocationSettings()

    private var callbacks = mutableMapOf<Int, FlutterLocationCallback?>(
        ONE_LOCATION_CALLBACK to null,
        STREAM_LOCATION_CALLBACK to null
    )

    fun changeSettings(updatedSettings: LocationSettings) {
        Log.d(TAG, "changeSettings called: settings : $updatedSettings")
        settings = updatedSettings
        callbacks[STREAM_LOCATION_CALLBACK]?.updateSettings(settings)
    }

    /**
     * Return the current state of the permissions needed.
     */
    fun checkPermissions(): Boolean {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }

        val locationPermissionState = ContextCompat.checkSelfPermission(
            context, permission
        )

        return locationPermissionState == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Checks whether location services is enabled.
     */
    fun checkServiceEnabled(): Int {
        val isEnabled = when {
            locationManager == null -> false
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.P -> locationManager.isLocationEnabled
            else -> {
                val gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                val networkEnabled =
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

                gpsEnabled || networkEnabled
            }
        }

        return if (isEnabled) 1 else 0
    }

    fun startRequestingLocation(sink: EventSink) {
        Log.d(TAG, "startRequestingLocation called")
        callbacks[STREAM_LOCATION_CALLBACK]?.dispose()
        callbacks[STREAM_LOCATION_CALLBACK] =
            StreamFlutterLocationCallback(sink, lopper, mainLooper, fusedLocationClient, settings)
    }

    fun cancelRequestingLocation() {
        Log.d(TAG, "cancelRequestingLocation called")
        callbacks[STREAM_LOCATION_CALLBACK]?.dispose()
    }


    fun dispose(error: ErrorData? = null) {
        Log.d(TAG, "dispose called")
        callbacks.values.takeWhile { it != null }.forEach {
            it?.dispose(error)
        }
    }

    fun getCurrentLocation(result: MethodChannel.Result) {
        callbacks[ONE_LOCATION_CALLBACK]?.dispose()
        callbacks[ONE_LOCATION_CALLBACK] =
            MethodFlutterLocationCallback(result, fusedLocationClient, settings)
    }
}
