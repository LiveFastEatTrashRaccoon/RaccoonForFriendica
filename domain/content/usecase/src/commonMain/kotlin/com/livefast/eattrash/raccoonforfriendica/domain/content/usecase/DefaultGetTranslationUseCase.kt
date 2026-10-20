package com.livefast.eattrash.raccoonforfriendica.domain.content.usecase

import com.livefast.eattrash.raccoonforfriendica.core.translation.store.TranslationProviderConfigStore
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TranslatedPostModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.FallbackTranslationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.SupportedFeatureRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TranslationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.MarkupMode
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@ContributesBinding(AppScope::class)
@Inject
class DefaultGetTranslationUseCase(
    private val supportedFeatureRepository: SupportedFeatureRepository,
    private val defaultRepository: TranslationRepository,
    private val fallbackRepository: FallbackTranslationRepository,
    private val stripMarkup: StripMarkupUseCase,
    private val translationProviderConfigStore: TranslationProviderConfigStore,
) : GetTranslationUseCase {
    override suspend fun invoke(post: PostModel, targetLang: String): TranslatedPostModel? {
        val nativeTranslationSupported =
            supportedFeatureRepository.features.value.supportsTranslation
        val nativeTranslation =
            if (nativeTranslationSupported) {
                defaultRepository.getTranslation(
                    post = post,
                    targetLang = targetLang,
                )
            } else {
                null
            }
        return nativeTranslation ?: getFallbackTranslation(post, targetLang)
    }

    private suspend fun getFallbackTranslation(post: PostModel, targetLang: String): TranslatedPostModel? {
        val configId = translationProviderConfigStore.getDefaultId() ?: return null
        val providerConfig = translationProviderConfigStore.getById(configId) ?: return null
        return fallbackRepository.getTranslation(
            config = providerConfig,
            targetLang = targetLang,
            post =
            post.copy(
                title = post.title?.stripMarkup(),
                content = post.content.stripMarkup(),
                spoiler = post.spoiler?.stripMarkup(),
                card =
                post.card?.let { card ->
                    card.copy(
                        title = card.title.stripMarkup(),
                        description = card.description.stripMarkup(),
                    )
                },
                poll =
                post.poll?.let { poll ->
                    poll.copy(
                        options = poll.options.map { opt -> opt.copy(title = opt.title.stripMarkup()) },
                    )
                },
                attachments = post.attachments.map { att ->
                    att.copy(description = att.description?.stripMarkup())
                },
            ),
        )
    }

    private fun String.stripMarkup(): String = stripMarkup(
        text = this,
        mode = MarkupMode.HTML,
    )
}
