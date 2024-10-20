package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

sealed interface EventsPaginationSpecification {
    data object All : EventsPaginationSpecification
}
