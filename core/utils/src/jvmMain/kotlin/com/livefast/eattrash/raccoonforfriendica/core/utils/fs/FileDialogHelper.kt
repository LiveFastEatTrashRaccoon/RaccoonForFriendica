package com.livefast.eattrash.raccoonforfriendica.core.utils.fs

import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import java.io.FilenameFilter

internal object FileDialogHelper {
    fun chooseFile(
        mode: Int,
        title: String = DIALOG_TITLE,
        fileName: String? = null,
        mimeTypes: Array<String>? = null,
    ): File? {
        val dialog = FileDialog(null as Frame?, title, mode)
        if (fileName != null) {
            dialog.file = fileName
        }

        if (mode == FileDialog.LOAD && mimeTypes != null) {
            val extensions = mimeTypes.flatMap { mimeToExtensions(it) }.toSet()
            if (extensions.isNotEmpty()) {
                dialog.filenameFilter = FilenameFilter { _, name ->
                    extensions.any { name.lowercase().endsWith(it.lowercase()) }
                }
            }
        }

        dialog.isVisible = true
        val selectedFile = dialog.file ?: return null
        return File(dialog.directory, selectedFile)
    }

    private fun mimeToExtensions(mimeType: String): List<String> {
        return when (mimeType.lowercase()) {
            "text/plain" -> listOf(".txt")
            "application/json" -> listOf(".json")
            "image/jpeg" -> listOf(".jpg", ".jpeg")
            "image/png" -> listOf(".png")
            "image/gif" -> listOf(".gif")
            "image/*" -> listOf(".jpg", ".jpeg", ".png", ".gif", ".webp", ".bmp")
            "application/pdf" -> listOf(".pdf")
            else -> emptyList()
        }
    }

    private const val DIALOG_TITLE = "File"
}
