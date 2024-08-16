package com.livefast.eattrash.raccoonforfriendica.feature.thread.data

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel

internal data class ConversationNode(
    val entry: TimelineEntryModel,
    val children: MutableList<ConversationNode> = mutableListOf(),
)
