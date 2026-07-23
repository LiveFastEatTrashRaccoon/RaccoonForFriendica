package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.livefast.eattrash.raccoonforfriendica.core.di.getByInjection
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.ActiveAccountMonitor
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.EntryActionRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.SetupAccountUseCase

fun getActiveAccountMonitor(): ActiveAccountMonitor = getByInjection(ActiveAccountMonitor::class)

@Composable
fun rememberActiveAccountMonitor() = remember { getActiveAccountMonitor() }

fun getSetupAccountUseCase(): SetupAccountUseCase = getByInjection(SetupAccountUseCase::class)

@Composable
fun rememberSetupAccountUseCase() = remember { getSetupAccountUseCase() }

fun getEntryActionRepository(): EntryActionRepository = getByInjection(EntryActionRepository::class)

@Composable
fun rememberEntryActionRepository() = remember { getEntryActionRepository() }
