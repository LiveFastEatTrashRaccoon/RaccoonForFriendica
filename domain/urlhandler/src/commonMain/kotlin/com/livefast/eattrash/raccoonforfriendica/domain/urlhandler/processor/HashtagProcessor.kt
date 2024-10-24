package com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor

import com.livefast.eattrash.raccoonforfriendica.core.navigation.DetailOpener
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ApiConfigurationRepository

internal interface HashtagProcessor : UrlProcessor

internal class DefaultHashtagProcessor(
    private val apiConfigurationRepository: ApiConfigurationRepository,
    private val detailOpener: DetailOpener,
) : HashtagProcessor {
    override suspend fun process(uri: String): Boolean {
        val currentNode = apiConfigurationRepository.node.value
        val tagPrefix = "https://$currentNode/search?tag="
        if (!uri.startsWith(tagPrefix)) {
            return false
        }

        val tag = uri.replace(tagPrefix, "")
        detailOpener.openHashtag(tag)
        return true
    }
}
