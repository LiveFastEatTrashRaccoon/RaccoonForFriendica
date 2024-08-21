package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EditListMembersForm(
    @SerialName("account_ids") val accountIds: List<String>,
)
