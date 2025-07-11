package com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.converters

import com.livefast.eattrash.raccoonforfriendica.core.utils.substituteAllOccurrences
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.ContentRegexes

internal class DefaultMarkdownConverter : MarkdownConverter {
    override fun toHtml(value: String) = value
        .run {
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
            Regex("(?<=^|\n)# (?<title>.*?)(?=$|\n)").substituteAllOccurrences(this) { match ->
                val content = match.groups["title"]?.value.orEmpty()
                append("<h1>$content</h1>\n")
            }
        }.run {
            Regex("(?<=^|\n)## (?<title>.*?)(?=\$|\n)").substituteAllOccurrences(this) { match ->
                val content = match.groups["title"]?.value.orEmpty()
                append("<h2>$content</h2>\n")
            }
        }.run {
            Regex("(?<=^|\n)### (?<title>.*?)(?=$|\n)").substituteAllOccurrences(this) { match ->
                val content = match.groups["title"]?.value.orEmpty()
                append("<h3>$content</h3>\n")
            }
        }.run {
            Regex("(?<=^|\n)#### (?<title>.*?)(?=\$|\n)").substituteAllOccurrences(this) { match ->
                val content = match.groups["title"]?.value.orEmpty()
                append("<h4>$content</h4>\n")
            }
        }.run {
            Regex("(?<=^|\n)##### (?<title>.*?)(?=\$|\n)").substituteAllOccurrences(this) { match ->
                val content = match.groups["title"]?.value.orEmpty()
                append("<h5>$content</h5>\n")
            }
        }.run {
            Regex("(?<=^|\n)> (?<text>.*?)(?=\$|\n)").substituteAllOccurrences(this) { match ->
                val content = match.groups["text"]?.value.orEmpty()
                append("<blockquote>$content</blockquote>\n")
            }
        }.run {
            ContentRegexes.BBCODE_SHARE.substituteAllOccurrences(this) { match ->
                val url = match.groups["url"]?.value.orEmpty()
                append(url)
            }
        }

    override fun fromHtml(value: String) = value
}
