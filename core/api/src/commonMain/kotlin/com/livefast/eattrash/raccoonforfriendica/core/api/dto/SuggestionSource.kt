package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName

enum class SuggestionSource {
    @SerialName("staff")
    STAFF,

    @SerialName("past_interactions")
    PAST_INTERACTIONS,

    @SerialName("global")
    GLOBAL,
}
