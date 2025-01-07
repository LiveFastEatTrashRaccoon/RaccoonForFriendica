package com.livefast.eattrash.raccoonforfriendica.domain.content.data

data class UserRateLimitModel(
    val id: Long = 0,
    val accountId: Long = 0,
    val handle: String,
    val rate: Double = 1.0,
)
