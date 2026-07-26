package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.livefast.eattrash.raccoonforfriendica.core.di.DelicateDiApi
import com.livefast.eattrash.raccoonforfriendica.core.di.getByInjection
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.ActiveAccountMonitor
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.EntryActionRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.SetupAccountUseCase

@OptIn(DelicateDiApi::class)
@Composable
fun rememberActiveAccountMonitor() = remember { getByInjection(ActiveAccountMonitor::class) }

@OptIn(DelicateDiApi::class)
@Composable
fun rememberSetupAccountUseCase() = remember { getByInjection(SetupAccountUseCase::class) }

@OptIn(DelicateDiApi::class)
@Composable
fun rememberEntryActionRepository() = remember { getByInjection(EntryActionRepository::class) }
