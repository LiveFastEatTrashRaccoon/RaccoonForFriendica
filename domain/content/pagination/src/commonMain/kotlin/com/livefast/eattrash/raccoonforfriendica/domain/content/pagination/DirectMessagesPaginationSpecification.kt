package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

sealed interface DirectMessagesPaginationSpecification {
    data object All : DirectMessagesPaginationSpecification

    data class Replies(
        val parentUri: String,
    ) : DirectMessagesPaginationSpecification
}
