package com.livefast.eattrash.raccoonforfriendica.domain.content.data

sealed interface UserListType {
    data object Follower : UserListType

    data object Following : UserListType

    data object UsersReblog : UserListType

    data object UsersFavorite : UserListType
}

fun UserListType.toInt(): Int =
    when (this) {
        UserListType.Follower -> 0
        UserListType.Following -> 1
        UserListType.UsersFavorite -> 2
        UserListType.UsersReblog -> 3
    }

fun Int.toUserListType(): UserListType =
    when (this) {
        3 -> UserListType.UsersReblog
        2 -> UserListType.UsersFavorite
        1 -> UserListType.Following
        else -> UserListType.Follower
    }
