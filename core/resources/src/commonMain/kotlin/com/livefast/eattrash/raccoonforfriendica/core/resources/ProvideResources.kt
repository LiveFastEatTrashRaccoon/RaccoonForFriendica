package com.livefast.eattrash.raccoonforfriendica.core.resources

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf

val LocalResources: ProvidableCompositionLocal<CoreResources> =
    staticCompositionLocalOf {
        error("CompositionLocal CoreResources not found")
    }

@Composable
fun ProvideResources(resources: CoreResources, content: @Composable () -> Unit) {
    CompositionLocalProvider(
        value = LocalResources provides resources,
        content = content,
    )
}
