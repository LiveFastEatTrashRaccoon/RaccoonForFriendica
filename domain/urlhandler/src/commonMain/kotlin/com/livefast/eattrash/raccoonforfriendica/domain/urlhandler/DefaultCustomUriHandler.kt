package com.livefast.eattrash.raccoonforfriendica.domain.urlhandler

import androidx.compose.ui.platform.UriHandler
import com.livefast.eattrash.raccoonforfriendica.core.navigation.MainRouter
import com.livefast.eattrash.raccoonforfriendica.core.utils.url.CustomTabsHelper
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.UrlOpeningMode
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor.EntryProcessor
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor.HashtagProcessor
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor.UserProcessor
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesBinding
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@AssistedInject
class DefaultCustomUriHandler(
    @Assisted private val fallbackHandler: UriHandler,
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
                try {
                    fallbackHandler.openUri(url)
                } catch (e: Exception) {
                    if (e is CancellationException) throw e
                    e.printStackTrace()
                }
        }
    }
}

@AssistedFactory
@ContributesBinding(AppScope::class)
interface DefaultCustomUriHandlerFactory : CustomUriHandlerFactory {
    override fun create(@Assisted fallbackHandler: UriHandler): DefaultCustomUriHandler
}
