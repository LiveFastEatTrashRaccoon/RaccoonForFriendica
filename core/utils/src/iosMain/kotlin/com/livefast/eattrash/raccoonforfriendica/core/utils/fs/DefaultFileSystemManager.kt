package com.livefast.eattrash.raccoonforfriendica.core.utils.fs

import androidx.compose.runtime.Composable
import okio.FileSystem
import okio.Path
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Single
class DefaultFileSystemManager : FileSystemManager {
    override val isSupported = false

    @Composable
    override fun readFromFile(
        mimeTypes: Array<String>,
        callback: (String?) -> Unit,
    ) {
        callback(null)
    }

    @Composable
    override fun writeToFile(
        mimeType: String,
        name: String,
        data: String,
        callback: (Boolean) -> Unit,
    ) {
        callback(false)
    }

    override fun getTempDir(): Path = FileSystem.SYSTEM_TEMPORARY_DIRECTORY
}

object FileSystemManagerDiHelper : KoinComponent {
    val helper: FileSystemManager by inject()
}

actual fun getFileSystemManager(): FileSystemManager = FileSystemManagerDiHelper.helper
