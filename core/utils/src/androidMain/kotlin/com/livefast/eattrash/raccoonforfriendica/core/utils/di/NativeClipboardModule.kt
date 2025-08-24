package com.livefast.eattrash.raccoonforfriendica.core.utils.di

import androidx.compose.ui.platform.Clipboard
import com.livefast.eattrash.raccoonforfriendica.core.utils.clipboard.ClipboardHelper
import com.livefast.eattrash.raccoonforfriendica.core.utils.clipboard.DefaultClipboardHelper
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.factory
import org.kodein.di.instance

actual val nativeClipboardModule = DI.Module("NativeClipboardModule") {
    bind<ClipboardHelper> {
        factory<Any, Clipboard, ClipboardHelper> { clipboard: Clipboard ->
            DefaultClipboardHelper(
                clipboard = clipboard,
                context = instance(),
            )
        }
    }
}
