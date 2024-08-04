package com.github.akesiseli.raccoonforfriendica.core.resources.di

import com.github.akesiseli.raccoonforfriendica.core.resources.CoreResources
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual fun getCoreResources(): CoreResources = CoreResourcesDiHelper.coreResources

internal object CoreResourcesDiHelper : KoinComponent {
    val coreResources: CoreResources by inject()
}
