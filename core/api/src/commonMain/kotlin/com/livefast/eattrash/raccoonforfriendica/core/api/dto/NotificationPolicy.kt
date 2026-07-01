package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class NotificationPolicy {
    @SerialName("all")
    ALL,

    @SerialName("followed")
    FOLLOWED,

    @SerialName("follower")
    FOLLOWER,

    @SerialName("none")
    NONE,
}

val NotificationPolicy.serialName: String
    get() = NotificationPolicy.serializer().descriptor.getElementName(ordinal)
