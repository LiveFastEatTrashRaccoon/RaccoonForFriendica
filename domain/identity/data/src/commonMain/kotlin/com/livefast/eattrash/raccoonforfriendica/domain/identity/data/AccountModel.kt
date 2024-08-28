package com.livefast.eattrash.raccoonforfriendica.domain.identity.data

data class AccountModel(
    val active: Boolean = false,
    val avatar: String? = null,
    val handle: String = "",
    val id: Long = 0,
    val remoteId: String? = null,
    val displayName: String? = null,
)
