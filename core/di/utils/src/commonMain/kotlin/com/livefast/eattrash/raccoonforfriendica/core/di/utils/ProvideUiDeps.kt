package com.livefast.eattrash.raccoonforfriendica.core.di.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf

val LocalUiDeps: ProvidableCompositionLocal<UiDeps> =
    staticCompositionLocalOf {
        error("CompositionLocal UiDeps not found")
    }

@Composable
fun ProvideUiDeps(deps: UiDeps, content: @Composable () -> Unit) {
    CompositionLocalProvider(
        value = LocalUiDeps provides deps,
        content = content,
    )
}
