package com.livefast.eattrash.raccoonforfriendica.core.api.dto

data class Page<T>(val elements: List<T>, val cursor: String?)
