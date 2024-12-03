package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NodeInfoModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.RuleModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Single
internal class DefaultNodeInfoRepository(
    @Named("default") private val provider: ServiceProvider,
) : NodeInfoRepository {
    override suspend fun getInfo(): NodeInfoModel? =
        withContext(Dispatchers.IO) {
            runCatching {
                provider.instance.getInfo().toModel()
            }.getOrNull()
        }

    override suspend fun getRules(): List<RuleModel>? =
        withContext(Dispatchers.IO) {
            runCatching {
                provider.instance.getRules().map { it.toModel() }
            }.getOrNull()
        }
}
