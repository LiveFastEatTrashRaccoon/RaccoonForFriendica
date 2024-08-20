package com.livefast.eattrash.raccoonforfriendica.domain.content.data

data class CircleModel(
    val id: String,
    val name: String = "",
    val users: List<UserModel> = emptyList(),
)
