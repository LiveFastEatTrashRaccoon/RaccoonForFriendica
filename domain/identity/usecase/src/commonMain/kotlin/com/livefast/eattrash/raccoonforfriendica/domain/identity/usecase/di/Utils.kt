package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.di

import androidx.compose.ui.platform.UriHandler
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.ActiveAccountMonitor
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.CustomUriHandler
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.EntryActionRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.SetupAccountUseCase
import org.koin.core.module.Module

expect fun getActiveAccountMonitor(): ActiveAccountMonitor

expect fun getSetupAccountUseCase(): SetupAccountUseCase

expect fun getCustomUriHandler(fallback: UriHandler): CustomUriHandler

expect fun getEntryActionRepository(): EntryActionRepository

expect val domainIdentityUseCaseNativeModule: Module
