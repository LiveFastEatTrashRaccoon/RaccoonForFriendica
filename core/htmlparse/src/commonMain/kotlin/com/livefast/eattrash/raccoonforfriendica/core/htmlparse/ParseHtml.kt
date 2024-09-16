package com.livefast.eattrash.raccoonforfriendica.core.htmlparse

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
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
    val string = AnnotatedString.Builder()

    val handler =
        KsoupHtmlHandler
            .Builder()
            .onOpenTag { name, attributes, _ ->
                when (name) {
                    "p" ->
                        if (string.length != 0) {
                            // separate paragraphs with a blank line
                            string.append("\n\n")
                        }

                    "span" -> Unit
                    "br" -> string.append('\n')
                    "a" -> {
                        string.pushStringAnnotation("link", attributes["href"] ?: "")
                        string.pushStyle(
                            SpanStyle(
                                color = linkColor,
                                textDecoration = TextDecoration.Underline,
                            ),
                        )
                    }

                    "b", "strong" -> string.pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                    "u" -> string.pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                    "i", "em" -> string.pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
                    "s" -> string.pushStyle(SpanStyle(textDecoration = TextDecoration.LineThrough))
                    "ul", "ol" -> Unit
                    "li" -> string.append(" • ")
                    else -> println("onOpenTag: Unhandled span $name")
                }
            }.onCloseTag { name, _ ->
                when (name) {
                    "p" -> string.append(' ')
                    "span", "br" -> Unit
                    "b", "strong", "u", "i", "em", "s" -> string.pop()
                    "a" -> {
                        string.pop() // corresponds to pushStyle
                        string.pop() // corresponds to pushStringAnnotation
                    }
                    "ul", "ol" -> Unit
                    "li" -> string.append('\n')
                    else -> println("onCloseTag: Unhandled span $name")
                }
            }.onText { text ->
                string.append(text)
            }.build()

    val ksoupHtmlParser = KsoupHtmlParser(handler)
    val html = if (requiresHtmlDecode) KsoupEntities.decodeHtml(this) else this
    ksoupHtmlParser.write(html)
    ksoupHtmlParser.end()

    return string.toAnnotatedString()
}
