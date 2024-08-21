package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EditListForm(
    @SerialName("title") val title: String,
    @SerialName("replies_policy") val replyPolicy: String = "list",
    @SerialName("exclusive") val exclusive: Boolean = false,
)
