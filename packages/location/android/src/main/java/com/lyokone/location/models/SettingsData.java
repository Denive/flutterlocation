package com.lyokone.location.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.location.LocationRequest;

import io.flutter.plugin.common.MethodCall;

public class SettingsData implements Parcelable {
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

    protected SettingsData(Parcel in) {
        if (in.readByte() == 0) {
            locationAccuracy = null;
        } else {
            locationAccuracy = in.readInt();
        }
        if (in.readByte() == 0) {
            updateIntervalMilliseconds = null;
        } else {
            updateIntervalMilliseconds = in.readLong();
        }
        if (in.readByte() == 0) {
            fastestUpdateIntervalMilliseconds = null;
        } else {
            fastestUpdateIntervalMilliseconds = in.readLong();
        }
        if (in.readByte() == 0) {
            distanceFilter = null;
        } else {
            distanceFilter = in.readFloat();
        }
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

    public static final Creator<SettingsData> CREATOR = new Creator<SettingsData>() {
        @Override
        public SettingsData createFromParcel(Parcel in) {
            return new SettingsData(in);
        }

        @Override
        public SettingsData[] newArray(int size) {
            return new SettingsData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (locationAccuracy == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(locationAccuracy);
        }
        if (updateIntervalMilliseconds == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(updateIntervalMilliseconds);
        }
        if (fastestUpdateIntervalMilliseconds == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(fastestUpdateIntervalMilliseconds);
        }
        if (distanceFilter == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeFloat(distanceFilter);
        }
    }

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
