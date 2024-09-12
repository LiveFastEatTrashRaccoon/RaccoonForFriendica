package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

sealed interface UnpublishedPaginationSpecification {
    data object Scheduled : UnpublishedPaginationSpecification
}
