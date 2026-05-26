package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.NodeInfo
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.core.utils.network.provideHttpClientEngine
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NodeInfoModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.RuleModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toModel
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json

internal class DefaultNodeInfoRepository(
    private val provider: ServiceProvider,
    private val client: HttpClient = HttpClient(provideHttpClientEngine()),
    private val json: Json,
) : NodeInfoRepository {
    override suspend fun getInfo(): NodeInfoModel? {
        val instanceInfo =
            try {
                provider.instance.getInfo()
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                null
            }

        val softwareName =
            try {
                extractSoftwareName()
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                null
            }

        return instanceInfo?.toModel()?.copy(
            software = softwareName,
        )
    }

    override suspend fun getRules(): List<RuleModel>? = try {
        provider.instance.getRules().map { it.toModel() }
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        null
    }

    private suspend fun extractSoftwareName(): String {
        val linksJson =
            client.get("https://${provider.currentNode}/.well-known/nodeinfo").bodyAsText()
        val url =
            json.decodeFromString<NodeInfo>(linksJson)
                .links
                .lastOrNull()
                ?.href
                .orEmpty()
        val dataJson = client.get(url).bodyAsText()
        val data = json.decodeFromString<Map<String, Any?>>(dataJson)
        val softwareInfo = data["software"] as? Map<*, *>
        return softwareInfo?.get("name").toString().trim('"')
    }
}
