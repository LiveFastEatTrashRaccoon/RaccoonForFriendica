package com.livefast.eattrash.raccoonforfriendica.core.resources.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.livefast.eattrash.raccoonforfriendica.core.di.RootDI
import com.livefast.eattrash.raccoonforfriendica.core.resources.CoreResources
import org.kodein.di.instance

fun getCoreResources(): CoreResources {
    val res by RootDI.di.instance<CoreResources>()
    return res
}

@Composable
fun rememberCoreResources() = remember { getCoreResources() }
