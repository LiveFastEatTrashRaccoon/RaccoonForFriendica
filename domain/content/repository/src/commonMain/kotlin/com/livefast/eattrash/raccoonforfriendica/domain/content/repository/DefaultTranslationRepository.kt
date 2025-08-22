package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TranslatedTimelineEntryModel
import io.ktor.client.request.forms.FormDataContent
import io.ktor.http.parameters

internal class DefaultTranslationRepository(private val provider: ServiceProvider) : TranslationRepository {
    override suspend fun getTranslation(entry: TimelineEntryModel, targetLang: String): TranslatedTimelineEntryModel? =
        runCatching {
            val res =
                provider.status.translate(
                    id = entry.id,
                    data =
                    FormDataContent(
                        parameters {
                            append("lang", targetLang)
                        },
                    ),
                )
            TranslatedTimelineEntryModel(
                source = entry,
                target =
                entry.copy(
                    content = res.content,
                    attachments =
                    entry.attachments.map { att ->
                        val translatedDescription =
                            res.attachments.firstOrNull { it.id == att.id }?.description
                        att.copy(
                            description = translatedDescription ?: att.description,
                        )
                    },
                    poll =
                    entry.poll?.copy(
                        options =
                        entry.poll
                            ?.options
                            ?.mapIndexed { idx, opt ->
                                val translatedTitle =
                                    res.poll
                                        ?.options
                                        ?.getOrNull(idx)
                                        ?.title
                                opt.copy(
                                    title = translatedTitle ?: opt.title,
                                )
                            }.orEmpty(),
                    ),
                    lang = targetLang,
                ),
                provider = res.provider,
            )
        }.getOrNull()
}
