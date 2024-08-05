package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Poll(
    @SerialName("id") val id: String,
    @SerialName("expires_at") val expiresAt: String? = null,
    @SerialName("expired") val expired: Boolean = false,
    @SerialName("multiple") val multiple: Boolean = false,
    @SerialName("votes_count") val votesCount: Int? = null,
    @SerialName("voters_count") val votersCount: Int? = null,
    @SerialName("options") val options: List<PollOption> = emptyList(),
    @SerialName("voted") val voted: Boolean = false,
    @SerialName("own_votes") val ownVotes: List<Int> = emptyList(),
)
