package com.lyokone.location.hadler

import android.util.Log
import com.lyokone.location.FlutterLocation
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.EventChannel.EventSink

class StreamHandlerImpl : EventChannel.StreamHandler {
    companion object {
        private const val TAG = "StreamHandlerImpl"
        private const val STREAM_CHANNEL_NAME = "lyokone/locationstream"
    }

    private var location: FlutterLocation? = null
    private var channel: EventChannel? = null

    /**
     * Registers this instance as a stream events handler on the given
     * `messenger`.
     */
    fun startListening(messenger: BinaryMessenger?) {
        if (channel != null) {
            Log.wtf(TAG, "Setting a method call handler before the last was disposed.")
            stopListening()
        }
        channel = EventChannel(messenger, STREAM_CHANNEL_NAME).apply {
            setStreamHandler(this@StreamHandlerImpl)
        }
    }

    /**
     * Clears this instance from listening to stream events.
     */
    fun stopListening() {
        if (channel == null) {
            Log.d(TAG, "Tried to stop listening when no MethodChannel had been initialized.")
            return
        }

        channel?.setStreamHandler(null)
        channel = null
    }

    override fun onListen(arguments: Any, eventsSink: EventSink) {
        location?.startRequestingLocation(sink = eventsSink)
    }

    override fun onCancel(arguments: Any) {
        location?.dispose()
    }

}
