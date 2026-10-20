package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TranslatedPostModel

interface TranslationRepository {
    suspend fun getTranslation(post: PostModel, targetLang: String): TranslatedPostModel?
}
