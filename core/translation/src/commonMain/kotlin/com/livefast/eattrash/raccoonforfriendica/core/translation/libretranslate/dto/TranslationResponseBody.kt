package com.livefast.eattrash.raccoonforfriendica.core.translation.libretranslate.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TranslationResponseBody(@SerialName("translatedText") val text: String? = null)
