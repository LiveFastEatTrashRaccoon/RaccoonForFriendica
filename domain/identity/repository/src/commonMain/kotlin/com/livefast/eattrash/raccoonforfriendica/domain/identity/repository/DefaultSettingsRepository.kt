package com.livefast.eattrash.raccoonforfriendica.domain.identity.repository

import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.toInt
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.toUIBarTheme
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.toUiFontFamily
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.toUiFontScale
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.toUiTheme
import com.livefast.eattrash.raccoonforfriendica.core.persistence.dao.SettingsDao
import com.livefast.eattrash.raccoonforfriendica.core.persistence.entities.SettingsEntity
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.SettingsModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.toDomainMaxLines
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.toImageLoadingMode
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.toInt
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.toMarkupMode
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.toNotificationMode
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.toSerialMaxLines
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.toUrlOpeningMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlin.time.Duration.Companion.seconds

internal class DefaultSettingsRepository(
    private val settingsDao: SettingsDao,
) : SettingsRepository {
    override val current = MutableStateFlow<SettingsModel?>(null)

    override fun changeCurrent(settings: SettingsModel) {
        current.update { settings }
    }

    override suspend fun get(accountId: Long) = settingsDao.getBy(accountId)?.toModel()

    override suspend fun create(settings: SettingsModel) {
        settingsDao.insert(settings.toEntity())
    }

    override suspend fun update(settings: SettingsModel) {
        settingsDao.update(settings.toEntity())
    }

    override suspend fun delete(settings: SettingsModel) {
        settingsDao.delete(settings.toEntity())
    }
}

private fun SettingsEntity.toModel() =
    SettingsModel(
        id = id,
        accountId = accountId,
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
        barTheme = barTheme.toUIBarTheme(),
    )

private fun SettingsModel.toEntity() =
    SettingsEntity(
        id = id,
        accountId = accountId,
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
        barTheme = barTheme.toInt(),
    )
