package com.livefast.eattrash.raccoonforfriendica.core.commonui.components.di

import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.FabNestedScrollConnection
import org.koin.java.KoinJavaComponent

actual fun getFabNestedScrollConnection(): FabNestedScrollConnection {
    val res: FabNestedScrollConnection by KoinJavaComponent.inject(FabNestedScrollConnection::class.java)
    return res
}
