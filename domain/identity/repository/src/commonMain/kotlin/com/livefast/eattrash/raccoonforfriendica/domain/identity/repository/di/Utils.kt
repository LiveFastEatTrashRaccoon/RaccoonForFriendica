package com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.livefast.eattrash.raccoonforfriendica.core.di.getByInjection
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AuthManager
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository

fun getSettingsRepository(): SettingsRepository = getByInjection(SettingsRepository::class)

@Composable
fun rememberSettingsRepository() = remember { getSettingsRepository() }

fun getAuthManager(): AuthManager = getByInjection(AuthManager::class)
