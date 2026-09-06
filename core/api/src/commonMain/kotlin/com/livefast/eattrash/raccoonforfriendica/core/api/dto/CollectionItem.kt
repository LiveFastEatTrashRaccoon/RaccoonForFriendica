package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CollectionItem(
    @SerialName("account_id") val accountId: String,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("id") val id: String,
    @SerialName("state") val state: CollectionItemState = CollectionItemState.Accepted,
)

enum class CollectionItemState {
    @SerialName("pending")
    Pending,

    @SerialName("accepted")
    Accepted,

    @SerialName("rejected")
    Rejected,

    @SerialName("revoked")
    Revoked,
}
