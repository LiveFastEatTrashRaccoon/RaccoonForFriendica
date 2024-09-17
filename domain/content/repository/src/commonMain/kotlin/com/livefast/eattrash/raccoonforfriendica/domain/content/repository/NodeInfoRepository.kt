package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NodeInfoModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.RuleModel

interface NodeInfoRepository {
    suspend fun getInfo(): NodeInfoModel?

    suspend fun getRules(): List<RuleModel>?
}
