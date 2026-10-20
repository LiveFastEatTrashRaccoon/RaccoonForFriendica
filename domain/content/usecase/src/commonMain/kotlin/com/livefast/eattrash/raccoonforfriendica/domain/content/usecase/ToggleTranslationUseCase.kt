package com.livefast.eattrash.raccoonforfriendica.domain.content.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel

interface ToggleTranslationUseCase {
    suspend operator fun invoke(post: PostModel, targetLang: String): PostModel
}
