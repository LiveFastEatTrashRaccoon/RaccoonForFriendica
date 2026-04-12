package com.livefast.eattrash.raccoonforfriendica.domain.content.usecase

import com.livefast.eattrash.raccoonforfriendica.core.translation.store.TranslationProviderConfigStore
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TranslatedTimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.FallbackTranslationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.SupportedFeatureRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TranslationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.MarkupMode

internal class DefaultGetTranslationUseCase(
    private val supportedFeatureRepository: SupportedFeatureRepository,
    private val defaultRepository: TranslationRepository,
    private val fallbackRepository: FallbackTranslationRepository,
    private val stripMarkup: StripMarkupUseCase,
    private val translationProviderConfigStore: TranslationProviderConfigStore,
) : GetTranslationUseCase {
    override suspend fun invoke(entry: TimelineEntryModel, targetLang: String): TranslatedTimelineEntryModel? {
        val nativeTranslationSupported =
            supportedFeatureRepository.features.value.supportsTranslation
        val nativeTranslation =
            if (nativeTranslationSupported) {
                defaultRepository.getTranslation(
                    entry = entry,
                    targetLang = targetLang,
                )
            } else {
                null
            }
        return nativeTranslation ?: getFallbackTranslation(entry, targetLang)
    }

    private suspend fun getFallbackTranslation(
        entry: TimelineEntryModel,
        targetLang: String,
    ): TranslatedTimelineEntryModel? {
        val configId = translationProviderConfigStore.getDefaultId() ?: return null
        val providerConfig = translationProviderConfigStore.getById(configId) ?: return null
        return fallbackRepository.getTranslation(
            config = providerConfig,
            targetLang = targetLang,
            entry =
            entry.copy(
                title = entry.title?.stripMarkup(),
                content = entry.content.stripMarkup(),
                spoiler = entry.spoiler?.stripMarkup(),
                card =
                entry.card?.let { card ->
                    card.copy(
                        title = card.title.stripMarkup(),
                        description = card.description.stripMarkup(),
                    )
                },
                poll =
                entry.poll?.let { poll ->
                    poll.copy(
                        options = poll.options.map { opt -> opt.copy(title = opt.title.stripMarkup()) },
                    )
                },
                attachments = entry.attachments.map { att ->
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
