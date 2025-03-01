package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.NodeInfoUtils
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.core.utils.network.provideHttpClientEngine
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NodeInfoModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.RuleModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toModel
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

internal class DefaultNodeInfoRepository(
    private val provider: ServiceProvider,
    private val client: HttpClient = HttpClient(provideHttpClientEngine()),
) : NodeInfoRepository {
    override suspend fun getInfo(): NodeInfoModel? =
        withContext(Dispatchers.IO) {
            val instanceInfo =
                runCatching {
                    provider.instance.getInfo()
                }.getOrNull()

            val softwareName =
                runCatching {
                    extractSoftwareName()
                }.getOrNull()

            instanceInfo?.toModel()?.copy(
                software = softwareName,
            )
        }

    override suspend fun getRules(): List<RuleModel>? =
        withContext(Dispatchers.IO) {
            runCatching {
                provider.instance.getRules().map { it.toModel() }
            }.getOrNull()
        }

    private suspend fun extractSoftwareName(): String? {
        val linksJson =
            client.get("https://${provider.currentNode}/.well-known/nodeinfo").bodyAsText()
        val url =
            NodeInfoUtils
                .linksFromJson(linksJson)
                .links
                .lastOrNull()
                ?.href
                .orEmpty()
        val dataJson = client.get(url).bodyAsText()
        val data = NodeInfoUtils.dataFromJson(dataJson)
        val softwareInfo = data["software"] as? Map<*, *>
        return softwareInfo?.get("name").toString().trim('"')
    }
}
