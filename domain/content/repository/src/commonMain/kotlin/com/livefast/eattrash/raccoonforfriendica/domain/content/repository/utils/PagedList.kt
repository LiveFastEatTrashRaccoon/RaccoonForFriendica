package com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils

data class PagedList<T>(val list: List<T> = emptyList(), val cursor: String? = null)
