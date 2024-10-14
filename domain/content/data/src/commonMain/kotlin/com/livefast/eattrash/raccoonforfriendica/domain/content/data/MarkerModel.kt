package com.livefast.eattrash.raccoonforfriendica.domain.content.data

sealed interface MarkerType {
    data object Home : MarkerType

    data object Notifications : MarkerType
}

data class MarkerModel(
    val type: MarkerType,
    val lastReadId: String,
)
