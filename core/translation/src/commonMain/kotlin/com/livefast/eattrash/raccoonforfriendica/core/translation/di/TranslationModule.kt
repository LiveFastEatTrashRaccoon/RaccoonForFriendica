package com.livefast.eattrash.raccoonforfriendica.core.translation.di

import com.livefast.eattrash.raccoonforfriendica.core.translation.DefaultTranslationProviderFactory
import com.livefast.eattrash.raccoonforfriendica.core.translation.TranslationProviderFactory
import com.livefast.eattrash.raccoonforfriendica.core.translation.store.DefaultTranslationProviderConfigStore
import com.livefast.eattrash.raccoonforfriendica.core.translation.store.TranslationProviderConfigStore
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

val translationModule = DI.Module("TranslationModule") {
    bindSingleton<TranslationProviderFactory> {
        DefaultTranslationProviderFactory()
    }
    bindSingleton<TranslationProviderConfigStore> {
        DefaultTranslationProviderConfigStore(keyStore = instance())
    }
}
