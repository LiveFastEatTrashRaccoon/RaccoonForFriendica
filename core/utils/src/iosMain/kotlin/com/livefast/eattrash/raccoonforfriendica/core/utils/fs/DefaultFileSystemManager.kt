package com.livefast.eattrash.raccoonforfriendica.core.utils.fs

import org.koin.core.annotation.Single
import androidx.compose.runtime.Composable
import okio.FileSystem
import okio.Path

@Single
internal class DefaultFileSystemManager : FileSystemManager {
    override val isSupported = false

    @Composable
    override fun readFromFile(mimeTypes: Array<String>, callback: (String?) -> Unit) {
        callback(null)
    }

    @Composable
    override fun writeToFile(mimeType: String, name: String, data: String, callback: (Boolean) -> Unit) {
        callback(false)
    }

    override fun getTempDir(): Path = FileSystem.SYSTEM_TEMPORARY_DIRECTORY
}
