package com.livefast.eattrash.raccoonforfriendica.domain.urlhandler

import androidx.compose.ui.platform.UriHandler
import com.livefast.eattrash.raccoonforfriendica.core.navigation.DetailOpener
import com.livefast.eattrash.raccoonforfriendica.core.utils.url.CustomTabsHelper
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.UrlOpeningMode
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor.EntryProcessor
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor.HashtagProcessor
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor.UserProcessor
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.Single

@Single
internal class DefaultCustomUriHandler(
    @InjectedParam private val defaultHandler: UriHandler,
    private val customTabsHelper: CustomTabsHelper,
    private val settingsRepository: SettingsRepository,
    private val detailOpener: DetailOpener,
    private val hashtagProcessor: HashtagProcessor,
    private val userProcessor: UserProcessor,
    private val entryProcessor: EntryProcessor,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : CustomUriHandler {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)

    override fun openUri(uri: String) {
        openUri(
            uri = uri,
            allowOpenExternal = true,
        )
    }

    override fun openUri(
        uri: String,
        allowOpenExternal: Boolean,
    ) {
        val urlOpeningMode =
            settingsRepository.current.value?.urlOpeningMode ?: UrlOpeningMode.External

        val processors =
            // careful: topmost items have higher priority
            listOf(
                hashtagProcessor,
                userProcessor,
                entryProcessor,
            )
        scope.launch {
            if (processors.none { it.process(uri) }) {
                if (allowOpenExternal) {
                    openExternalUrl(url = uri, mode = urlOpeningMode)
                }
            }
        }
    }

    private fun openExternalUrl(
        url: String,
        mode: UrlOpeningMode,
    ) {
        when {
            mode == UrlOpeningMode.Internal ->
                detailOpener.openInternalWebView(url)

            customTabsHelper.isSupported && mode == UrlOpeningMode.CustomTabs ->
                customTabsHelper.handle(url)

            else ->
                runCatching {
                    defaultHandler.openUri(url)
                }
        }
    }
}
