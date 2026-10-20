package com.livefast.eattrash.raccoonforfriendica.domain.content.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TranslatedPostModel

interface GetTranslationUseCase {
    suspend operator fun invoke(post: PostModel, targetLang: String): TranslatedPostModel?
}
