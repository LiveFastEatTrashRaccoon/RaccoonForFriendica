package com.livefast.eattrash.raccoonforfriendica.core.utils.fs

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import okio.FileSystem
import okio.Path
import java.io.InputStreamReader
import java.io.OutputStreamWriter

internal class DefaultFileSystemManager(private val context: Context) : FileSystemManager {
    override val isSupported = true

    @Composable
    override fun readFromFile(mimeTypes: Array<String>, callback: (String?) -> Unit) {
        val launcher =
            rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
                if (uri != null) {
                    val stream = context.contentResolver.openInputStream(uri)
                    InputStreamReader(stream).use { reader ->
                        val content = reader.readText()
                        callback(content)
                    }
                }
            }
        SideEffect {
            launcher.launch(mimeTypes)
        }
    }

    @Composable
    override fun writeToFile(mimeType: String, name: String, data: String, callback: (Boolean) -> Unit) {
        val launcher =
            rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument(mimeType)) { uri ->
                if (uri != null) {
                    val stream = context.contentResolver.openOutputStream(uri)
                    OutputStreamWriter(stream).use { writer ->
                        writer.write(data)
                        callback(true)
                    }
                }
            }
        SideEffect {
            launcher.launch(name)
        }
    }

    override fun getTempDir(): Path = FileSystem.SYSTEM_TEMPORARY_DIRECTORY
}
