package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

sealed interface AlbumPhotoPaginationSpecification {
    data class Default(
        val album: String,
    ) : AlbumPhotoPaginationSpecification
}
