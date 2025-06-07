package com.livefast.eattrash.raccoonforfriendica.core.testutils

import com.livefast.eattrash.raccoonforfriendica.core.di.RootDI
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.kodein.di.DI

class KodeinTestRule(private val modules: List<DI.Module>) : TestWatcher() {
    override fun starting(description: Description) {
        RootDI.di =
            DI {
                importAll(modules)
            }
    }
}
