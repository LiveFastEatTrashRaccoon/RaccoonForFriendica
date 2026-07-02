package com.livefast.eattrash.raccoonforfriendica.domain.content.data

sealed interface NotificationType {
    data object Mention : NotificationType

    data object Reblog : NotificationType

    data object Follow : NotificationType

    data object FollowRequest : NotificationType

    data object Entry : NotificationType

    data object Favorite : NotificationType

    data object Poll : NotificationType

    data object Update : NotificationType

    data object Quote : NotificationType

    data object QuotedUpdate : NotificationType

    data object Unknown : NotificationType

    companion object {
        val ALL =
            listOf(
                Entry,
                Favorite,
                Follow,
                FollowRequest,
                Mention,
                Poll,
                Reblog,
                Update,
                Quote,
                QuotedUpdate,
            )
    }
}
