package com.livefast.eattrash.raccoonforfriendica.domain.content.data

import kotlin.jvm.Transient

data class CircleModel(
    val id: String,
    val name: String = "",
    val replyPolicy: CircleReplyPolicy = CircleReplyPolicy.List,
    val exclusive: Boolean = false,
    @Transient
    val editable: Boolean = false,
)
