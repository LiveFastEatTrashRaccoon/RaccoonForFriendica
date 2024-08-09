package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Suggestion(
    @SerialName("sources") val sources: List<SuggestionSource> = emptyList(),
    @SerialName("account") val user: Account,
)
