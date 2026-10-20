package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel

interface ReplyHelper {
    suspend fun PostModel.withInReplyToIfMissing(): PostModel
}
