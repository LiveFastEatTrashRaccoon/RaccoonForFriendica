package com.livefast.eattrash.raccoonforfriendica.core.utils.di

import com.livefast.eattrash.raccoonforfriendica.core.utils.url.CustomTabsHelper
import com.livefast.eattrash.raccoonforfriendica.core.utils.url.DefaultCustomTabsHelper
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

internal actual val nativeOpenUrlModule =
    DI.Module("NativeUrlModule") {
        bind<CustomTabsHelper> {
            singleton {
                DefaultCustomTabsHelper(
                    context = instance(),
                )
            }
        }
    }
