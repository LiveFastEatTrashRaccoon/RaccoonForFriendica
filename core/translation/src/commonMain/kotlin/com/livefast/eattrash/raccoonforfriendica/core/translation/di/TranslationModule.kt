package com.livefast.eattrash.raccoonforfriendica.core.translation.di

import com.livefast.eattrash.raccoonforfriendica.core.translation.DefaultTranslationProviderFactory
import com.livefast.eattrash.raccoonforfriendica.core.translation.TranslationProviderFactory
import com.livefast.eattrash.raccoonforfriendica.core.translation.store.DefaultTranslationProviderConfigStore
import com.livefast.eattrash.raccoonforfriendica.core.translation.store.TranslationProviderConfigStore
import org.koin.dsl.module

val translationModule = module {
    single<TranslationProviderFactory> {
        DefaultTranslationProviderFactory()
    }
    single<TranslationProviderConfigStore> {
        DefaultTranslationProviderConfigStore(keyStore = get())
    }
}
