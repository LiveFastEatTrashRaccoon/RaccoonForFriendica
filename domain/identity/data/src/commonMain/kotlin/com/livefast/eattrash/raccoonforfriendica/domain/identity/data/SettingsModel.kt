package com.livefast.eattrash.raccoonforfriendica.domain.identity.data

import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.TimelineLayout
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.UiBarTheme
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.UiFontFamily
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.UiFontScale
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.UiTheme
import kotlin.time.Duration

data class SettingsModel(
    val id: Long = 0,
    val accountId: Long = 0,
    val lang: String = "en",
    val theme: UiTheme = UiTheme.Default,
    val fontFamily: UiFontFamily = UiFontFamily.Default,
    val fontScale: UiFontScale = UiFontScale.Normal,
    val dynamicColors: Boolean = false,
    val customSeedColor: Int? = null,
    val defaultTimelineType: Int = 0,
    val defaultTimelineId: String? = null,
    val includeNsfw: Boolean = false,
    val blurNsfw: Boolean = true,
    val urlOpeningMode: UrlOpeningMode = UrlOpeningMode.External,
    val defaultPostVisibility: Int = 0,
    val defaultReplyVisibility: Int = 1,
    val excludeRepliesFromTimeline: Boolean = false,
    val openGroupsInForumModeByDefault: Boolean = true,
    val markupMode: MarkupMode = MarkupMode.PlainText,
    val maxPostBodyLines: Int = Int.MAX_VALUE,
    val notificationMode: NotificationMode = NotificationMode.Disabled,
    val pullNotificationCheckInterval: Duration? = null,
    val autoloadImages: ImageLoadingMode = ImageLoadingMode.Always,
    val hideNavigationBarWhileScrolling: Boolean = true,
    val barTheme: UiBarTheme = UiBarTheme.Transparent,
    val timelineLayout: TimelineLayout = TimelineLayout.Full,
    val replyDepth: Int = 1,
)

fun Int.toDomainMaxLines(): Int = when (this) {
    // interpret null values as unlimited
    0 -> Int.MAX_VALUE
    else -> this
}

fun Int.toSerialMaxLines(): Int = when (this) {
    Int.MAX_VALUE -> 0
    else -> this
}
