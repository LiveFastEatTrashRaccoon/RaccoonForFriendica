package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.di

import androidx.compose.ui.platform.UriHandler
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.ActiveAccountMonitor
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.CustomUriHandler
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.EntryActionRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.SetupAccountUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.pullnotifications.DefaultPullNotificationChecker
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.pullnotifications.PullNotificationChecker
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

actual fun getActiveAccountMonitor(): ActiveAccountMonitor = DomainIdentityUseCaseDiHelper.activeAccountMonitor

actual fun getSetupAccountUseCase(): SetupAccountUseCase = DomainIdentityUseCaseDiHelper.setupAccountUseCase

actual fun getCustomUriHandler(fallback: UriHandler): CustomUriHandler = DomainIdentityUseCaseDiHelper.getCustomUriHandler(fallback)

actual fun getEntryActionRepository(): EntryActionRepository = DomainIdentityUseCaseDiHelper.entryActionRepository

actual val domainIdentityUseCaseNativeModule =
    module {
        single<PullNotificationChecker> {
            DefaultPullNotificationChecker()
    }
}

internal object DomainIdentityUseCaseDiHelper : KoinComponent {
    val activeAccountMonitor: ActiveAccountMonitor by inject()
    val setupAccountUseCase: SetupAccountUseCase by inject()
    val entryActionRepository: EntryActionRepository by inject()

    fun getCustomUriHandler(default: UriHandler): CustomUriHandler {
        val res by inject<CustomUriHandler>(
            parameters = {
                parametersOf(default)
            },
        )
        return res
    }
}
