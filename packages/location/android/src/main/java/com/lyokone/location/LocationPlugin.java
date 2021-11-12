package com.lyokone.location;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.Task;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.embedding.engine.plugins.lifecycle.HiddenLifecycleReference;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.PluginRegistry;

/**
 * LocationPlugin
 */
public class LocationPlugin implements FlutterPlugin, ActivityAware, MethodChannel.MethodCallHandler, PluginRegistry.RequestPermissionsResultListener, PluginRegistry.ActivityResultListener {
    private static final String TAG = "LocationPlugin";

    private static final String METHOD_CHANNEL_NAME = "lyokone/location";
    private static final String EVENT_CHANNEL_NAME = "lyokone/locationstream";

    @Nullable
    private MethodChannel methodChannel;
    @Nullable
    private EventChannel eventChannel;

    @Nullable
    private ActivityPluginBinding activityBinding;
    @Nullable
    private LocationManager locationManager;

    @Nullable
    private LocationUpdatesService service;

    @Nullable
    private LifecycleObserver lifecycleObserver;

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            Log.d(TAG, "Service connected: " + name);

            service = ((LocationUpdatesService.LocalBinder) binder).getService();
            if (eventChannel != null) {
                eventChannel.setStreamHandler(service);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "Service disconnected:" + name);

            if (eventChannel != null) {
                eventChannel.setStreamHandler(null);
            }

            service = null;
        }
    };

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        methodChannel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), METHOD_CHANNEL_NAME);
        methodChannel.setMethodCallHandler(this);
        eventChannel = new EventChannel(flutterPluginBinding.getBinaryMessenger(), EVENT_CHANNEL_NAME);
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        methodChannel = null;
        eventChannel = null;
    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        activityBinding = binding;
        lifecycleObserver = new LifecycleObserver(binding.getActivity(), serviceConnection);

        locationManager = (LocationManager) binding.getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        ((HiddenLifecycleReference) activityBinding.getLifecycle()).getLifecycle().addObserver(lifecycleObserver);
    }

    @Override
    public void onDetachedFromActivity() {
        if (activityBinding != null) {
            if (lifecycleObserver != null) {
                ((HiddenLifecycleReference) activityBinding.getLifecycle()).getLifecycle().removeObserver(lifecycleObserver);
            }

            activityBinding.removeRequestPermissionsResultListener(this);
            activityBinding.removeActivityResultListener(this);
        }

        lifecycleObserver = null;
        activityBinding = null;
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        onDetachedFromActivity();
    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
        onAttachedToActivity(binding);
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        switch (call.method) {
            case "getLocation":
                onGetLocation(result);
                break;
            case "serviceEnabled":
                checkServiceEnabled(result);
                break;
            case "requestService":
                requestService(result);
                break;
            case "isBackgroundModeEnabled":
                isBackgroundModeEnabled(result);
                break;
            case "enableBackgroundMode":
                enableBackgroundMode(call, result);
                break;
            case "changeNotificationOptions":
                changeNotificationOptions(call, result);
                break;

            // TODO add later
            case "changeSettings":
                changeSettings(call, result);
                break;
            case "hasPermission":
                onHasPermission(result);
                break;
            case "requestPermission":
                onRequestPermission(result);
                break;
            default:
                result.notImplemented();
                break;
        }
    }


    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        return false;
    }

    @Override
    public boolean onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        return false;
    }

    private void onGetLocation(MethodChannel.Result result) {

    }

    public void changeSettings(MethodCall call, MethodChannel.Result result) {
//        this.locationAccuracy = newLocationAccuracy;
//        this.updateIntervalMilliseconds = updateIntervalMilliseconds;
//        this.fastestUpdateIntervalMilliseconds = fastestUpdateIntervalMilliseconds;
//        this.distanceFilter = distanceFilter;
//
//        createLocationCallback();
//        createLocationRequest();
//        buildLocationSettingsRequest();
//        startRequestingLocation();
    }

    private void onRequestPermission(MethodChannel.Result result) {

    }

    private void onHasPermission(MethodChannel.Result result) {

    }

    private void enableBackgroundMode(MethodCall call, MethodChannel.Result result) {

    }

    private void isBackgroundModeEnabled(MethodChannel.Result result) {

    }

    private void changeNotificationOptions(MethodCall call, MethodChannel.Result result) {
    }

    private void checkServiceEnabled(final @NonNull MethodChannel.Result result) {
        if (locationManager != null) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    result.success(locationManager.isLocationEnabled() ? 1 : 0);
                } else {
                    boolean gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    boolean network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                    result.success((gps_enabled || network_enabled) && service != null ? 1 : 0);
                }
            } catch (IllegalArgumentException exception) {
                result.error("SERVICE_STATUS_ERROR", "Location service status couldn't be determined", null);
            }
        } else {
            result.success(0);
        }
    }

    private void requestService(final @NonNull MethodChannel.Result result) {
        if (activityBinding != null) {
            final Activity activity = activityBinding.getActivity();

            LocationRequest locationRequest = LocationRequest.create();
            // TODO SET PRIORITY
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            Task<LocationSettingsResponse> task = LocationServices.getSettingsClient(activity)
                    .checkLocationSettings(builder.build());

            task.addOnSuccessListener((response) -> {
                LocationSettingsStates states = response.getLocationSettingsStates();

                result.success(states != null && states.isLocationPresent() && service != null ? 1 : 0);
            });

            task.addOnFailureListener((e) -> {
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException rae = (ResolvableApiException) e;
                    int statusCode = rae.getStatusCode();
                    switch (statusCode) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
//                            try {
//                                // Show the dialog by calling startResolutionForResult(), and check the
//                                // result in onActivityResult().
//                                rae.startResolutionForResult(activity, GPS_ENABLE_REQUEST);
//                            } catch (IntentSender.SendIntentException sie) {
//                                result.error("SERVICE_STATUS_ERROR", "Could not resolve location request",
//                                        null);
//                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            result.error("SERVICE_STATUS_DISABLED",
                                    "Failed to get location. Location services disabled", null);
                            break;
                    }
                } else {
                    // This should not happen according to Android documentation but it has been
                    // observed on some phones.
                    result.error("SERVICE_STATUS_ERROR", "Unexpected error type received", null);
                }
            });

        } else {
            result.error("MISSING_ACTIVITY", "You should not requestService activation outside of an activity.", null);
            throw new ActivityNotFoundException();
        }
    }
}
