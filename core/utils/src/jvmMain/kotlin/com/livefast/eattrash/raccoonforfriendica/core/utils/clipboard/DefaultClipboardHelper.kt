package com.livefast.eattrash.raccoonforfriendica.core.utils.clipboard

import androidx.compose.ui.platform.Clipboard
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection
import java.awt.datatransfer.UnsupportedFlavorException
import java.io.IOException

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class DefaultClipboardHelperFactory : ClipboardHelperFactory {
    override fun create(clipboard: Clipboard): ClipboardHelper = DefaultClipboardHelper()
}

class DefaultClipboardHelper : ClipboardHelper {
    override suspend fun setText(text: String) {
        val selection = StringSelection(text)
        val systemClipboard = Toolkit.getDefaultToolkit().systemClipboard
        systemClipboard.setContents(selection, selection)
    }

    override suspend fun getText(): String? = try {
        val systemClipboard = Toolkit.getDefaultToolkit().systemClipboard
        val contents = systemClipboard.getContents(null)

        if (contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            withContext(Dispatchers.IO) {
                contents.getTransferData(DataFlavor.stringFlavor)
            } as String
        } else {
            null
        }
    } catch (_: UnsupportedFlavorException) {
        null
    } catch (_: IOException) {
        null
    } catch (_: Exception) {
        null
    }
}
