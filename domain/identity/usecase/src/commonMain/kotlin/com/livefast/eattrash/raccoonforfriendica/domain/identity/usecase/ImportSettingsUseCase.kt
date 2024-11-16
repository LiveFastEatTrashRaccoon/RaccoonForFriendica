package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase

interface ImportSettingsUseCase {
    suspend operator fun invoke(content: String)
}
