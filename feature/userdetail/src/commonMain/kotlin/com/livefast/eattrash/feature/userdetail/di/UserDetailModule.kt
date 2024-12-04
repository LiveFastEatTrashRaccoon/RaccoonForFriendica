package com.livefast.eattrash.feature.userdetail.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.ksp.generated.module

@Module
@ComponentScan("com.livefast.eattrash.feature.userdetail")
internal class UserDetailModule

val featureUserDetailModule = UserDetailModule().module
