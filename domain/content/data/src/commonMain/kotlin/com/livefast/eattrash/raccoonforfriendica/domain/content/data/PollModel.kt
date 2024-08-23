package com.livefast.eattrash.raccoonforfriendica.domain.content.data

import kotlin.jvm.Transient

data class PollModel(
    val id: String,
    val expiresAt: String? = null,
    val expired: Boolean = false,
    val multiple: Boolean = false,
    val votes: Int = 0,
    val voters: Int = 0,
    val options: List<PollOptionModel> = emptyList(),
    val voted: Boolean = false,
    val ownVotes: List<Int> = emptyList(),
    @Transient
    val loading: Boolean = false,
)
