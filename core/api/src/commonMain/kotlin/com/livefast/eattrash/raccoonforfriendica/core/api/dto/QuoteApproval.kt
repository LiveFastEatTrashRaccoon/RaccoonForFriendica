package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuoteApproval(
    @SerialName("automatic")
    val automatic: List<QuotePolicy> = emptyList(),
    @SerialName("manual")
    val manual: List<QuotePolicy> = emptyList(),
    @SerialName("current_user")
    val currentUser: QuotePolicyForCurrentUser? = null,
)

@Serializable
enum class QuotePolicy {
    @SerialName("public")
    Public,

    @SerialName("followers")
    Followers,

    @SerialName("following")
    Following,

    @SerialName("nobody")
    Nobody,

    @SerialName("unsupported_policy")
    Unsupported,
}

enum class QuotePolicyForCurrentUser {
    @SerialName("automatic")
    Automatic,

    @SerialName("manual")
    Manual,

    @SerialName("denied")
    Denied,

    @SerialName("unknown")
    Unknown,
}

val QuotePolicy.serialName: String
    get() = QuotePolicy.serializer().descriptor.getElementName(ordinal)
