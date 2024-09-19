package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Search(
    @SerialName("accounts") val accounts: List<Account> = emptyList(),
    @SerialName("hashtags") val hashtags: List<Tag> = emptyList(),
    @SerialName("statuses") val statuses: List<Status> = emptyList(),
)
