package com.lyokone.location.model

import android.location.Location
import android.os.Build

data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Double,
    val verticalAccuracyMeters: Double?,
    val bearingAccuracyDegrees: Double?,
    val elapsedRealtimeUncertaintyNanos: Double?,
    val provider: String,
    val satelliteNumber: Int?,
    val elapsedRealtimeNanos: Double?,
    val isMock: Boolean,
    val speed: Double,
    val speedAccuracy: Double?,
    val bearing: Double,
    val time: Double,
) {
    constructor(location: Location) : this(
        latitude = location.latitude,
        longitude = location.longitude,
        accuracy = location.accuracy.toDouble(),
        verticalAccuracyMeters = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) location.verticalAccuracyMeters.toDouble() else null,
        bearingAccuracyDegrees = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) location.bearingAccuracyDegrees.toDouble() else null,
        elapsedRealtimeUncertaintyNanos = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) location.elapsedRealtimeUncertaintyNanos else null,
        provider = location.provider,
        satelliteNumber = location.extras?.getInt("satellites"),
        elapsedRealtimeNanos = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) location.elapsedRealtimeNanos.toDouble() else null,
        isMock = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 && location.isFromMockProvider,
        speed = location.speed.toDouble(),
        speedAccuracy = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) location.speedAccuracyMetersPerSecond.toDouble() else null,
        bearing = location.bearing.toDouble(),
        time = location.time.toDouble()
    )


    fun toMap(): Map<String, Any?> {
        return mapOf<String, Any?>(
            "latitude" to latitude,
            "longitude" to longitude,
            "accuracy" to accuracy,
            "verticalAccuracy" to verticalAccuracyMeters,
            "headingAccuracy" to bearingAccuracyDegrees,
            "elapsedRealtimeUncertaintyNanos" to elapsedRealtimeUncertaintyNanos,
            "provider" to provider,
            "satelliteNumber" to satelliteNumber,
            "elapsedRealtimeNanos" to elapsedRealtimeNanos,
            "isMock" to if (isMock) 1 else 0,
            "speed" to speed,
            "speed_accuracy" to speedAccuracy,
            "heading" to bearing,
            "time" to time
        )
    }
}


