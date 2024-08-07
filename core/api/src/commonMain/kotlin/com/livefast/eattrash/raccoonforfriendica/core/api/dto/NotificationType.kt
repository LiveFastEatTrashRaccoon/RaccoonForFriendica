package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName

enum class NotificationType {
    @SerialName("mention")
    MENTION,

    @SerialName("status")
    STATUS,

    @SerialName("reblog")
    REBLOG,

    @SerialName("follow")
    FOLLOW,

    @SerialName("follow_request")
    FOLLOW_REQUEST,

    @SerialName("favourite")
    FAVOURITE,

    @SerialName("poll")
    POLL,

    @SerialName("update")
    UPDATE,

    @SerialName("admin.sign_up")
    ADMIN_SIGN_UP,

    @SerialName("admin.report")
    ADMIN_REPORT,

    @SerialName("severed_relationships")
    SEVERED_RELATIONSHIPS,

    @SerialName("moderation_warning")
    MODERATION_WARNING,
}
