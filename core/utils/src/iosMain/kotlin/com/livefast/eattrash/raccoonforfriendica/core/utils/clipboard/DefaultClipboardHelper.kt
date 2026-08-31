package com.livefast.eattrash.raccoonforfriendica.core.utils.clipboard

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam

@Factory
@OptIn(ExperimentalComposeUiApi::class)
internal class DefaultClipboardHelper(
    @InjectedParam private val clipboard: Clipboard,
) : ClipboardHelper {

    override suspend fun setText(text: String) {
        val newEntry = ClipEntry.withPlainText(text)
        clipboard.setClipEntry(newEntry)
    }

    override suspend fun getText(): String? =
        clipboard.getClipEntry()?.getPlainText()
}
