package com.livefast.eattrash.raccoonforfriendica.core.translation

import com.livefast.eattrash.raccoonforfriendica.core.translation.libretranslate.LibreTranslateProvider

internal class DefaultTranslationProviderFactory : TranslationProviderFactory {
    override fun create(config: TranslationProviderConfig): TranslationProvider = when (config.name) {
        TranslationProviderTypes.LibreTranslate.name -> LibreTranslateProvider(
            apiKey = config.apiKey,
            baseUrl = config.url,
        )
        else -> throw IllegalArgumentException("Unknown translation provider: ${config.name}")
    }
}
