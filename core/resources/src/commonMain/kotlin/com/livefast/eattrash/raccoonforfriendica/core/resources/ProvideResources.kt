package com.livefast.eattrash.raccoonforfriendica.core.resources

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import com.livefast.eattrash.raccoonforfriendica.core.di.DelicateDiApi
import com.livefast.eattrash.raccoonforfriendica.core.resources.di.rememberCoreResources

val LocalResources: ProvidableCompositionLocal<CoreResources> =
    staticCompositionLocalOf {
        error("CompositionLocal CoreResources not found")
    }

@Composable
fun ProvideResources(content: @Composable () -> Unit) {
    val resources = rememberCoreResources()
    CompositionLocalProvider(
        value = LocalResources provides resources,
        content = content,
    )
}
