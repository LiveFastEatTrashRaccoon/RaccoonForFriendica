package com.livefast.eattrash.raccoonforfriendica.core.utils.gallery

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.livefast.eattrash.raccoonforfriendica.core.utils.fs.FileDialogHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.awt.FileDialog
import java.io.File

internal class DefaultGalleryHelper : GalleryHelper {
    override val supportsCustomPath = false

    override fun saveToGallery(
        bytes: ByteArray,
        name: String,
        additionalPathSegment: String?,
    ): Any? {
        return try {
            val userHome = System.getProperty("user.home")
            val picturesDir = File(userHome, "Downloads")
            val targetDir = if (additionalPathSegment != null) {
                File(picturesDir, additionalPathSegment)
            } else {
                picturesDir
            }

            if (!targetDir.exists()) {
                targetDir.mkdirs()
            }

            val file = File(targetDir, name)
            file.writeBytes(bytes)
            file.absolutePath
        } catch (e: Exception) {
            null
        }
    }

    @Composable
    override fun getImageFromGallery(result: (ByteArray) -> Unit) {
        LaunchedEffect(Unit) {
            val file = withContext(Dispatchers.IO) {
                FileDialogHelper.chooseFile(mode = FileDialog.LOAD, mimeTypes = arrayOf("image/*"))
            }
            val bytes = withContext(Dispatchers.IO) {
                file?.readBytes()
            }
            if (bytes != null) {
                result(bytes)
            }
        }
    }
}
