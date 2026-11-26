package com.livefast.eattrash.raccoonforfriendica.core.utils.clipboard

import android.content.ClipData
import android.content.Context
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.toClipEntry
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesBinding

@AssistedInject
class DefaultClipboardHelper(
    @Assisted private val clipboard: Clipboard,
    private val context: Context,
) : ClipboardHelper {
    override suspend fun setText(text: String) {
        val newEntry = ClipData.newPlainText("", text).toClipEntry()
        clipboard.setClipEntry(newEntry)
    }

    override suspend fun getText(): String? {
        val data = clipboard.getClipEntry()?.clipData ?: return null
        val count = data.itemCount
        return if (count > 0) {
            val item = data.getItemAt(count - 1)
            item.coerceToText(context).toString()
        } else {
            null
        }
    }
}

@ContributesBinding(AppScope::class)
@AssistedFactory
fun interface DefaultClipboardHelperFactory : ClipboardHelperFactory {
    override fun create(@Assisted clipboard: Clipboard): DefaultClipboardHelper
}
