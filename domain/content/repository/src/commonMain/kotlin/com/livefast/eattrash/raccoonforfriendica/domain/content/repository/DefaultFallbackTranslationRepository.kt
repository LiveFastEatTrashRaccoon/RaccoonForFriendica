package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TranslatedTimelineEntryModel

internal class DefaultFallbackTranslationRepository : FallbackTranslationRepository {
    override suspend fun getTranslation(
        entry: TimelineEntryModel,
        targetLang: String,
        config: FallbackTranslationProviderConfig?,
    ): TranslatedTimelineEntryModel? = runCatching {
        TranslatedTimelineEntryModel(
            source = entry,
            target =
            entry.copy(
                content =
                entry.content.translate(
                    sourceLang = entry.lang,
                    targetLang = targetLang,
                ),
                title =
                entry.title?.translate(
                    sourceLang = entry.lang,
                    targetLang = targetLang,
                ) ?: entry.title,
                spoiler =
                entry.spoiler?.translate(
                    sourceLang = entry.lang,
                    targetLang = targetLang,
                ) ?: entry.spoiler,
                card =
                entry.card?.let { card ->
                    card.copy(
                        title =
                        card.title.translate(
                            sourceLang = entry.lang,
                            targetLang = targetLang,
                        ),
                        description =
                        card.description.translate(
                            sourceLang = entry.lang,
                            targetLang = targetLang,
                        ),
                    )
                },
                attachments =
                entry.attachments.map { att ->
                    val translatedDescription =
                        att.description?.translate(
                            sourceLang = entry.lang,
                            targetLang = targetLang,
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
                                )
                            opt.copy(title = translatedTitle)
                        }.orEmpty(),
                ),
            ),
            provider = config?.name.orEmpty(),
        )
    }.getOrNull()

    private suspend fun String.translate(sourceLang: String?, targetLang: String): String =
        // TODO: implement translation
        this
}
