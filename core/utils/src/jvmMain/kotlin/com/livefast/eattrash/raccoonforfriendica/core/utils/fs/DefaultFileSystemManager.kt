package com.livefast.eattrash.raccoonforfriendica.core.utils.fs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.FileSystem
import okio.Path
import java.awt.FileDialog

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class DefaultFileSystemManager : FileSystemManager {
    override val isSupported = true

    @Composable
    override fun readFromFile(mimeTypes: Array<String>, callback: (String?) -> Unit) {
        LaunchedEffect(Unit) {
            val file = withContext(Dispatchers.IO) {
                FileDialogHelper.chooseFile(mode = FileDialog.LOAD, mimeTypes = mimeTypes)
            }
            callback(file?.readText())
        }
    }

    @Composable
    override fun writeToFile(
        mimeType: String,
        name: String,
        data: String,
        callback: (Boolean) -> Unit,
    ) {
        LaunchedEffect(Unit) {
            val success = withContext(Dispatchers.IO) {
                val file = FileDialogHelper.chooseFile(FileDialog.SAVE, fileName = name)
                if (file != null) {
                    try {
                        file.writeText(data)
                        true
                    } catch (_: Exception) {
                        false
                    }
                } else {
                    false
                }
            }
            callback(success)
        }
    }

    override fun getTempDir(): Path {
        return FileSystem.SYSTEM_TEMPORARY_DIRECTORY
    }
}
