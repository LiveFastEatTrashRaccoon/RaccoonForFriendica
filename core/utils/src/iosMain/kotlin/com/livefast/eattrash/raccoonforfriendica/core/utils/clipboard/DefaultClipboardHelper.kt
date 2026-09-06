package com.livefast.eattrash.raccoonforfriendica.core.utils.clipboard

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesBinding

@AssistedInject
@OptIn(ExperimentalComposeUiApi::class)
class DefaultClipboardHelper(
    @Assisted private val clipboard: Clipboard,
) : ClipboardHelper {

    override suspend fun setText(text: String) {
        val newEntry = ClipEntry.withPlainText(text)
        clipboard.setClipEntry(newEntry)
    }

    override suspend fun getText(): String? =
        clipboard.getClipEntry()?.getPlainText()
}

@AssistedFactory
@ContributesBinding(AppScope::class)
fun interface DefaultClipboardHelperFactory : ClipboardHelperFactory {
    override fun create(@Assisted clipboard: Clipboard): DefaultClipboardHelper
}
