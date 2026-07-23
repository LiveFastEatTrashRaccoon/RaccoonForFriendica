package com.livefast.eattrash.raccoonforfriendica.core.utils.di

import androidx.compose.ui.platform.Clipboard
import com.livefast.eattrash.raccoonforfriendica.core.utils.clipboard.ClipboardHelper
import com.livefast.eattrash.raccoonforfriendica.core.utils.clipboard.DefaultClipboardHelper
import org.koin.dsl.module

actual val nativeClipboardModule = module {
    factory<ClipboardHelper> { params ->
        val arg: Clipboard = params.get()
        DefaultClipboardHelper(arg)
    }
}
