package com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor

import com.livefast.eattrash.raccoonforfriendica.core.navigation.DetailOpener
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor.UriHandlerConstants.DETAIL_FRAGMENT
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor.UriHandlerConstants.INSTANCE_FRAGMENT

internal interface HashtagProcessor : UrlProcessor

internal class DefaultHashtagProcessor(
    private val detailOpener: DetailOpener,
) : HashtagProcessor {
    override suspend fun process(uri: String): Boolean {
        val tag =
            listOfNotNull(
                REGEX_1.find(uri)?.groups?.let { it["tag"]?.value },
                REGEX_2.find(uri)?.groups?.let { it["tag"]?.value },
            ).firstOrNull() ?: return false

        detailOpener.openHashtag(tag)
        return true
    }

    companion object {
        private val REGEX_1 =
            Regex("https://(?<instance>$INSTANCE_FRAGMENT)/search\\?tag=(?<tag>$DETAIL_FRAGMENT)")
        private val REGEX_2 =
            Regex("https://(?<instance>$INSTANCE_FRAGMENT)/tags/(?<tag>$DETAIL_FRAGMENT)")
    }
}
