package com.livefast.eattrash.raccoonforfriendica.core.utils.fs

import androidx.compose.runtime.Composable
import com.livefast.eattrash.raccoonforfriendica.core.di.RootDI
import okio.Path
import org.kodein.di.instance

interface FileSystemManager {
    val isSupported: Boolean

    @Composable
    fun readFromFile(
        mimeTypes: Array<String>,
        callback: (String?) -> Unit,
    )

    @Composable
    fun writeToFile(
        mimeType: String,
        name: String,
        data: String,
        callback: (Boolean) -> Unit,
    )

    fun getTempDir(): Path
}

fun getFileSystemManager(): FileSystemManager {
    val res by RootDI.di.instance<FileSystemManager>()
    return res
}
