package com.livefast.eattrash.raccoonforfriendica.domain.identity.repository

import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.toInt
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.toUiFontFamily
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.toUiTheme
import com.livefast.eattrash.raccoonforfriendica.core.persistence.dao.SettingsDao
import com.livefast.eattrash.raccoonforfriendica.core.persistence.entities.SettingsEntity
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.SettingsModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

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
}

private fun SettingsEntity.toModel() =
    SettingsModel(
        id = id,
        accountId = accountId,
        lang = lang,
        theme = theme.toUiTheme(),
        fontFamily = fontFamily.toUiFontFamily(),
        dynamicColors = dynamicColors,
        customSeedColor = customSeedColor,
    )

private fun SettingsModel.toEntity() =
    SettingsEntity(
        id = id,
        accountId = accountId,
        lang = lang,
        theme = theme.toInt(),
        fontFamily = fontFamily.toInt(),
        dynamicColors = dynamicColors,
        customSeedColor = customSeedColor,
    )
