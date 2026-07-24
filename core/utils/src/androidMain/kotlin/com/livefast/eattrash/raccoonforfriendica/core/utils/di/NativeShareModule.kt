package com.livefast.eattrash.raccoonforfriendica.core.utils.di

import com.livefast.eattrash.raccoonforfriendica.core.utils.share.DefaultShareHelper
import com.livefast.eattrash.raccoonforfriendica.core.utils.share.ShareHelper
import org.koin.dsl.module

internal actual val nativeShareModule = module {
    single<ShareHelper> {
        DefaultShareHelper(
            context = get(),
        )
    }
}
