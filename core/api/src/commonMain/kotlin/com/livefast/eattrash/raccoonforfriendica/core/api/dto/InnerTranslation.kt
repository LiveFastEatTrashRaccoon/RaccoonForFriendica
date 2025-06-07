package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class InnerTranslation(
    @SerialName("inputs") val inputs: InnerTranslationInputs,
    @SerialName("response") val response: InnerTranslationResponse,
)

@Serializable
internal data class InnerTranslationInputs(
    @SerialName("text") val text: String,
    @SerialName("source_lang") val sourceLang: String?,
    @SerialName("target_lang") val targetLang: String,
)

@Serializable
internal data class InnerTranslationResponse(@SerialName("translated_text") val translatedText: String)
