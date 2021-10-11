package com.lyokone.location.model

import android.os.Looper
import com.google.android.gms.location.*
import com.google.android.gms.tasks.CancellationTokenSource
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodChannel


sealed class FlutterLocationCallback(
    val fusedLocationClient: FusedLocationProviderClient
) {

    abstract fun updateSettings(settings: LocationSettings)

    abstract fun dispose(errorData: ErrorData? = null)
}

class MethodFlutterLocationCallback(
    private val result: MethodChannel.Result,
    fusedLocationClient: FusedLocationProviderClient,
    settings: LocationSettings
) : FlutterLocationCallback(fusedLocationClient) {
    private val cts = CancellationTokenSource()

    private var isSend = false;

    init {
        requestLocation(settings.locationAccuracy)
        fusedLocationClient.getCurrentLocation(settings.locationAccuracy, cts.token)
            .addOnSuccessListener {
                val location = LocationData(it)

                result.success(location.toMap())
                isSend = true
            }.addOnFailureListener {
                result.error("GET_LOCATION_ERROR", it.localizedMessage, it)
                isSend = true
            }
    }


    override fun updateSettings(settings: LocationSettings) {}

    override fun dispose(errorData: ErrorData? = null) {
        if (!isSend) {
            if (errorData == null) {
                result.success(null)
            } else {
                result.error(errorData.errorCode, errorData.errorMessage, null)
            }

            cts.cancel()
        }
    }

    private fun requestLocation(locationAccuracy: Int) {
        fusedLocationClient.getCurrentLocation(locationAccuracy, cts.token).addOnSuccessListener {
            val location = LocationData(it)

            result.success(location.toMap())
        }.addOnFailureListener {
            result.error("GET_LOCATION_ERROR", it.localizedMessage, it)
        }
    }
}

class StreamFlutterLocationCallback(
    private val sink: EventChannel.EventSink,
    private val looper: Looper,
    fusedLocationClient: FusedLocationProviderClient,
    settings: LocationSettings
) : FlutterLocationCallback(fusedLocationClient) {

    private val locationCallback = createLocationCallback(sink)

    init {
        requestLocation(settings)
    }

    override fun updateSettings(settings: LocationSettings) {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        requestLocation(settings)
    }

    override fun dispose(errorData: ErrorData?) {
        if (errorData != null)
            sink.error(errorData.errorCode, errorData.errorMessage, null)

        sink.endOfStream()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun requestLocation(settings: LocationSettings) {
        try {
            fusedLocationClient.requestLocationUpdates(
                createLocationRequest(settings),
                locationCallback,
                looper
            )
        } catch (unlikely: SecurityException) {
            sink.error(
                "PERMISSION_DENIED",
                "Lost location permission. Could not request updates.. $unlikely",
                null
            )
            sink.endOfStream()
        }
    }

    /**
     * Sets up the location request. Android has two location request settings:
     */
    private fun createLocationRequest(settings: LocationSettings): LocationRequest {
        return LocationRequest.create()
            .setInterval(settings.updateIntervalMilliseconds)
            .setFastestInterval(settings.fastestUpdateIntervalMilliseconds)
            .setPriority(settings.locationAccuracy)
            .setSmallestDisplacement(settings.distanceFilter)
    }

    private fun createLocationCallback(sink: EventChannel.EventSink): LocationCallback {
        return object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)

                val location = LocationData(locationResult.lastLocation)

                sink.success(location.toMap())
            }

            override fun onLocationAvailability(locationAvailabity: LocationAvailability) {
                super.onLocationAvailability(locationAvailabity)

                if (!locationAvailabity.isLocationAvailable)
                    sink.error(
                        "LOCATION_UNAVAILABLE",
                        "You should check enable status for location service.",
                        null
                    )
            }
        }
    }
}
