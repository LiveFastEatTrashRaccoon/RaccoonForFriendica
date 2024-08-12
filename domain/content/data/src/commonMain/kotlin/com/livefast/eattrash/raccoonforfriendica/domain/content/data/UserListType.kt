package com.livefast.eattrash.raccoonforfriendica.domain.content.data

sealed interface UserListType {
    data class Follower(
        val userId: String,
    ) : UserListType

    data class Following(
        val userId: String,
    ) : UserListType

    data class UsersReblog(
        val entryId: String,
        val count: Int,
    ) : UserListType

    data class UsersFavorite(
        val entryId: String,
        val count: Int,
    ) : UserListType
}
