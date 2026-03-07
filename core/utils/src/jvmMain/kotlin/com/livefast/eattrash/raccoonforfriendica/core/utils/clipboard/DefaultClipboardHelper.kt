package com.livefast.eattrash.raccoonforfriendica.core.utils.clipboard

import androidx.compose.ui.platform.Clipboard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection
import java.awt.datatransfer.UnsupportedFlavorException
import java.io.IOException

internal class DefaultClipboardHelper(private val clipboard: Clipboard) : ClipboardHelper {
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
        // clipboard does not contain text
        null
    } catch (_: IOException) {
        // general I/O error (rare for clipboard)
        null
    } catch (_: Exception) {
        null
    }
}
