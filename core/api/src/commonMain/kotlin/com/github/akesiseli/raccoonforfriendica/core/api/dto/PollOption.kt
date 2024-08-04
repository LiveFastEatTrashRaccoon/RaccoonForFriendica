package com.github.akesiseli.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PollOption(
    @SerialName("title") val title: String,
    @SerialName("votes_count") val votesCount: Int = 0,
)
