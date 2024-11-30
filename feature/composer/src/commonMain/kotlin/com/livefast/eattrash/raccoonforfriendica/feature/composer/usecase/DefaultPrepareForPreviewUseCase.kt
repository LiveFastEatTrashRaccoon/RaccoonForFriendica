package com.livefast.eattrash.raccoonforfriendica.feature.composer.usecase

import com.livefast.eattrash.raccoonforfriendica.core.utils.detailName
import com.livefast.eattrash.raccoonforfriendica.core.utils.nodeName
import com.livefast.eattrash.raccoonforfriendica.core.utils.substituteAllOccurrences
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.MarkupMode
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ApiConfigurationRepository
import com.livefast.eattrash.raccoonforfriendica.feature.composer.converters.BBCodeConverter
import com.livefast.eattrash.raccoonforfriendica.feature.composer.converters.MarkdownConverter
import com.livefast.eattrash.raccoonforfriendica.feature.composer.utils.ComposerRegexes

internal class DefaultPrepareForPreviewUseCase(
    private val apiConfigurationRepository: ApiConfigurationRepository,
    private val bbCodeConverter: BBCodeConverter,
    private val markdownConverter: MarkdownConverter,
) : PrepareForPreviewUseCase {
    override fun invoke(
        text: String,
        mode: MarkupMode,
    ): String =
        when (mode) {
            MarkupMode.BBCode -> bbCodeConverter.toHtml(text)
            MarkupMode.Markdown -> markdownConverter.toHtml(text)
            else -> text
        }.withMentions().withHashtags()

    private fun String.withMentions(): String =
        ComposerRegexes.USER_MENTION.substituteAllOccurrences(this) { match ->
            val handle = match.groups["handlePrefix"]?.value.orEmpty()
            val currentNode = apiConfigurationRepository.node.value
            val node = handle.nodeName ?: currentNode
            val name = handle.detailName.orEmpty()
            val url =
                if (node == currentNode) {
                    "https://$node/profile/$name"
                } else {
                    "https://$node/users/$name"
                }
            append("<a href=\"$url\">@$handle</a>")
        }

    private fun String.withHashtags(): String =
        ComposerRegexes.HASHTAG.substituteAllOccurrences(this) { match ->
            val tag = match.groups["hashtag"]?.value.orEmpty()
            val node = apiConfigurationRepository.node.value
            val url = "https://$node/search?tag=$tag"
            append("<a href=\"$url\">#$tag</a>")
        }
}
