package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.livefast.eattrash.raccoonforfriendica.core.di.RootDI
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.ActiveAccountMonitor
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.EntryActionRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.SetupAccountUseCase
import org.kodein.di.instance

fun getActiveAccountMonitor(): ActiveAccountMonitor {
    val res by RootDI.di.instance<ActiveAccountMonitor>()
    return res
}

@Composable
fun rememberActiveAccountMonitor() = remember { getActiveAccountMonitor() }

fun getSetupAccountUseCase(): SetupAccountUseCase {
    val res by RootDI.di.instance<SetupAccountUseCase>()
    return res
}

@Composable
fun rememberSetupAccountUseCase() = remember { getSetupAccountUseCase() }

fun getEntryActionRepository(): EntryActionRepository {
    val res by RootDI.di.instance<EntryActionRepository>()
    return res
}

@Composable
fun rememberEntryActionRepository() = remember { getEntryActionRepository() }
