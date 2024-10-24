package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.di

import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.ActiveAccountMonitor
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.EntryActionRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.SetupAccountUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual fun getActiveAccountMonitor(): ActiveAccountMonitor = DomainIdentityUseCaseDiHelper.activeAccountMonitor

actual fun getSetupAccountUseCase(): SetupAccountUseCase = DomainIdentityUseCaseDiHelper.setupAccountUseCase

actual fun getEntryActionRepository(): EntryActionRepository = DomainIdentityUseCaseDiHelper.entryActionRepository

internal object DomainIdentityUseCaseDiHelper : KoinComponent {
    val activeAccountMonitor: ActiveAccountMonitor by inject()
    val setupAccountUseCase: SetupAccountUseCase by inject()
    val entryActionRepository: EntryActionRepository by inject()
}
