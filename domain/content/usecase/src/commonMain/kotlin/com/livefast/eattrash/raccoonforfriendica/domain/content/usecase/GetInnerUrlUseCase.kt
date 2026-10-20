package com.livefast.eattrash.raccoonforfriendica.domain.content.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel

interface GetInnerUrlUseCase {
    suspend operator fun invoke(post: PostModel): String?
}
