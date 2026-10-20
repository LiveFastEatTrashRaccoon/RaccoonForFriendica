package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

sealed interface UserPaginationSpecification {
    data class Follower(val userId: String) : UserPaginationSpecification

    data class Following(val userId: String) : UserPaginationSpecification

    data class PostUsersReblog(val postId: String) : UserPaginationSpecification

    data class PostUsersFavorite(val postId: String) : UserPaginationSpecification

    data class Search(val query: String = "", val withRelationship: Boolean = true) : UserPaginationSpecification

    data class SearchFollowing(
        val query: String = "",
        val excludeIds: List<String> = emptyList(),
        val withRelationship: Boolean = true,
    ) : UserPaginationSpecification

    data object Muted : UserPaginationSpecification

    data object Blocked : UserPaginationSpecification

    data object Limited : UserPaginationSpecification

    data class CircleMembers(val id: String, val query: String = "") : UserPaginationSpecification
}
