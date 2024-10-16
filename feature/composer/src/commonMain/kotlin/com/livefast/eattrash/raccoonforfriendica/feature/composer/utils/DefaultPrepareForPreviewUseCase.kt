package com.livefast.eattrash.raccoonforfriendica.feature.composer.utils

import com.livefast.eattrash.raccoonforfriendica.core.utils.nodeName
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.MarkupMode
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ApiConfigurationRepository

internal class DefaultPrepareForPreviewUseCase(
    private val apiConfigurationRepository: ApiConfigurationRepository,
) : PrepareForPreviewUseCase {
    override fun invoke(
        text: String,
        mode: MarkupMode,
    ): String =
        run {
            when (mode) {
                MarkupMode.BBCode -> text.convertBBCodeToHtml()
                MarkupMode.Markdown -> text.convertMarkdownToHtml()
                else -> text
            }
        }.withMentions().withHashtags()

    private fun String.convertBBCodeToHtml(): String =
        replace("[u]", "<u>")
            .replace("[/u]", "</u>")
            .replace("[s]", "<s>")
            .replace("[/s]", "</s>")
            .replace("[i]", "<i>")
            .replace("[/i]", "</i>")
            .replace("[b]", "<b>")
            .replace("[/b]", "</b>")
            .replace("[code]", "<code>")
            .replace("[/code]", "</code>")
            .replace("[ul]", "<ul>")
            .replace("[/ul]", "</ul>")
            .replace("[ol]", "<ol>")
            .replace("[/ol]", "</ol>")
            .replace("[li]", "<li>")
            .replace("[/li]", "</li>")
            .replace(Regex("\n\n"), "<br /><br />")
            .replace("\n", " ")
            .run {
                ComposerRegexes.SHARE.replaceAllOccurrences(this) { match ->
                    val url = match.groups["url"]?.value.orEmpty()
                    append("<a href=\"$url\">#$url</a>")
                }
            }

    private fun String.convertMarkdownToHtml(): String =
        run {
            Regex("~~(?<content>.*?)~~").replaceAllOccurrences(this) { match ->
                val content = match.groups["content"]?.value.orEmpty()
                append("<s>$content</s>")
            }
        }.run {
            Regex("_(?<content>.*?)_").replaceAllOccurrences(this) { match ->
                val content = match.groups["content"]?.value.orEmpty()
                append("<i>$content</i>")
            }
        }.run {
            Regex("\\*\\*(?<content>.*?)\\*\\*").replaceAllOccurrences(this) { match ->
                val content = match.groups["content"]?.value.orEmpty()
                append("<b>$content</b>")
            }
        }.run {
            Regex("`(?<content>.*?)`").replaceAllOccurrences(this) { match ->
                val content = match.groups["content"]?.value.orEmpty()
                append("<code>$content</code>")
            }
        }.run {
            Regex("\\[(?<anchor>.*?)]\\((?<url>.*?)\\)").replaceAllOccurrences(this) { match ->
                val anchor = match.groups["anchor"]?.value.orEmpty()
                val url = match.groups["url"]?.value.orEmpty()
                append("<a href=\"$url\">$anchor</a>")
            }
        }.replace("<ul>", "\n")
            .replace("</ul>", "\n")
            .replace("<ol>", "\n")
            .replace("</ol>", "\n")
            .run {
                Regex("^- (?<content>.*?)$").replaceAllOccurrences(this) { match ->
                    val content = match.groups["content"]?.value.orEmpty()
                    append("<li>$content</li>")
                }
            }.run {
                ComposerRegexes.SHARE.replaceAllOccurrences(this) { match ->
                    val url = match.groups["url"]?.value.orEmpty()
                    append(url)
                }
            }

    private fun String.withMentions(): String =
        also { original ->
            val matches = ComposerRegexes.USER_MENTION.findAll(original).toList()
            buildString {
                var index = 0
                for (match in matches) {
                    val range = match.range
                    append(original.substring(index, range.first))
                    val handle = match.groupValues.firstOrNull().orEmpty()
                    val currentNode = apiConfigurationRepository.node.value
                    val node = handle.nodeName ?: currentNode
                    val name = handle.substringBefore("@")
                    val url =
                        if (node == currentNode) {
                            "https://$node/profile/$name"
                        } else {
                            "https://$node/users/$name"
                        }
                    append("<a href=\"$url\">#$handle</a>")
                    index = range.last + 1
                }
                if (index < original.length) {
                    append(original.substring(index, original.length))
                }
            }
        }

    private fun String.withHashtags(): String =
        also { original ->
            val matches = ComposerRegexes.HASHTAG.findAll(original).toList()
            buildString {
                var index = 0
                for (match in matches) {
                    val range = match.range
                    append(original.substring(index, range.first))
                    val tag = match.groupValues.firstOrNull().orEmpty()
                    val node = apiConfigurationRepository.node.value
                    val url = "https://$node/search?tag=$tag"
                    append("<a href=\"$url\">#$tag</a>")
                    index = range.last + 1
                }
                if (index < original.length) {
                    append(original.substring(index, original.length))
                }
            }
        }
}

private fun Regex.replaceAllOccurrences(
    original: String,
    onMatchResult: StringBuilder.(MatchResult) -> Unit,
): String =
    buildString {
        val matches = findAll(original).toList()
        var index = 0
        for (match in matches) {
            val range = match.range
            append(original.substring(index, range.first))
            onMatchResult(match)
            index = range.last + 1
        }
        if (index < original.length) {
            append(original.substring(index, original.length))
        }
    }
