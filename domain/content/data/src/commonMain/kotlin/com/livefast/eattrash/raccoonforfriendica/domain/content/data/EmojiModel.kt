package com.livefast.eattrash.raccoonforfriendica.domain.content.data

data class EmojiModel(
    val code: String,
    val url: String,
    val staticUrl: String,
    val visibleInPicker: Boolean = false,
    val category: String? = null,
)
