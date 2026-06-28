package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Quote(
    @SerialName("state") val state: QuoteState? = null,
    @SerialName("quoted_status") val quotedStatus: Status? = null,
    @SerialName("quoted_status_id") val quotedStatusId: String? = null,
)

enum class QuoteState {
    @SerialName("pending")
    Pending,

    @SerialName("accepted")
    Accepted,

    @SerialName("rejected")
    Rejected,

    @SerialName("revoked")
    Revoked,

    @SerialName("deleted")
    Deleted,

    @SerialName("unauthorized")
    Unauthorized,

    @SerialName("blocked_account")
    BlockedAccount,

    @SerialName("blocked_domain")
    BlockedDomain,

    @SerialName("muted_account")
    MutedAccount,
}
