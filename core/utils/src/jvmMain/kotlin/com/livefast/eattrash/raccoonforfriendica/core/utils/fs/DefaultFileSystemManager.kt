package com.livefast.eattrash.raccoonforfriendica.core.utils.fs

import androidx.compose.runtime.Composable
import okio.Path
import okio.FileSystem

internal class DefaultFileSystemManager : FileSystemManager {
    override val isSupported = false

    @Composable
    override fun readFromFile(mimeTypes: Array<String>, callback: (String?) -> Unit) {
        // TODO(jvm): implement
        // no-op
    }

    @Composable
    override fun writeToFile(
        mimeType: String,
        name: String,
        data: String,
        callback: (Boolean) -> Unit,
    ) {
        // TODO(jvm): implement
        // no-op
    }

    override fun getTempDir(): Path {
        return FileSystem.SYSTEM_TEMPORARY_DIRECTORY
    }
}
