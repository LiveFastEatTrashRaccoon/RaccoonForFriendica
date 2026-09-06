package com.livefast.eattrash.raccoonforfriendica.core.utils.fs

import androidx.compose.runtime.Composable
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import okio.FileSystem
import okio.Path

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class DefaultFileSystemManager : FileSystemManager {
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
