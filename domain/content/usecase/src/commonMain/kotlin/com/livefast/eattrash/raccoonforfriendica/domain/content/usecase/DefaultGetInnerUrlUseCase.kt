package com.livefast.eattrash.raccoonforfriendica.domain.content.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ApiConfigurationRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@ContributesBinding(AppScope::class)
@Inject
class DefaultGetInnerUrlUseCase(private val apiConfigurationRepository: ApiConfigurationRepository) :
    GetInnerUrlUseCase {
    override suspend fun invoke(post: PostModel): String? {
        val baseUrl =
            apiConfigurationRepository.node.value.takeIf { it.isNotEmpty() } ?: return null
        return buildString {
            append("https://")
            append(baseUrl)
            append("/search?q=${post.url}")
            append("&type=statuses")
            append("&resolve=true")
            append("&limit=1")
        }
    }
}
