package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.translation.TranslationProviderConfig
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TranslatedPostModel

interface FallbackTranslationRepository {
    suspend fun getTranslation(
        post: PostModel,
        targetLang: String,
        config: TranslationProviderConfig?,
    ): TranslatedPostModel?
}
