package com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.livefast.eattrash.raccoonforfriendica.core.di.RootDI
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AuthManager
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import org.kodein.di.instance

fun getSettingsRepository(): SettingsRepository {
    val res by RootDI.di.instance<SettingsRepository>()
    return res
}

@Composable
fun rememberSettingsRepository() = remember { getSettingsRepository() }

fun getAuthManager(): AuthManager {
    val res by RootDI.di.instance<AuthManager>()
    return res
}
