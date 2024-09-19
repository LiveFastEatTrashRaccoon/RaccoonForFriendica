package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EditListForm(
    @SerialName("exclusive") val exclusive: Boolean = false,
    @SerialName("replies_policy") val replyPolicy: String = "list",
    @SerialName("title") val title: String,
)
