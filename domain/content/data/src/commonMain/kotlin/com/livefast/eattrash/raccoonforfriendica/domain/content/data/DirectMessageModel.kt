package com.livefast.eattrash.raccoonforfriendica.domain.content.data

data class DirectMessageModel(
    val id: String,
    val text: String? = null,
    val title: String? = null,
    val read: Boolean = true,
    val parentUri: String? = null,
    val sender: UserModel? = null,
    val recipient: UserModel? = null,
)
