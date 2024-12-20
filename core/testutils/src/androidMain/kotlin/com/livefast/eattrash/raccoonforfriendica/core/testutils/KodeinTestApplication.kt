package com.livefast.eattrash.raccoonforfriendica.core.testutils

import android.app.Application
import com.livefast.eattrash.raccoonforfriendica.core.di.RootDI
import org.kodein.di.DI
import org.kodein.di.DIAware

class KodeinTestApplication :
    Application(),
    DIAware {
    override val di: DI
        get() = RootDI.di
}
