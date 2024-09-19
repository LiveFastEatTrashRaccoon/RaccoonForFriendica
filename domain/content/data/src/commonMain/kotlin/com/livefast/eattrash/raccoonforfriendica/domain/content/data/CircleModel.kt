package com.livefast.eattrash.raccoonforfriendica.domain.content.data

import kotlin.jvm.Transient

data class CircleModel(
    val exclusive: Boolean = false,
    val id: String,
    @Transient val editable: Boolean = false,
    val name: String = "",
    val replyPolicy: CircleReplyPolicy = CircleReplyPolicy.List,
)
