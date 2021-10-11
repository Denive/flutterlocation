package com.lyokone.location.service

import android.graphics.Color
import io.flutter.plugin.common.MethodCall

data class NotificationOptions(
    val channelName: String,
    val title: String,
    val iconName: String,
    val subtitle: String?,
    val description: String?,
    val color: Int?,
    val onTapBringToFront: Boolean
) {
    companion object {
        private const val DEFAULT_CHANNEL_NAME: String = "Location background service"
        private const val DEFAULT_NOTIFICATION_TITLE: String = "Location background service running"
        const val DEFAULT_NOTIFICATION_ICON_NAME: String = "navigation_empty_icon"
    }

    constructor(call: MethodCall) : this(
        channelName = call.argument<String>("channelName") ?: DEFAULT_CHANNEL_NAME,
        title = call.argument<String>("title") ?: DEFAULT_NOTIFICATION_TITLE,
        iconName = call.argument<String>("iconName") ?: DEFAULT_NOTIFICATION_ICON_NAME,
        subtitle = call.argument<String>("subtitle"),
        description = call.argument<String>("description"),
        color = call.argument<String>("color")?.let {
            Color.parseColor(it)
        },
        onTapBringToFront = call.argument<Boolean>("onTapBringToFront") ?: false,
    )

    constructor() : this(
        channelName = DEFAULT_CHANNEL_NAME,
        title = DEFAULT_NOTIFICATION_TITLE,
        iconName = DEFAULT_NOTIFICATION_ICON_NAME,
        subtitle = null,
        description = null,
        color = null,
        onTapBringToFront = false,
    )
}