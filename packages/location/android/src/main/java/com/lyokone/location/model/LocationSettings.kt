package com.lyokone.location.model

import com.google.android.gms.location.LocationRequest
import io.flutter.plugin.common.MethodCall

data class LocationSettings(
    val locationAccuracy: Int,
    val updateIntervalMilliseconds: Long,
    val fastestUpdateIntervalMilliseconds: Long,
    val distanceFilter: Float
) {

    companion object {
        private val FLUTTER_ACCURACY: Map<Int, Int> = mapOf(
            Pair(0, LocationRequest.PRIORITY_NO_POWER),
            Pair(1, LocationRequest.PRIORITY_LOW_POWER),
            Pair(2, LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY),
            Pair(3, LocationRequest.PRIORITY_HIGH_ACCURACY),
            Pair(4, LocationRequest.PRIORITY_HIGH_ACCURACY),
            Pair(5, LocationRequest.PRIORITY_LOW_POWER),
        )

        private const val DEFAULT_UPDATE_INTERVAL_IN_MILLISECONDS: Long = 5000L
        private const val DEFAULT_DISTANCE_FILTER: Float = 0f
        private const val DEFAULT_LOCATION_ACCURACY: Int = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    constructor() : this(
        locationAccuracy = DEFAULT_LOCATION_ACCURACY,
        updateIntervalMilliseconds = DEFAULT_UPDATE_INTERVAL_IN_MILLISECONDS,
        fastestUpdateIntervalMilliseconds = DEFAULT_UPDATE_INTERVAL_IN_MILLISECONDS / 2,
        distanceFilter = DEFAULT_DISTANCE_FILTER
    )

    constructor(call: MethodCall) : this(
        locationAccuracy = call.argument<Int>("accuracy")?.let {
            FLUTTER_ACCURACY[it] ?: DEFAULT_LOCATION_ACCURACY
        } ?: DEFAULT_LOCATION_ACCURACY,
        updateIntervalMilliseconds = call.argument<Long>("interval")
            ?: DEFAULT_UPDATE_INTERVAL_IN_MILLISECONDS,
        fastestUpdateIntervalMilliseconds = (call.argument<Long>("interval")
            ?: DEFAULT_UPDATE_INTERVAL_IN_MILLISECONDS) / 2,
        distanceFilter = call.argument<Float>("distanceFilter")
            ?: DEFAULT_DISTANCE_FILTER
    )
}