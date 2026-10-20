package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.translation.TranslationProviderConfig
import com.livefast.eattrash.raccoonforfriendica.core.translation.TranslationProviderFactory
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TranslatedPostModel
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.ktor.utils.io.CancellationException

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class DefaultFallbackTranslationRepository(private val translationProviderFactory: TranslationProviderFactory) :
    FallbackTranslationRepository {
    override suspend fun getTranslation(
        post: PostModel,
        targetLang: String,
        config: TranslationProviderConfig?,
    ): TranslatedPostModel? = try {
        TranslatedPostModel(
            source = post,
            target =
            post.copy(
                content =
                post.content.translate(
                    sourceLang = post.lang,
                    targetLang = targetLang,
                    config = config,
                ),
                title =
                post.title?.translate(
                    sourceLang = post.lang,
                    targetLang = targetLang,
                    config = config,
                ) ?: post.title,
                spoiler =
                post.spoiler?.translate(
                    sourceLang = post.lang,
                    targetLang = targetLang,
                    config = config,
                ) ?: post.spoiler,
                card =
                post.card?.let { card ->
                    card.copy(
                        title =
                        card.title.translate(
                            sourceLang = post.lang,
                            targetLang = targetLang,
                            config = config,
                        ),
                        description =
                        card.description.translate(
                            sourceLang = post.lang,
                            targetLang = targetLang,
                            config = config,
                        ),
                    )
                },
                attachments =
                post.attachments.map { att ->
                    val translatedDescription =
                        att.description?.translate(
                            sourceLang = post.lang,
                            targetLang = targetLang,
                            config = config,
                        )
                    att.copy(
                        description = translatedDescription ?: att.description,
                    )
                },
                poll =
                post.poll?.copy(
                    options =
                    post.poll
                        ?.options
                        ?.map { opt ->
                            val translatedTitle =
                                opt.title.translate(
                                    sourceLang = post.lang,
                                    targetLang = targetLang,
                                    config = config,
                                )
                            opt.copy(title = translatedTitle)
                        }.orEmpty(),
                ),
            ),
            provider = config?.name.orEmpty(),
        )
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        null
    }

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
