package com.livefast.eattrash.raccoonforfriendica.core.utils.fs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.livefast.eattrash.raccoonforfriendica.core.di.DelicateDiApi
import com.livefast.eattrash.raccoonforfriendica.core.di.getByInjection
import okio.Path

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

@OptIn(DelicateDiApi::class)
@Composable
fun rememberFileSystemManager() = remember { getByInjection(FileSystemManager::class) }
