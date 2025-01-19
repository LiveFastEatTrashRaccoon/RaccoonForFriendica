package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.service.InnerTranslationService
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TranslatedTimelineEntryModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

internal class FallbackTranslationRepository(
    private val service: InnerTranslationService,
) : TranslationRepository {
    override suspend fun getTranslation(
        entry: TimelineEntryModel,
        targetLang: String,
    ): TranslatedTimelineEntryModel? =
        withContext(Dispatchers.IO) {
            runCatching {
                val translatedContent =
                    entry.content.translate(
                        sourceLang = entry.lang,
                        targetLang = targetLang,
                    )
                TranslatedTimelineEntryModel(
                    source = entry,
                    target =
                        entry.copy(
                            content = translatedContent ?: entry.content,
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
                                                opt.copy(
                                                    title = translatedTitle ?: opt.title,
                                                )
                                            }.orEmpty(),
                                ),
                            lang = targetLang,
                        ),
                    provider = PROVIDER_NAME,
                )
            }.getOrNull()
        }

    private suspend fun String.translate(
        sourceLang: String?,
        targetLang: String,
    ): String? =
        service.translate(
            sourceText = this,
            sourceLang = sourceLang,
            targetLang = targetLang,
    )

    companion object {
        private const val PROVIDER_NAME = "TAS"
    }
}
