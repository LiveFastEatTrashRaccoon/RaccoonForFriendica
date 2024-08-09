package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase

import androidx.compose.ui.platform.UriHandler
import com.livefast.eattrash.raccoonforfriendica.core.navigation.DetailOpener
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ApiConfigurationRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

internal class DefaultOpenUrlUseCase(
    private val defaultHandler: UriHandler,
    private val apiConfigurationRepository: ApiConfigurationRepository,
    private val userRepository: UserRepository,
    private val detailOpener: DetailOpener,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : OpenUrlUseCase {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)

    override operator fun invoke(url: String) {
        val currentNode = apiConfigurationRepository.node.value
        val tagPrefix = "https://$currentNode/search?tag="

        when {
            url.startsWith(tagPrefix) -> {
                val tag = url.replace(tagPrefix, "")
                detailOpener.openHashtag(tag)
            }

            USER_REGEX.matches(url) -> {
                USER_REGEX.find(url)?.groups?.also { group ->
                    val (node, user) = group["instance"]?.value.orEmpty() to group["detail"]?.value.orEmpty()
                    scope.launch {
                        val handle = "$user@$node"
                        val userId = userRepository.getByHandle(handle)
                        if (userId != null) {
                            detailOpener.openUserDetail(userId.id)
                        }
                    }
                }
            }

            else -> defaultHandler.openUri(url)
        }
    }

    companion object {
        private const val DETAIL_FRAGMENT: String = "[a-zA-Z0-9_]{3,}"

        private const val INSTANCE_FRAGMENT: String =
            "([a-zA-Z0-9][a-zA-Z0-9-]{0,61}[a-zA-Z0-9]\\.)+[a-zA-Z]{2,}"

        private val USER_REGEX =
            Regex("https://(?<instance>$INSTANCE_FRAGMENT)/users/(?<detail>$DETAIL_FRAGMENT)")
    }
}
