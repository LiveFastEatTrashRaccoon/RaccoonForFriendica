package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.di

import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.SetupAccountUseCase
import org.koin.java.KoinJavaComponent

actual fun getSetupAccountUseCase(): SetupAccountUseCase {
    val res by KoinJavaComponent.inject<SetupAccountUseCase>(SetupAccountUseCase::class.java)
    return res
}
