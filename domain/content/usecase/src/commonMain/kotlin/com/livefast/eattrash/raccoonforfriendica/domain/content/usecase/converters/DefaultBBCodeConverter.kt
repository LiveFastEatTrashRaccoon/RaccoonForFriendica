package com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.converters

import com.livefast.eattrash.raccoonforfriendica.core.utils.substituteAllOccurrences
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.ContentRegexes
import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlHandler
import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlParser

internal class DefaultBBCodeConverter : BBCodeConverter {
    override fun toHtml(value: String) = value
        .replace("[h1]", "<h1>")
        .replace("[/h1]", "</h1>")
        .replace("[h2]", "<h2>")
        .replace("[/h2]", "</h2>")
        .replace("[h3]", "<h3>")
        .replace("[/h3]", "</h3>")
        .replace("[h4]", "<h4>")
        .replace("[/h4]", "</h4>")
        .replace("[h5]", "<h5>")
        .replace("[/h5]", "</h5>")
        .replace("[u]", "<u>")
        .replace("[/u]", "</u>")
        .replace("[s]", "<s>")
        .replace("[/s]", "</s>")
        .replace("[i]", "<i>")
        .replace("[/i]", "</i>")
        .replace("[b]", "<b>")
        .replace("[/b]", "</b>")
        .replace("[p]", "<p>")
        .replace("[/p]", "</p>")
        .replace("[code]", "<code>")
        .replace("[/code]", "</code>")
        .replace("[ul]", "<ul>")
        .replace("[/ul]", "</ul>")
        .replace("[ol]", "<ol>")
        .replace("[/ol]", "</ol>")
        .replace("[li]", "<li>")
        .replace("[/li]", "</li>")
        .replace("[quote]", "<blockquote>")
        .replace("[/quote]", "</blockquote>")
        .run {
            ContentRegexes.BBCODE_SHARE.substituteAllOccurrences(this) { match ->
                val url = match.groups["url"]?.value.orEmpty()
                append("<a href=\"$url\">$url</a>")
            }
        }.run {
            ContentRegexes.BBCODE_URL.substituteAllOccurrences(this) { match ->
                val url = match.groups["url"]?.value.orEmpty()
                val anchor = match.groups["anchor"]?.value.orEmpty()
                append("<a href=\"$url\">$anchor</a>")
            }
        }.replaceNewlines()

    override fun fromHtml(value: String): String {
        val builder = StringBuilder()
        val handler =
            KsoupHtmlHandler
                .Builder()
                .onOpenTag { name, attributes, _ ->
                    when (name) {
                        "h1" -> builder.append("[h1]")
                        "h2" -> builder.append("[h2]")
                        "h3" -> builder.append("[h3]")
                        "h4" -> builder.append("[h4]")
                        "h5" -> builder.append("[h5]")
                        "br" -> builder.appendLine()
                        "b", "strong" -> builder.append("[b]")
                        "u" -> builder.append("[u]")
                        "i", "em" -> builder.append("[i]")
                        "s" -> builder.append("[s]")
                        "ul" -> builder.append("[ul]")
                        "ol" -> builder.append("[ol]")
                        "li" -> builder.append("[li]")
                        "code" -> builder.append("[code]")
                        "blockquote" -> builder.append("[quote]")
                        "q" -> builder.append("[quote]")
                        "a" -> {
                            val url = attributes["href"] ?: ""
                            builder.append("[url=$url]")
                        }

                        "img" -> {
                            val url = attributes["src"]
                            builder.append("[img]")
                            builder.append(url)
                        }

                        else -> Unit
                    }
                }.onCloseTag { name, _ ->
                    when (name) {
                        "h1" -> builder.append("[/h1]")
                        "h2" -> builder.append("[/h2]")
                        "h3" -> builder.append("[/h3]")
                        "h4" -> builder.append("[/h4]")
                        "h5" -> builder.append("[/h5]")
                        "span" -> Unit
                        "br" -> builder.append("\n")
                        "img" -> builder.append("[/img]")
                        "b", "strong" -> builder.append("[/b]")
                        "u" -> builder.append("[/u]")
                        "i", "em" -> builder.append("[/i]")
                        "s" -> builder.append("[/s]")
                        "p" -> builder.append("\n\n")
                        "code" -> builder.append("[/code]")
                        "a" -> builder.append("[/url]")
                        "ul" -> builder.append("[/ul]")
                        "ol" -> builder.append("[/ol]")
                        "li" -> builder.append("[/li]")
                        "blockquote" -> builder.append("[/quote]")
                        "q" -> builder.append("[/quote]")
                        else -> Unit
                    }
                }.onText { text ->
                    builder.append(text)
                }.build()

        KsoupHtmlParser(handler).apply {
            write(value)
            end()
        }

        // fix hashtags, mentions and group references removing links
        val result =
            builder
                .toString()
                .let { stepResult ->
                    Regex("!" + ContentRegexes.BBCODE_URL.pattern).substituteAllOccurrences(stepResult) { match ->
                        val anchor = match.groups["anchor"]?.value.orEmpty()
                        append("!$anchor")
                    }
                }.let { stepResult ->
                    Regex("@" + ContentRegexes.BBCODE_URL.pattern).substituteAllOccurrences(stepResult) { match ->
                        val anchor = match.groups["anchor"]?.value.orEmpty()
                        append("@$anchor")
                    }
                }.let { stepResult ->
                    Regex("#" + ContentRegexes.BBCODE_URL.pattern).substituteAllOccurrences(stepResult) { match ->
                        val anchor = match.groups["anchor"]?.value.orEmpty()
                        append("#$anchor")
                    }
                }

        return result
    }
}

private fun String.replaceNewlines(): String = replace("\n", "<br />")
