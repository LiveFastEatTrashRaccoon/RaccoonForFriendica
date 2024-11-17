package com.livefast.eattrash.raccoonforfriendica.feature.composer.utils

import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.MarkupMode

interface StripMarkupUseCase {
    operator fun invoke(
        text: String,
        mode: MarkupMode,
    ): String
}
