package com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor

import com.livefast.eattrash.raccoonforfriendica.core.navigation.MainRouter
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor.UriHandlerConstants.DETAIL_FRAGMENT
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor.UriHandlerConstants.INSTANCE_FRAGMENT
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal interface HashtagProcessor : UrlProcessor

internal class DefaultHashtagProcessor(
    private val mainRouter: MainRouter,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main,
) : HashtagProcessor {
    override suspend fun process(uri: String): Boolean {
        val tag =
            listOfNotNull(
                REGEX_1.find(uri)?.groups?.let { it["tag"]?.value },
                REGEX_2.find(uri)?.groups?.let { it["tag"]?.value },
            ).firstOrNull() ?: return false
        withContext(dispatcher) {
            mainRouter.openHashtag(tag)
        }
        return true
    }

    companion object {
        private val REGEX_1 =
            Regex("https://(?<instance>$INSTANCE_FRAGMENT)/search\\?tag=(?<tag>$DETAIL_FRAGMENT)")
        private val REGEX_2 =
            Regex("https://(?<instance>$INSTANCE_FRAGMENT)/tags/(?<tag>$DETAIL_FRAGMENT)")
    }
}
