package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TranslatedPostModel
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.ktor.client.request.forms.FormDataContent
import io.ktor.http.parameters
import io.ktor.utils.io.CancellationException

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class DefaultTranslationRepository(private val provider: ServiceProvider) : TranslationRepository {
    override suspend fun getTranslation(post: PostModel, targetLang: String): TranslatedPostModel? = try {
        val res =
            provider.status.translate(
                id = post.id,
                data =
                FormDataContent(
                    parameters {
                        append("lang", targetLang)
                    },
                ),
            )
        TranslatedPostModel(
            source = post,
            target =
            post.copy(
                content = res.content,
                attachments =
                post.attachments.map { att ->
                    val translatedDescription =
                        res.attachments.firstOrNull { it.id == att.id }?.description
                    att.copy(
                        description = translatedDescription ?: att.description,
                    )
                },
                poll =
                post.poll?.copy(
                    options =
                    post.poll
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
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        null
    }
}
