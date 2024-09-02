package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NodeInfoModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

internal class DefaultNodeInfoRepository(
    private val provider: ServiceProvider,
) : NodeInfoRepository {
    override suspend fun getInfo(): NodeInfoModel? =
        withContext(Dispatchers.IO) {
            runCatching {
                provider.instance.getInfo().toModel()
            }.getOrNull()
        }

    override suspend fun isFriendica(): Boolean =
        getInfo()
            ?.version
            .orEmpty()
            .contains(FRIENDICA_REGEX)

    companion object {
        private val FRIENDICA_REGEX =
            Regex("\\(compatible; Friendica (?<version>[a-zA-Z0-9.-_]*)\\)")
    }
}
