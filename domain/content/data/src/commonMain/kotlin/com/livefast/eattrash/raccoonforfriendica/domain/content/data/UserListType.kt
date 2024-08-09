package com.livefast.eattrash.raccoonforfriendica.domain.content.data

sealed interface UserListType {
    data object Follower : UserListType

    data object Following : UserListType
}
