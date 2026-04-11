package com.livefast.eattrash.raccoonforfriendica.core.translation.di

import com.livefast.eattrash.raccoonforfriendica.core.translation.DefaultTranslationProviderFactory
import com.livefast.eattrash.raccoonforfriendica.core.translation.TranslationProviderFactory
import com.livefast.eattrash.raccoonforfriendica.core.translation.store.DefaultTranslationProviderConfigStore
import com.livefast.eattrash.raccoonforfriendica.core.translation.store.TranslationProviderConfigStore
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

val translationModule = DI.Module("TranslationModule") {
    bind<TranslationProviderFactory> {
        singleton {
            DefaultTranslationProviderFactory()
        }
    }
    bind<TranslationProviderConfigStore> {
        singleton {
            DefaultTranslationProviderConfigStore(keyStore = instance())
        }
    }
}
