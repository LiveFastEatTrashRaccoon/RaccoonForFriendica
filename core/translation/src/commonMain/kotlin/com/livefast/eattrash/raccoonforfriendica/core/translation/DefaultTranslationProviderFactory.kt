package com.livefast.eattrash.raccoonforfriendica.core.translation

import com.livefast.eattrash.raccoonforfriendica.core.translation.libretranslate.LibreTranslateProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class DefaultTranslationProviderFactory : TranslationProviderFactory {
    override fun create(config: TranslationProviderConfig): TranslationProvider = when (config.name) {
        TranslationProviderTypes.LibreTranslate.name -> LibreTranslateProvider(
            apiKey = config.apiKey,
            baseUrl = config.url,
        )
        else -> throw IllegalArgumentException("Unknown translation provider: ${config.name}")
    }
}
