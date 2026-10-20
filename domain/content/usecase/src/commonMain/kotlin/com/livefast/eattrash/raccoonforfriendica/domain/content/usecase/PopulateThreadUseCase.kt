package com.livefast.eattrash.raccoonforfriendica.domain.content.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel

interface PopulateThreadUseCase {
    suspend operator fun invoke(post: PostModel, maxDepth: Int = 10): List<PostModel>
}
