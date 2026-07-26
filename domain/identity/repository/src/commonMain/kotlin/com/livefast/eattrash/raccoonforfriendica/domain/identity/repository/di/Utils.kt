package com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.livefast.eattrash.raccoonforfriendica.core.di.DelicateDiApi
import com.livefast.eattrash.raccoonforfriendica.core.di.getByInjection
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository

@OptIn(DelicateDiApi::class)
@Composable
fun rememberSettingsRepository() = remember { getByInjection(SettingsRepository::class) }
