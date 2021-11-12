package com.lyokone.location;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;

public class LifecycleObserver implements androidx.lifecycle.LifecycleObserver {
    private final String TAG = LifecycleObserver.class.getSimpleName();

    private final Activity activity;
    private final ServiceConnection serviceConnection;

    private boolean bound = false;

    private final ServiceConnection privateServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected");
            bound = true;
            serviceConnection.onServiceConnected(name, service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected");
            bound = false;
            serviceConnection.onServiceDisconnected(name);
        }
    };

    public LifecycleObserver(Activity activity, ServiceConnection serviceConnection) {
        this.activity = activity;
        this.serviceConnection = serviceConnection;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    void onStart() {
        activity.bindService(
                new Intent(activity, LocationUpdatesService.class),
                privateServiceConnection, Context.BIND_AUTO_CREATE
        );
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    void onStop() {
        if (bound) {
            // Unbind from the service. This signals to the service that this activity is no longer
            // in the foreground, and the service can respond by promoting itself to a foreground
            // service.
            activity.unbindService(privateServiceConnection);
            bound = false;
        }
    }
}
