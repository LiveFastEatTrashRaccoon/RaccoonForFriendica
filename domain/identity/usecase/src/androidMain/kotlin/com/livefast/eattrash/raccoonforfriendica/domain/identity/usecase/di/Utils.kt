package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.di

import androidx.compose.ui.platform.UriHandler
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.ActiveAccountMonitor
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.CustomUriHandler
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.EntryActionRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.SetupAccountUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.pullnotifications.DefaultPullNotificationChecker
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.pullnotifications.PullNotificationChecker
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent

actual fun getActiveAccountMonitor(): ActiveAccountMonitor {
    val res by KoinJavaComponent.inject<ActiveAccountMonitor>(ActiveAccountMonitor::class.java)
    return res
}

actual fun getSetupAccountUseCase(): SetupAccountUseCase {
    val res by KoinJavaComponent.inject<SetupAccountUseCase>(SetupAccountUseCase::class.java)
    return res
}

actual fun getCustomUriHandler(fallback: UriHandler): CustomUriHandler {
    val inject =
        KoinJavaComponent.inject<CustomUriHandler>(
            CustomUriHandler::class.java,
            parameters = {
                parametersOf(fallback)
            },
        )
    val res by inject
    return res
}

actual fun getEntryActionRepository(): EntryActionRepository {
    val res by KoinJavaComponent.inject<EntryActionRepository>(EntryActionRepository::class.java)
    return res
}

actual val domainIdentityUseCaseNativeModule =
    module {
        single<PullNotificationChecker> {
            DefaultPullNotificationChecker(
                context = get(),
            )
    }
}
