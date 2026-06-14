package com.livefast.eattrash.raccoonforfriendica.core.resources.testutils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.livefast.eattrash.raccoonforfriendica.core.resources.DefaultCoreResources
import com.livefast.eattrash.raccoonforfriendica.core.resources.LocalResources

@Composable
fun ProvideTestResources(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        value = LocalResources provides DefaultCoreResources(),
        content = content,
    )
}
