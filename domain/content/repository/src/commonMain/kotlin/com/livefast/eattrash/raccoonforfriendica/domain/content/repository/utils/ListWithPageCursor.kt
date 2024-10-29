package com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils

data class ListWithPageCursor<T>(
    val list: List<T> = emptyList(),
    val cursor: String? = null,
)
