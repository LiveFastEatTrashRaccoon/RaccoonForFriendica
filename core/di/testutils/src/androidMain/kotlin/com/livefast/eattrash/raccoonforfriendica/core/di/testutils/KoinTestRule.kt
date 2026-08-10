package com.livefast.eattrash.raccoonforfriendica.core.di.testutils

import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.koin.core.context.GlobalContext.getKoinApplicationOrNull
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.dsl.KoinAppDeclaration

class KoinTestRule(private val appDeclaration: KoinAppDeclaration) : TestWatcher() {
    override fun starting(description: Description) {
        if (getKoinApplicationOrNull() != null) {
            stopKoin()
        }
        startKoin(appDeclaration)
    }

    override fun finished(description: Description) {
        stopKoin()
    }
}
