package com.livefast.eattrash.raccoonforfriendica.core.utils.di

import com.livefast.eattrash.raccoonforfriendica.core.utils.fs.DefaultFileSystemManager
import com.livefast.eattrash.raccoonforfriendica.core.utils.fs.FileSystemManager
import org.koin.dsl.module

internal actual val nativeFileSystemModule = module {
    single<FileSystemManager> {
        DefaultFileSystemManager(
            context = get(),
        )
    }
}
