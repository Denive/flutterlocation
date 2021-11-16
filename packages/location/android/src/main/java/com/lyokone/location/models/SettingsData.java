package com.lyokone.location.models;

import com.google.android.gms.location.LocationRequest;

import io.flutter.plugin.common.MethodCall;

public class SettingsData {
    private static final Integer DEFAULT_LOCATION_ACCURACY = LocationRequest.PRIORITY_HIGH_ACCURACY;
    private static final Long DEFAULT_UPDATE_INTERVAL = 5000L;
    private static final Float DEFAULT_DISTANCE_FILTER = 0f;

    final public Integer locationAccuracy;
    final public Long updateIntervalMilliseconds;
    final public Long fastestUpdateIntervalMilliseconds;
    final public Float distanceFilter;

    private SettingsData(
            final Integer locationAccuracy,
            final Long updateIntervalMilliseconds,
            final Float distanceFilter
    ) {
        this.locationAccuracy = locationAccuracy != null ? locationAccuracy : DEFAULT_LOCATION_ACCURACY;
        this.updateIntervalMilliseconds = updateIntervalMilliseconds != null ? updateIntervalMilliseconds : DEFAULT_UPDATE_INTERVAL;
        this.fastestUpdateIntervalMilliseconds = this.updateIntervalMilliseconds / 2;
        this.distanceFilter = distanceFilter != null ? distanceFilter : DEFAULT_DISTANCE_FILTER;
    }

    public static SettingsData fromCall(MethodCall call) {
        Integer locationAccuracy = null;
        Long updateIntervalMilliseconds = null;
        Float distanceFilter = null;

        try {
            locationAccuracy = call.argument("accuracy");
            updateIntervalMilliseconds = (Long) call.argument("interval");
            distanceFilter = (Float) call.argument("distanceFilter");
        } catch (ClassCastException ignore) {
        }

        return new SettingsData(locationAccuracy, updateIntervalMilliseconds, distanceFilter);
    }


    public static SettingsData blank() {
        return new SettingsData(DEFAULT_LOCATION_ACCURACY, DEFAULT_UPDATE_INTERVAL, DEFAULT_DISTANCE_FILTER);
    }
}
