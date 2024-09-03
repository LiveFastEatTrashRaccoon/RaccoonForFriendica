package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FriendicaPrivateMessage(
    @SerialName("id") val id: Long,
    @SerialName("sender") val sender: FriendicaContact? = null,
    @SerialName("sender_id") val senderId: Long? = null,
    @SerialName("recipient") val recipient: FriendicaContact? = null,
    @SerialName("recipient_id") val recipientId: Long? = null,
    @SerialName("text") val text: String? = null,
    @SerialName("title") val title: String? = null,
    @SerialName("sender_screen_name") val senderScreenName: String? = null,
    @SerialName("recipient_screen_name") val recipientScreenName: String? = null,
    @SerialName("friendica_seen") val seen: Boolean? = null,
    @SerialName("friendica_parent_uri") val parentUri: String? = null,
)
