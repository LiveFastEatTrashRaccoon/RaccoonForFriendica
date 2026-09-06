package com.livefast.eattrash.raccoonforfriendica.core.commonui.components.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.livefast.eattrash.raccoonforfriendica.core.di.utils.DummyUiDeps
import com.livefast.eattrash.raccoonforfriendica.core.di.utils.ProvideUiDeps
import com.livefast.eattrash.raccoonforfriendica.core.di.utils.UiDeps
import com.livefast.eattrash.raccoonforfriendica.core.l10n.DefaultStrings
import com.livefast.eattrash.raccoonforfriendica.core.l10n.ProvideStrings
import com.livefast.eattrash.raccoonforfriendica.core.l10n.Strings
import com.livefast.eattrash.raccoonforfriendica.core.resources.CoreResources
import com.livefast.eattrash.raccoonforfriendica.core.resources.DefaultCoreResources
import com.livefast.eattrash.raccoonforfriendica.core.resources.ProvideResources

@Composable
fun PreviewWrapper(
    uiDeps: UiDeps = remember {
        object : DummyUiDeps() {
            override val resources: CoreResources = DefaultCoreResources()
            override val strings: Strings = DefaultStrings()
        }
    },
    content: @Composable () -> Unit,
) {
    ProvideUiDeps(uiDeps) {
        ProvideResources(resources = uiDeps.resources) {
            ProvideStrings(lang = "en", strings = uiDeps.strings) {
                content()
            }
        }
    }
}
