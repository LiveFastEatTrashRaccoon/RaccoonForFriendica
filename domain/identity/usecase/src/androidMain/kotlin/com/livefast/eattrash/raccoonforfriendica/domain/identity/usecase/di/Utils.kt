package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.di

import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.ActiveAccountMonitor
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.EntryActionRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.SetupAccountUseCase
import org.koin.java.KoinJavaComponent

actual fun getActiveAccountMonitor(): ActiveAccountMonitor {
    val res by KoinJavaComponent.inject<ActiveAccountMonitor>(ActiveAccountMonitor::class.java)
    return res
}

actual fun getSetupAccountUseCase(): SetupAccountUseCase {
    val res by KoinJavaComponent.inject<SetupAccountUseCase>(SetupAccountUseCase::class.java)
    return res
}

actual fun getEntryActionRepository(): EntryActionRepository {
    val res by KoinJavaComponent.inject<EntryActionRepository>(EntryActionRepository::class.java)
    return res
}
