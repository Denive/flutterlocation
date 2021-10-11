package com.lyokone.location.service

import android.app.Service
import android.content.Intent
import android.content.res.Configuration
import android.os.Binder
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.util.Log
import com.lyokone.location.FlutterLocation
import com.lyokone.location.model.ErrorData
import com.lyokone.location.model.LocationSettings
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodChannel


class FlutterLocationService : Service() {
    companion object {
        private const val TAG = "FlutterLocationService"

        private const val ONGOING_NOTIFICATION_ID = 75418
        private const val CHANNEL_ID = "flutter_location_channel_01"
    }

    private var _isForegroundLocationEnabled: Boolean = false

    var isForegroundLocationEnabled: Boolean
        get() = _isForegroundLocationEnabled
        set(value) {
            if (value)
                startForeground(ONGOING_NOTIFICATION_ID, notification.build())
            else {
                stopForeground(true)
            }

            _isForegroundLocationEnabled = value
        }

    lateinit var flutterLocation: FlutterLocation

    lateinit var serviceHandler: Handler

    lateinit var notification: BackgroundNotification

    private val binder: IBinder = LocalBinder()

    /**
     * Used to check whether the bound activity has really gone away and not unbound as part of an
     * orientation change. We create a foreground service notification only if the former takes
     * place.
     */
    private var changingConfiguration = false

    override fun onCreate() {
        super.onCreate()

        val handlerThread = HandlerThread(TAG)
        handlerThread.start()

        serviceHandler = Handler(handlerThread.looper)
        flutterLocation = FlutterLocation(this, handlerThread.looper, mainLooper)

        notification = BackgroundNotification(
            context = this,
            channelId = CHANNEL_ID,
            notificationId = ONGOING_NOTIFICATION_ID
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
// TODO
        Log.i(TAG, "Service started")
//
//
//        val startedFromNotification = intent?.getBooleanExtra(
//            EXTRA_STARTED_FROM_NOTIFICATION,
//            false
//        ) ?: false
//
//        // We got here because the user decided to remove location updates from the notification.
//
//        // We got here because the user decided to remove location updates from the notification.
//        if (startedFromNotification) {
//            removeLocationUpdates()
//            stopSelf()
//        }
//        // Tells the system to not try to recreate the service after it has been killed.

        return START_NOT_STICKY
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        changingConfiguration = true
    }

    override fun onBind(intent: Intent?): IBinder? {
        // Called when a client (MainActivity in case of this sample) comes to the foreground
        // and binds with this service. The service should cease to be a foreground service
        // when that happens.
//        Log.i(TAG, "in onBind()")
//        stopForeground(true)
//        changingConfiguration = false

        return binder
    }

    override fun onRebind(intent: Intent?) {
        // Called when a client (MainActivity in case of this sample) returns to the foreground
        // and binds once again with this service. The service should cease to be a foreground
        // service when that happens.
//        Log.i(TAG, "in onRebind()")
//        stopForeground(true)
//        changingConfiguration = false
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.i(TAG, "Last client unbound from service")

        // Called when the last client (MainActivity in case of this sample) unbinds from this
        // service. If this method is called due to a configuration change in MainActivity, we
        // do nothing. Otherwise, we make this service a foreground service.
//        if (!changingConfiguration && flutterLocation.isForegroundLocationEnabled) {
//            Log.i(TAG, "Starting foreground service")
//
//            startForeground(
//                ONGOING_NOTIFICATION_ID,
//                notification.build()
//            )
//        }

        return true // Ensures onRebind() is called when a client re-binds.
    }

    override fun onDestroy() {
        super.onDestroy()

        flutterLocation.dispose()
        serviceHandler.removeCallbacksAndMessages(null)
    }

    /**
     * Makes a request for location updates. Note that in this sample we merely log the
     * {@link SecurityException}.
     */
    fun requestLocationUpdates(sink: EventChannel.EventSink) {
        flutterLocation.startRequestingLocation(sink)
    }

    /**
     * Removes location updates. Note that in this sample we merely log the
     * [SecurityException].
     */
    fun removeLocationUpdates() {
        try {
            flutterLocation.dispose()
            stopSelf()
        } catch (unlikely: SecurityException) {
            flutterLocation.dispose(
                ErrorData(
                    "PERMISSION_DENIED",
                    "Lost location permission. Could not request updates.. $unlikely",
                )
            )

        }
    }

    fun changeSettings(locationSettings: LocationSettings) {
        flutterLocation.changeSettings(locationSettings)
    }

    fun checkPermissions(): Boolean {
        return flutterLocation.checkPermissions()
    }

    fun getCurrentLocation(result: MethodChannel.Result) {
        return flutterLocation.getCurrentLocation(result)
    }

    fun checkServiceEnabled(): Int {
        return flutterLocation.checkServiceEnabled()
    }

    fun changeNotificationOptions(options: NotificationOptions): Map<String, Any>? {
        notification.updateOptions(options, isForegroundLocationEnabled)

        return if (isForegroundLocationEnabled)
            mapOf("channelId" to CHANNEL_ID, "notificationId" to ONGOING_NOTIFICATION_ID)
        else
            null
    }

    fun cancelStream() {
        flutterLocation.cancelRequestingLocation()
    }

    inner class LocalBinder : Binder() {
        val service: FlutterLocationService
            get() = this@FlutterLocationService
    }
}

