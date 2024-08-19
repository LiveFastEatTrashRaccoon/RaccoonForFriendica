package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase

import androidx.compose.runtime.Stable
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.UrlOpeningMode

@Stable
interface OpenUrlUseCase {
    operator fun invoke(
        url: String,
        mode: UrlOpeningMode = UrlOpeningMode.External,
    )
}
