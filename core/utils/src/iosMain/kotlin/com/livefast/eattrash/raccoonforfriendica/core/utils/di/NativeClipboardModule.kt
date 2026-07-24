package com.livefast.eattrash.raccoonforfriendica.core.utils.di

import androidx.compose.ui.platform.Clipboard
import com.livefast.eattrash.raccoonforfriendica.core.utils.clipboard.ClipboardHelper
import com.livefast.eattrash.raccoonforfriendica.core.utils.clipboard.DefaultClipboardHelper
import org.koin.dsl.module

actual val nativeClipboardModule = module  {
    factory<ClipboardHelper> { param ->
        val arg: Clipboard = param.get()
        DefaultClipboardHelper(clipboard = arg)
    }
}
