package com.livefast.eattrash.raccoonforfriendica.domain.content.data

sealed interface RelationshipStatus {
    data object MutualFollow : RelationshipStatus

    data object Following : RelationshipStatus

    data object FollowsYou : RelationshipStatus

    data object RequestedToOther : RelationshipStatus

    data object RequestedToYou : RelationshipStatus

    data object Undetermined : RelationshipStatus
}

fun RelationshipModel.toStatus(): RelationshipStatus =
    when {
        following && followedBy -> RelationshipStatus.MutualFollow
        following -> RelationshipStatus.Following
        followedBy -> RelationshipStatus.FollowsYou
        requested -> RelationshipStatus.RequestedToOther
        requestedBy -> RelationshipStatus.RequestedToYou
        else -> RelationshipStatus.Undetermined
    }
