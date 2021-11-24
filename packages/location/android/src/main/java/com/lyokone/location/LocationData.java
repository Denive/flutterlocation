package com.lyokone.location;

import android.location.Location;
import android.os.Build;

import java.util.HashMap;
import java.util.Map;

public class LocationData {
    final Double latitude;
    final Double longitude;
    final Double accuracy;
    final Double verticalAccuracyMeters;
    final Double bearingAccuracyDegrees;
    final Double elapsedRealtimeUncertaintyNanos;
    final String provider;
    final Integer satelliteNumber;
    final Double elapsedRealtimeNanos;
    final Boolean isMock;
    final Double speed;
    final Double speedAccuracy;
    final Double bearing;
    final Double time;

    public LocationData(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        accuracy = (double) location.getAccuracy();
        provider = location.getProvider();
        satelliteNumber = location.getExtras() != null ? location.getExtras().getInt("satellites") : null;
        isMock = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 && location.isFromMockProvider();
        speed = (double) location.getSpeed();
        bearing = (double) location.getBearing();
        time = (double) location.getTime();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            verticalAccuracyMeters = (double) location.getVerticalAccuracyMeters();
            bearingAccuracyDegrees = (double) location.getBearingAccuracyDegrees();
        } else {
            verticalAccuracyMeters = null;
            bearingAccuracyDegrees = null;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            elapsedRealtimeNanos = (double) location.getElapsedRealtimeNanos();
        } else {
            elapsedRealtimeNanos = null;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            speedAccuracy = (double) location.getSpeedAccuracyMetersPerSecond();
        } else {
            speedAccuracy = null;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            elapsedRealtimeUncertaintyNanos = (double) location.getElapsedRealtimeUncertaintyNanos();
        } else {
            elapsedRealtimeUncertaintyNanos = null;
        }
    }

    public Map<String, Object> toMap() {
        final Map<String, Object> map = new HashMap();

        map.put("latitude", latitude);
        map.put("longitude", longitude);
        map.put("accuracy", accuracy);
        map.put("verticalAccuracy", verticalAccuracyMeters);
        map.put("headingAccuracy", bearingAccuracyDegrees);
        map.put("elapsedRealtimeUncertaintyNanos", elapsedRealtimeUncertaintyNanos);
        map.put("provider", provider);
        map.put("satelliteNumber", satelliteNumber);
        map.put("elapsedRealtimeNanos", elapsedRealtimeNanos);
        map.put("isMock", isMock ? 1 : 0);
        map.put("speed", speed);
        map.put("speedAccuracy", speedAccuracy);
        map.put("heading", bearing);
        map.put("time", time);

        return map;

    }
}



