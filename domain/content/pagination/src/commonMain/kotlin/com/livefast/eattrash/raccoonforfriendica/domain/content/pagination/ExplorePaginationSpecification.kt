package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import kotlin.String

sealed interface ExplorePaginationSpecification {
    data class Posts(val includeNsfw: Boolean = true, val otherInstance: String? = null) :
        ExplorePaginationSpecification

    data class Hashtags(val refresh: Boolean = false, val otherInstance: String? = null) :
        ExplorePaginationSpecification

    data class Links(val otherInstance: String? = null) : ExplorePaginationSpecification

    data object Suggestions : ExplorePaginationSpecification
}
