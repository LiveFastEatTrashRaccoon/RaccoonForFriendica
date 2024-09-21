package com.livefast.eattrash.raccoonforfriendica.core.htmlparse

import androidx.compose.ui.text.AnnotatedString
import com.mohamedrejeb.ksoup.entities.KsoupEntities
import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlHandler
import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlParser

fun String.prettifyHtml(requiresHtmlDecode: Boolean = true): String {
    val builder = AnnotatedString.Builder()

    var nestingIndent = 0
    val handler =
        KsoupHtmlHandler
            .Builder()
            .onOpenTag { name, attrs, _ ->
                if (name.isBlockElement) {
                    if (builder.length > 0) {
                        builder.append('\n')
                    }
                }
                if (name != "br") {
                    builder.append("<$name")
                    if (attrs.isNotEmpty()) {
                        builder.append(' ')
                        for (attr in attrs) {
                            builder.append("${attr.key}=\"${attr.value}\"")
                        }
                    }
                    builder.append(">")
                    if (name.isBlockElement) {
                        builder.append('\n')
                        nestingIndent++
                        builder.append("  ".repeat(nestingIndent))
                    }
                }
            }.onCloseTag { name, _ ->
                if (name.isBlockElement) {
                    builder.append('\n')
                    nestingIndent--
                }
                if (name == "br") {
                    builder.append("<br />")
                } else {
                    if (name.isBlockElement) {
                        builder.append("  ".repeat(nestingIndent))
                    }
                    builder.append("</$name>")
                }
            }.onText { text ->
                builder.append(text)
            }.build()

    val ksoupHtmlParser = KsoupHtmlParser(handler)
    val html = if (requiresHtmlDecode) KsoupEntities.decodeHtml(this) else this
    ksoupHtmlParser.write(html)
    ksoupHtmlParser.end()

    return builder.toAnnotatedString().text
}

private val String.isBlockElement: Boolean get() = this in listOf("p", "div")
