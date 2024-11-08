package com.livefast.eattrash.raccoonforfriendica.feature.composer.utils

import com.livefast.eattrash.raccoonforfriendica.core.utils.detailName
import com.livefast.eattrash.raccoonforfriendica.core.utils.nodeName
import com.livefast.eattrash.raccoonforfriendica.core.utils.substituteAllOccurrences
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.MarkupMode
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ApiConfigurationRepository

internal class DefaultPrepareForPreviewUseCase(
    private val apiConfigurationRepository: ApiConfigurationRepository,
) : PrepareForPreviewUseCase {
    override fun invoke(
        text: String,
        mode: MarkupMode,
    ): String =
        when (mode) {
            MarkupMode.BBCode -> text.convertBBCodeToHtml()
            MarkupMode.Markdown -> text.convertMarkdownToHtml()
            else -> text.replaceNewlines()
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
            .run {
                ComposerRegexes.BBCODE_SHARE.substituteAllOccurrences(this) { match ->
                    val url = match.groups["url"]?.value.orEmpty()
                    append("<a href=\"$url\">$url</a>")
                }
            }.run {
                ComposerRegexes.BBCODE_URL.substituteAllOccurrences(this) { match ->
                    val url = match.groups["url"]?.value.orEmpty()
                    val anchor = match.groups["anchor"]?.value.orEmpty()
                    append("<a href=\"$url\">$anchor</a>")
                }
            }.replaceNewlines()

    private fun String.convertMarkdownToHtml(): String =
        run {
            Regex("~~(?<content>.*?)~~").substituteAllOccurrences(this) { match ->
                val content = match.groups["content"]?.value.orEmpty()
                append("<s>$content</s>")
            }
        }.run {
            Regex("_(?<content>.*?)_").substituteAllOccurrences(this) { match ->
                val content = match.groups["content"]?.value.orEmpty()
                append("<i>$content</i>")
            }
        }.run {
            Regex("\\*\\*(?<content>.*?)\\*\\*").substituteAllOccurrences(this) { match ->
                val content = match.groups["content"]?.value.orEmpty()
                append("<b>$content</b>")
            }
        }.run {
            Regex("`(?<content>.*?)`").substituteAllOccurrences(this) { match ->
                val content = match.groups["content"]?.value.orEmpty()
                append("<code>$content</code>")
            }
        }.run {
            Regex("\\[(?<anchor>.*?)]\\((?<url>.*?)\\)").substituteAllOccurrences(this) { match ->
                val anchor = match.groups["anchor"]?.value.orEmpty()
                val url = match.groups["url"]?.value.orEmpty()
                append("<a href=\"$url\">$anchor</a>")
            }
        }.run {
            Regex("^# (?<title>.*?)($|\n)").substituteAllOccurrences(this) { match ->
                val content = match.groups["title"]?.value.orEmpty()
                append("<h1>$content</h1>")
            }
        }.run {
            Regex("^## (?<title>.*?)($|\n)").substituteAllOccurrences(this) { match ->
                val content = match.groups["title"]?.value.orEmpty()
                append("<h2>$content</h2>")
            }
        }.run {
            Regex("^### (?<title>.*?)($|\n)").substituteAllOccurrences(this) { match ->
                val content = match.groups["title"]?.value.orEmpty()
                append("<h3>$content</h3>")
            }
        }.run {
            Regex("^#### (?<title>.*?)($|\n)").substituteAllOccurrences(this) { match ->
                val content = match.groups["title"]?.value.orEmpty()
                append("<h4>$content</h4>")
            }
        }.run {
            Regex("^##### (?<title>.*?)($|\n)").substituteAllOccurrences(this) { match ->
                val content = match.groups["title"]?.value.orEmpty()
                append("<h5>$content</h5>")
            }
        }.replace("<ul>", "\n")
            .replace("</ul>", "\n")
            .replace("<ol>", "\n")
            .replace("</ol>", "\n")
            .run {
                Regex("^- (?<content>.*?)$").substituteAllOccurrences(this) { match ->
                    val content = match.groups["content"]?.value.orEmpty()
                    append("<li>$content</li>")
                }
            }.run {
                ComposerRegexes.BBCODE_SHARE.substituteAllOccurrences(this) { match ->
                    val url = match.groups["url"]?.value.orEmpty()
                    append(url)
                }
            }.replaceNewlines()

    private fun String.replaceNewlines(): String =
        replace(Regex("\n\n"), "<br /><br />")
            .replace("\n", " ")

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
