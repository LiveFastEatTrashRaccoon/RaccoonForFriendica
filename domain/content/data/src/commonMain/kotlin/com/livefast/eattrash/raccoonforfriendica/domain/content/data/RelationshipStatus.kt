package com.livefast.eattrash.raccoonforfriendica.domain.content.data

import androidx.compose.runtime.Composable
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings

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

@Composable
fun RelationshipStatus.toReadableName(): String =
    when (this) {
        RelationshipStatus.Following -> LocalStrings.current.relationshipStatusFollowing
        RelationshipStatus.FollowsYou -> LocalStrings.current.relationshipStatusFollowsYou
        RelationshipStatus.MutualFollow -> LocalStrings.current.relationshipStatusMutual
        RelationshipStatus.RequestedToOther -> LocalStrings.current.relationshipStatusRequestedToOther
        RelationshipStatus.RequestedToYou -> LocalStrings.current.relationshipStatusRequestedToYou
        RelationshipStatus.Undetermined -> LocalStrings.current.actionFollow
    }

@Composable
fun RelationshipStatus.isProminent(): Boolean =
    when (this) {
        RelationshipStatus.Following -> false
        RelationshipStatus.FollowsYou -> false
        RelationshipStatus.MutualFollow -> false
        RelationshipStatus.RequestedToOther -> false
        RelationshipStatus.RequestedToYou -> true
        RelationshipStatus.Undetermined -> true
    }
