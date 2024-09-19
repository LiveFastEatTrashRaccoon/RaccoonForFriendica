package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Poll(
    @SerialName("expired") val expired: Boolean = false,
    @SerialName("expires_at") val expiresAt: String? = null,
    @SerialName("id") val id: String,
    @SerialName("multiple") val multiple: Boolean = false,
    @SerialName("options") val options: List<PollOption> = emptyList(),
    @SerialName("own_votes") val ownVotes: List<Int> = emptyList(),
    @SerialName("voted") val voted: Boolean = false,
    @SerialName("voters_count") val votersCount: Int? = null,
    @SerialName("votes_count") val votesCount: Int? = null,
)
