package com.lyokone.location;

import android.util.SparseArray;

import androidx.annotation.NonNull;

import com.google.android.gms.location.LocationRequest;

import io.flutter.plugin.common.MethodCall;

public class SettingsData {
    private static final Integer DEFAULT_FLUTTER_LOCATION_ACCURACY = 4;
    private static final Integer DEFAULT_UPDATE_INTERVAL = 5000;
    private static final Double DEFAULT_DISTANCE_FILTER = 0.0;

    final public Integer locationAccuracy;
    final public Long updateIntervalMilliseconds;
    final public Long fastestUpdateIntervalMilliseconds;
    final public Float distanceFilter;


    private SettingsData(
            final Integer locationAccuracy,
            final Integer updateIntervalMilliseconds,
            final Double distanceFilter
    ) {
        SparseArray<Integer> mapFlutterAccuracy = new SparseArray<Integer>() {
            {
                put(0, LocationRequest.PRIORITY_NO_POWER);
                put(1, LocationRequest.PRIORITY_LOW_POWER);
                put(2, LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                put(3, LocationRequest.PRIORITY_HIGH_ACCURACY);
                put(4, LocationRequest.PRIORITY_HIGH_ACCURACY);
                put(5, LocationRequest.PRIORITY_LOW_POWER);
            }
        };

        this.locationAccuracy = mapFlutterAccuracy.get(locationAccuracy != null ? locationAccuracy : DEFAULT_FLUTTER_LOCATION_ACCURACY);
        this.updateIntervalMilliseconds = (updateIntervalMilliseconds != null ? updateIntervalMilliseconds : DEFAULT_UPDATE_INTERVAL).longValue();
        this.fastestUpdateIntervalMilliseconds = this.updateIntervalMilliseconds / 2;
        this.distanceFilter = (distanceFilter != null ? distanceFilter : DEFAULT_DISTANCE_FILTER).floatValue();
    }


    public static SettingsData fromCall(MethodCall call) {
        Integer locationAccuracy = null;
        Integer updateIntervalMilliseconds = null;
        Double distanceFilter = null;

        try {
            locationAccuracy = call.argument("accuracy");
            updateIntervalMilliseconds = call.argument("interval");
            distanceFilter = call.argument("distanceFilter");
        } catch (ClassCastException ignore) {
        }

        return new SettingsData(locationAccuracy, updateIntervalMilliseconds, distanceFilter);
    }


    public static SettingsData blank() {
        return new SettingsData(DEFAULT_FLUTTER_LOCATION_ACCURACY, DEFAULT_UPDATE_INTERVAL, DEFAULT_DISTANCE_FILTER);
    }

    @NonNull
    @Override
    public String toString() {
        return "SettingsData{" +
                "locationAccuracy=" + locationAccuracy +
                ", updateIntervalMilliseconds=" + updateIntervalMilliseconds +
                ", fastestUpdateIntervalMilliseconds=" + fastestUpdateIntervalMilliseconds +
                ", distanceFilter=" + distanceFilter +
                '}';
    }
}
