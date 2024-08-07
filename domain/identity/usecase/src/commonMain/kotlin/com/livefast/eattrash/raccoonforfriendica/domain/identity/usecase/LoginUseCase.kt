package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase

interface LoginUseCase {
    suspend operator fun invoke(
        node: String,
        user: String,
        pass: String,
    )
}
