package com.livefast.eattrash.raccoonforfriendica.domain.content.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel

interface ToggleDislikeUseCase {
    suspend operator fun invoke(post: PostModel): PostModel?
}
