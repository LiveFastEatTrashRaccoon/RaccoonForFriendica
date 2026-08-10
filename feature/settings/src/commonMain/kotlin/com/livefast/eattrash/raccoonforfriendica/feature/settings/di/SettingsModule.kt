package com.livefast.eattrash.raccoonforfriendica.feature.settings.di

import com.livefast.eattrash.raccoonforfriendica.core.utils.permissions.PermissionControllerWrapper
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

data class SettingsViewModelArgs(val controller: PermissionControllerWrapper)

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.feature.settings")
class SettingsModule
