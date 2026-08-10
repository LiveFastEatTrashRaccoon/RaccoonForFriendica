package com.livefast.eattrash.raccoonforfriendica.feature.profile.di

import com.livefast.eattrash.raccoonforfriendica.feature.profile.ProfileMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.profile.ProfileViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.profile.edit.EditProfileMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.profile.edit.EditProfileViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.profile.loginintro.LoginIntroMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.profile.loginintro.LoginIntroViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.profile.myaccount.MyAccountMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.profile.myaccount.MyAccountViewModel
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.plugin.module.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.viewModel

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.feature.profile")
class ProfileModule
