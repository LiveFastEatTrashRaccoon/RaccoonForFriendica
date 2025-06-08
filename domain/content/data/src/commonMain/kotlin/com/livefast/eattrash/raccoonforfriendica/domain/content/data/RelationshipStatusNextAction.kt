package com.livefast.eattrash.raccoonforfriendica.domain.content.data

sealed interface RelationshipStatusNextAction {
    data object ConfirmUnfollow : RelationshipStatusNextAction

    data object ConfirmDeleteFollowRequest : RelationshipStatusNextAction

    data object Follow : RelationshipStatusNextAction

    data object Unfollow : RelationshipStatusNextAction

    data object AcceptRequest : RelationshipStatusNextAction
}

fun RelationshipStatus.getNextAction(): RelationshipStatusNextAction = when (this) {
    RelationshipStatus.Following -> RelationshipStatusNextAction.ConfirmUnfollow
    RelationshipStatus.FollowsYou -> RelationshipStatusNextAction.Follow
    RelationshipStatus.MutualFollow -> RelationshipStatusNextAction.ConfirmUnfollow
    RelationshipStatus.RequestedToOther -> RelationshipStatusNextAction.ConfirmDeleteFollowRequest
    RelationshipStatus.RequestedToYou -> RelationshipStatusNextAction.AcceptRequest
    RelationshipStatus.Undetermined -> RelationshipStatusNextAction.Follow
}
