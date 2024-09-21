package com.livefast.eattrash.raccoonforfriendica.core.htmlparse

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import com.mohamedrejeb.ksoup.entities.KsoupEntities
import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlHandler
import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlParser

fun String.parseHtml(
    linkColor: Color,
    requiresHtmlDecode: Boolean = true,
): AnnotatedString {
    val builder = AnnotatedString.Builder()

    val handler =
        KsoupHtmlHandler
            .Builder()
            .onOpenTag { name, attributes, _ ->
                when (name) {
                    "p" ->
                        if (builder.length != 0) {
                            // separate paragraphs with a blank line
                            builder.append("\n\n")
                        }

                    "span" -> Unit
                    "br" -> builder.append('\n')
                    "a" -> {
                        builder.pushStringAnnotation("link", attributes["href"] ?: "")
                        builder.pushStyle(
                            SpanStyle(
                                color = linkColor,
                                textDecoration = TextDecoration.Underline,
                            ),
                        )
                    }

                    "b", "strong" -> builder.pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                    "u" -> builder.pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                    "i", "em" -> builder.pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
                    "s" -> builder.pushStyle(SpanStyle(textDecoration = TextDecoration.LineThrough))
                    "ul", "ol" -> Unit
                    "li" -> builder.append(" • ")
                    "code" -> builder.pushStyle(SpanStyle(fontFamily = FontFamily.Monospace))
                    else -> println("onOpenTag: Unhandled span $name")
                }
            }.onCloseTag { name, _ ->
                when (name) {
                    "p" -> builder.append(' ')
                    "span", "br" -> Unit
                    "b", "strong", "u", "i", "em", "s", "code" -> builder.pop()
                    "a" -> {
                        builder.pop() // corresponds to pushStyle
                        builder.pop() // corresponds to pushStringAnnotation
                    }
                    "ul", "ol" -> Unit
                    "li" -> builder.append('\n')
                    else -> println("onCloseTag: Unhandled span $name")
                }
            }.onText { text ->
                builder.append(text)
            }.build()

    val ksoupHtmlParser = KsoupHtmlParser(handler)
    val html = if (requiresHtmlDecode) KsoupEntities.decodeHtml(this) else this
    ksoupHtmlParser.write(html)
    ksoupHtmlParser.end()

    return builder.toAnnotatedString()
}
