package com.livefast.eattrash.raccoonforfriendica.domain.identity.data

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
    val defaultReplyVisibility: Int = 0,
    val excludeRepliesFromTimeline: Boolean = false,
    val openGroupsInForumModeByDefault: Boolean = true,
    val markupMode: MarkupMode = MarkupMode.HTML,
    val maxPostBodyLines: Int = Int.MAX_VALUE,
    val pullNotificationCheckInterval: Duration? = null,
    val autoloadImages: Boolean = true,
)
