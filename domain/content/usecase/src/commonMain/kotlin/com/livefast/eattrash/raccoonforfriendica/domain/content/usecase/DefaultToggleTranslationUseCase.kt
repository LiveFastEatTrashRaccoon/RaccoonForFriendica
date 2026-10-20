package com.livefast.eattrash.raccoonforfriendica.domain.content.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@ContributesBinding(AppScope::class)
@Inject
class DefaultToggleTranslationUseCase(private val getTranslation: GetTranslationUseCase) : ToggleTranslationUseCase {
    override suspend operator fun invoke(post: PostModel, targetLang: String): PostModel {
        val translation: PostModel?
        val provider: String?
        when {
            !post.isShowingTranslation && post.translation == null -> {
                val result = getTranslation(post = post, targetLang = targetLang)
                translation = result?.target
                provider = result?.provider
            }

            else -> {
                translation = post.translation
                provider = post.translationProvider
            }
        }
        return post.copy(
            isShowingTranslation = translation != null && !post.isShowingTranslation,
            translation = translation,
            translationProvider = provider.takeIf { translation != null },
            translationLoading = false,
        )
    }
}
