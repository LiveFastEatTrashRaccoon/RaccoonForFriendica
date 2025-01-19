package com.livefast.eattrash.raccoonforfriendica.domain.content.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TranslatedTimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.SupportedFeatureRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TranslationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.MarkupMode

internal class DefaultGetTranslationUseCase(
    private val supportedFeatureRepository: SupportedFeatureRepository,
    private val defaultRepository: TranslationRepository,
    private val fallbackRepository: TranslationRepository,
    private val stripMarkup: StripMarkupUseCase,
) : GetTranslationUseCase {
    override suspend fun invoke(
        entry: TimelineEntryModel,
        targetLang: String,
    ): TranslatedTimelineEntryModel? {
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
        return nativeTranslation
            ?: fallbackRepository.getTranslation(
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
                        attachments = entry.attachments.map { att -> att.copy(description = att.description?.stripMarkup()) },
                    ),
                targetLang = targetLang,
            )
    }

    private fun String.stripMarkup(): String =
        stripMarkup(
            text = this,
            mode = MarkupMode.HTML,
        )
}
