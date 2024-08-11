package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase

import androidx.compose.runtime.Stable

@Stable
interface OpenUrlUseCase {
    operator fun invoke(url: String)
}
