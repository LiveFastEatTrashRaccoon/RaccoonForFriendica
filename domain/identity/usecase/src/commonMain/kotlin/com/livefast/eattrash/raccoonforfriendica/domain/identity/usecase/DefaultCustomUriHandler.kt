package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase

import androidx.compose.ui.platform.UriHandler
import com.livefast.eattrash.raccoonforfriendica.core.utils.url.CustomTabsHelper
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.UrlOpeningMode
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.urlprocessor.ExternalUserProcessor
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.urlprocessor.FriendicaUserProcessor
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.urlprocessor.GuppeProcessor
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.urlprocessor.HashtagProcessor
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.urlprocessor.LemmyProcessor
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.urlprocessor.MastodonUserProcessor
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.urlprocessor.PeertubeProcessor
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.urlprocessor.PixelfedProcessor
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

internal class DefaultCustomUriHandler(
    private val defaultHandler: UriHandler,
    private val customTabsHelper: CustomTabsHelper,
    private val settingsRepository: SettingsRepository,
    private val hashtagProcessor: HashtagProcessor,
    private val friendicaUserProcessor: FriendicaUserProcessor,
    private val externalUserProcessor: ExternalUserProcessor,
    private val mastodonUserProcessor: MastodonUserProcessor,
    private val lemmyProcessor: LemmyProcessor,
    private val guppeProcessor: GuppeProcessor,
    private val peertubeProcessor: PeertubeProcessor,
    private val pixelfedProcessor: PixelfedProcessor,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : CustomUriHandler {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)

    override fun openUri(uri: String) {
        val urlOpeningMode =
            settingsRepository.current.value?.urlOpeningMode ?: UrlOpeningMode.External

        // careful: the order is significant, topmost processor have priority
        val processors =
            listOf(
                hashtagProcessor,
                friendicaUserProcessor,
                externalUserProcessor,
                mastodonUserProcessor,
                guppeProcessor,
                lemmyProcessor,
                peertubeProcessor,
                pixelfedProcessor,
            )
        scope.launch {
            if (processors.none { it.process(uri) }) {
                openUrl(url = uri, mode = urlOpeningMode)
            }
        }
    }

    private fun openUrl(
        url: String,
        mode: UrlOpeningMode,
    ) {
        when {
            customTabsHelper.isSupported && mode == UrlOpeningMode.CustomTabs ->
                customTabsHelper.handle(url)

            else ->
                runCatching {
                    defaultHandler.openUri(url)
                }
        }
    }
}
