package com.livefast.eattrash.raccoonforfriendica.domain.identity.repository

import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.SettingsModel
import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {
    val current: StateFlow<SettingsModel?>

    suspend fun get(accountId: Long): SettingsModel?

    suspend fun create(settings: SettingsModel)

    suspend fun update(settings: SettingsModel)

    fun changeCurrent(settings: SettingsModel)
}
