package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.translation.TranslationProviderConfig
import com.livefast.eattrash.raccoonforfriendica.core.translation.TranslationProviderFactory
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TranslatedTimelineEntryModel

internal class DefaultFallbackTranslationRepository(
    private val translationProviderFactory: TranslationProviderFactory,
) : FallbackTranslationRepository {
    override suspend fun getTranslation(
        entry: TimelineEntryModel,
        targetLang: String,
        config: TranslationProviderConfig?,
    ): TranslatedTimelineEntryModel? = runCatching {
        TranslatedTimelineEntryModel(
            source = entry,
            target =
            entry.copy(
                content =
                entry.content.translate(
                    sourceLang = entry.lang,
                    targetLang = targetLang,
                    config = config,
                ),
                title =
                entry.title?.translate(
                    sourceLang = entry.lang,
                    targetLang = targetLang,
                    config = config,
                ) ?: entry.title,
                spoiler =
                entry.spoiler?.translate(
                    sourceLang = entry.lang,
                    targetLang = targetLang,
                    config = config,
                ) ?: entry.spoiler,
                card =
                entry.card?.let { card ->
                    card.copy(
                        title =
                        card.title.translate(
                            sourceLang = entry.lang,
                            targetLang = targetLang,
                            config = config,
                        ),
                        description =
                        card.description.translate(
                            sourceLang = entry.lang,
                            targetLang = targetLang,
                            config = config,
                        ),
                    )
                },
                attachments =
                entry.attachments.map { att ->
                    val translatedDescription =
                        att.description?.translate(
                            sourceLang = entry.lang,
                            targetLang = targetLang,
                            config = config,
                        )
                    att.copy(
                        description = translatedDescription ?: att.description,
                    )
                },
                poll =
                entry.poll?.copy(
                    options =
                    entry.poll
                        ?.options
                        ?.map { opt ->
                            val translatedTitle =
                                opt.title.translate(
                                    sourceLang = entry.lang,
                                    targetLang = targetLang,
                                    config = config,
                                )
                            opt.copy(title = translatedTitle)
                        }.orEmpty(),
                ),
            ),
            provider = config?.name.orEmpty(),
        )
    }.getOrNull()

    private suspend fun String.translate(
        sourceLang: String?,
        targetLang: String,
        config: TranslationProviderConfig?,
    ): String {
        if (sourceLang == null || config == null) {
            return this
        }
        val translationProvider = translationProviderFactory.create(config)
        return translationProvider.translate(this, sourceLang, targetLang)
    }
}
