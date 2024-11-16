package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase

interface ExportSettingsUseCase {
    suspend operator fun invoke(): String
}
