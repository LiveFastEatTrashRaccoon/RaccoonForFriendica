package com.livefast.eattrash.raccoonforfriendica.core.commonui.components.di

import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.FabNestedScrollConnection
import com.livefast.eattrash.raccoonforfriendica.core.di.RootDI
import org.kodein.di.instance

fun getFabNestedScrollConnection(): FabNestedScrollConnection {
    val res by RootDI.di.instance<FabNestedScrollConnection>()
    return res
}
