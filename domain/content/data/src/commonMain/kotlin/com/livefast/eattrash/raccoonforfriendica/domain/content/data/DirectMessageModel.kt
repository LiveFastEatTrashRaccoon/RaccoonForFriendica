package com.livefast.eattrash.raccoonforfriendica.domain.content.data

data class DirectMessageModel(
    val created: String? = null,
    val id: String,
    val parentUri: String? = null,
    val read: Boolean = true,
    val recipient: UserModel? = null,
    val sender: UserModel? = null,
    val text: String? = null,
    val title: String? = null,
)
