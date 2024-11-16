package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase

import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.toInt
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.toUiFontFamily
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.toUiFontScale
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.toUiTheme
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.SettingsModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.toDomainMaxLines
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.toImageLoadingMode
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.toInt
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.toMarkupMode
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.toNotificationMode
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.toSerialMaxLines
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.toUrlOpeningMode
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.seconds

internal val jsonSerializationStrategy =
    Json {
        encodeDefaults = true
        prettyPrint = true
    }

@Serializable
internal data class SerializableSettings(
    val lang: String = "en",
    val theme: Int = 0,
    val fontFamily: Int = 0,
    val fontScale: Int = 0,
    val dynamicColors: Boolean = false,
    val customSeedColor: Int? = null,
    val defaultTimelineType: Int = 0,
    val defaultTimelineId: String? = null,
    val includeNsfw: Boolean = false,
    val blurNsfw: Boolean = true,
    val urlOpeningMode: Int = 0,
    val defaultPostVisibility: Int = 0,
    val defaultReplyVisibility: Int = 1,
    val excludeRepliesFromTimeline: Boolean = false,
    val openGroupsInForumModeByDefault: Boolean = true,
    val markupMode: Int = 0,
    val maxPostBodyLines: Int = Int.MAX_VALUE,
    val notificationMode: Int = 0,
    val pullNotificationCheckInterval: Long? = null,
    val autoloadImages: Int = 1,
    val hideNavigationBarWhileScrolling: Boolean = true,
)

internal fun SerializableSettings.toModel() =
    SettingsModel(
        lang = lang,
        theme = theme.toUiTheme(),
        fontFamily = fontFamily.toUiFontFamily(),
        fontScale = fontScale.toUiFontScale(),
        dynamicColors = dynamicColors,
        customSeedColor = customSeedColor,
        defaultTimelineType = defaultTimelineType,
        blurNsfw = blurNsfw,
        includeNsfw = includeNsfw,
        urlOpeningMode = urlOpeningMode.toUrlOpeningMode(),
        defaultPostVisibility = defaultPostVisibility,
        defaultReplyVisibility = defaultReplyVisibility,
        excludeRepliesFromTimeline = excludeRepliesFromTimeline,
        openGroupsInForumModeByDefault = openGroupsInForumModeByDefault,
        markupMode = markupMode.toMarkupMode(),
        maxPostBodyLines = maxPostBodyLines.toDomainMaxLines(),
        defaultTimelineId = defaultTimelineId,
        notificationMode = notificationMode.toNotificationMode(),
        pullNotificationCheckInterval = pullNotificationCheckInterval?.seconds,
        autoloadImages = autoloadImages.toImageLoadingMode(),
        hideNavigationBarWhileScrolling = hideNavigationBarWhileScrolling,
    )

internal fun SettingsModel.toData() =
    SerializableSettings(
        lang = lang,
        theme = theme.toInt(),
        fontFamily = fontFamily.toInt(),
        fontScale = fontScale.toInt(),
        dynamicColors = dynamicColors,
        customSeedColor = customSeedColor,
        defaultTimelineType = defaultTimelineType,
        includeNsfw = includeNsfw,
        blurNsfw = blurNsfw,
        urlOpeningMode = urlOpeningMode.toInt(),
        defaultPostVisibility = defaultPostVisibility,
        defaultReplyVisibility = defaultReplyVisibility,
        excludeRepliesFromTimeline = excludeRepliesFromTimeline,
        openGroupsInForumModeByDefault = openGroupsInForumModeByDefault,
        markupMode = markupMode.toInt(),
        maxPostBodyLines = maxPostBodyLines.toSerialMaxLines(),
        defaultTimelineId = defaultTimelineId,
        notificationMode = notificationMode.toInt(),
        pullNotificationCheckInterval = pullNotificationCheckInterval?.inWholeSeconds,
        autoloadImages = autoloadImages.toInt(),
        hideNavigationBarWhileScrolling = hideNavigationBarWhileScrolling,
    )
