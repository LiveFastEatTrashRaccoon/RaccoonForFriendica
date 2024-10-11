package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase

import androidx.compose.ui.platform.UriHandler
import com.livefast.eattrash.raccoonforfriendica.core.navigation.DetailOpener
import com.livefast.eattrash.raccoonforfriendica.core.utils.url.CustomTabsHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.UrlOpeningMode
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ApiConfigurationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

internal class DefaultCustomUriHandler(
    private val defaultHandler: UriHandler,
    private val apiConfigurationRepository: ApiConfigurationRepository,
    private val userRepository: UserRepository,
    private val detailOpener: DetailOpener,
    private val customTabsHelper: CustomTabsHelper,
    private val settingsRepository: SettingsRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : CustomUriHandler {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)

    override fun openUri(uri: String) {
        val currentNode = apiConfigurationRepository.node.value
        val tagPrefix = "https://$currentNode/search?tag="
        val profilePrefix = "https://$currentNode/profile/"
        val urlOpeningMode =
            settingsRepository.current.value?.urlOpeningMode ?: UrlOpeningMode.External

        when {
            uri.startsWith(tagPrefix) -> {
                val tag = uri.replace(tagPrefix, "")
                detailOpener.openHashtag(tag)
            }

            uri.startsWith(profilePrefix) -> {
                val user = uri.replace(profilePrefix, "")
                scope.launch {
                    val remoteUser = getRemoteUser("$user@$currentNode")
                    if (remoteUser != null) {
                        detailOpener.openUserDetail(remoteUser)
                    } else {
                        openUrl(url = uri, mode = urlOpeningMode)
                    }
                }
            }

            EXTERNAL_USER_REGEX.matches(uri) -> {
                EXTERNAL_USER_REGEX.find(uri)?.groups?.also { group ->
                    val (node, user) = group["instance"]?.value.orEmpty() to group["detail"]?.value.orEmpty()
                    scope.launch {
                        val remoteUser = getRemoteUser("$user@$node")
                        if (remoteUser != null) {
                            detailOpener.openUserDetail(remoteUser)
                        } else {
                            openUrl(url = uri, mode = urlOpeningMode)
                        }
                    }
                }
            }

            LEMMY_USER_REGEX.matches(uri) -> {
                LEMMY_USER_REGEX.find(uri)?.groups?.also { group ->
                    val (node, user) = group["instance"]?.value.orEmpty() to group["detail"]?.value.orEmpty()
                    scope.launch {
                        val remoteUser = getRemoteUser("$user@$node")
                        if (remoteUser != null) {
                            detailOpener.openUserDetail(remoteUser)
                        } else {
                            openUrl(url = uri, mode = urlOpeningMode)
                        }
                    }
                }
            }

            LEMMY_COMMUNITY_REGEX.matches(uri) -> {
                LEMMY_COMMUNITY_REGEX.find(uri)?.groups?.also { group ->
                    val (node, user) = group["instance"]?.value.orEmpty() to group["detail"]?.value.orEmpty()
                    scope.launch {
                        val remoteUser = getRemoteUser("$user@$node")
                        if (remoteUser != null) {
                            detailOpener.openUserDetail(remoteUser)
                        } else {
                            openUrl(url = uri, mode = urlOpeningMode)
                        }
                    }
                }
            }

            else -> openUrl(url = uri, mode = urlOpeningMode)
        }
    }

    private suspend fun getRemoteUser(handle: String): UserModel? =
        withTimeoutOrNull(USER_SEARCH_TIMEOUT_MILLIS) {
            userRepository.getByHandle(handle)
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

    companion object {
        private const val USER_SEARCH_TIMEOUT_MILLIS = 1000L

        private const val DETAIL_FRAGMENT: String = "[a-zA-Z0-9_]{3,}"

        private const val INSTANCE_FRAGMENT: String =
            "([a-zA-Z0-9][a-zA-Z0-9-]{0,61}[a-zA-Z0-9]\\.)+[a-zA-Z]{2,}"

        private val EXTERNAL_USER_REGEX =
            Regex("https://(?<instance>$INSTANCE_FRAGMENT)/users/(?<detail>$DETAIL_FRAGMENT)")
        private val LEMMY_USER_REGEX =
            Regex("https://(?<instance>$INSTANCE_FRAGMENT)/u/(?<detail>$DETAIL_FRAGMENT)")
        private val LEMMY_COMMUNITY_REGEX =
            Regex("https://(?<instance>$INSTANCE_FRAGMENT)/c/(?<detail>$DETAIL_FRAGMENT)")
    }
}
