package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ApiCredentials

interface LoginUseCase {
    suspend operator fun invoke(
        node: String,
        credentials: ApiCredentials,
    )
}
