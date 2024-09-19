package com.livefast.eattrash.raccoonforfriendica.domain.content.data

import kotlin.jvm.Transient

data class PollModel(
    val expired: Boolean = false,
    val expiresAt: String? = null,
    val id: String,
    @Transient val loading: Boolean = false,
    val multiple: Boolean = false,
    val options: List<PollOptionModel> = emptyList(),
    val ownVotes: List<Int> = emptyList(),
    val voted: Boolean = false,
    val voters: Int = 0,
    val votes: Int = 0,
)
