package com.livefast.eattrash.raccoonforfriendica.core.utils.di

import com.livefast.eattrash.raccoonforfriendica.core.utils.url.CustomTabsHelper
import com.livefast.eattrash.raccoonforfriendica.core.utils.url.DefaultCustomTabsHelper
import org.koin.dsl.module

internal actual val nativeOpenUrlModule = module {
    single<CustomTabsHelper> {
        DefaultCustomTabsHelper()
    }
}
