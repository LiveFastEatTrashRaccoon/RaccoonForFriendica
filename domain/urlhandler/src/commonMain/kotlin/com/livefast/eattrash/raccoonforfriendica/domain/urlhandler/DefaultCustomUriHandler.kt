package com.livefast.eattrash.raccoonforfriendica.domain.urlhandler

import androidx.compose.ui.platform.UriHandler
import com.livefast.eattrash.raccoonforfriendica.core.navigation.MainRouter
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

internal class DefaultCustomUriHandler(
    private val defaultHandler: UriHandler,
    private val customTabsHelper: CustomTabsHelper,
    private val settingsRepository: SettingsRepository,
    private val mainRouter: MainRouter,
    private val hashtagProcessor: HashtagProcessor,
    private val userProcessor: UserProcessor,
    private val entryProcessor: EntryProcessor,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : CustomUriHandler {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)

    override fun openUri(uri: String) {
        openUri(
            uri = uri,
            allowOpenInternal = true,
            allowOpenExternal = true,
        )
    }

    override fun openUri(uri: String, allowOpenExternal: Boolean, allowOpenInternal: Boolean) {
        val urlOpeningMode =
            settingsRepository.current.value?.urlOpeningMode ?: UrlOpeningMode.External

        val processors =
            buildList {
                // careful: topmost items have higher priority
                if (allowOpenInternal) {
                    this += hashtagProcessor
                    this += userProcessor
                    this += entryProcessor
                }
            }
        scope.launch {
            if (processors.none { it.process(uri) }) {
                if (allowOpenExternal) {
                    openExternalUrl(url = uri, mode = urlOpeningMode)
                }
            }
        }
    }

    private fun openExternalUrl(url: String, mode: UrlOpeningMode) {
        when {
            mode == UrlOpeningMode.Internal ->
                mainRouter.openInternalWebView(url)

            customTabsHelper.isSupported && mode == UrlOpeningMode.CustomTabs ->
                customTabsHelper.handle(url)

            else ->
                runCatching {
                    defaultHandler.openUri(url)
                }
        }
    }
}
