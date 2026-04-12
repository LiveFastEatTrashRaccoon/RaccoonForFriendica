package com.livefast.eattrash.raccoonforfriendica.core.translation

interface TranslationProviderFactory {
    fun create(config: TranslationProviderConfig): TranslationProvider
}
