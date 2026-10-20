package com.livefast.eattrash.raccoonforfriendica.domain.content.data

sealed interface PostListType {
    data object Favorites : PostListType

    data object Bookmarks : PostListType

    data class Quoting(val postId: String, val otherInstance: String? = null) : PostListType
}
