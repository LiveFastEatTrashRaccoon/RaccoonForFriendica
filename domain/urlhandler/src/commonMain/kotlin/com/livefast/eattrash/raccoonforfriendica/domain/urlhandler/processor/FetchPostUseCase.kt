package com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel

interface FetchPostUseCase {
    suspend operator fun invoke(url: String): PostModel?
}
