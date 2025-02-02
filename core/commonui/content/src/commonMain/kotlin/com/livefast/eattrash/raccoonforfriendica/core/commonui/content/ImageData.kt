package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlHandler
import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlParser

internal data class ImageData(
    val url: String,
    val description: String? = null,
)

internal fun extractImageData(html: String): ImageData? {
    var url: String? = null
    var description: String? = null
    KsoupHtmlParser(
        KsoupHtmlHandler
            .Builder()
            .onOpenTag { name, attributes, _ ->
                when (name) {
                    "img" -> {
                        url = attributes["src"]
                        description = attributes["alt"]
                    }

                    else -> Unit
                }
            }.build(),
    ).apply {
        write(html)
        end()
    }

    return url?.let { ImageData(url = it, description = description) }
}

internal val TimelineEntryModel.embeddedImageUrls: List<String>
    get() {
        val data = extractImagesData(content)
        return data.map { it.url }
    }

private fun extractImagesData(html: String): List<ImageData> {
    var url: String? = null
    var description: String? = null
    val result = mutableListOf<ImageData>()
    KsoupHtmlParser(
        KsoupHtmlHandler
            .Builder()
            .onOpenTag { name, attributes, _ ->
                when (name) {
                    "img" -> {
                        url = attributes["src"]
                        description = attributes["alt"]
                    }

                    else -> Unit
                }
            }.onCloseTag { name, _ ->
                when (name) {
                    "img" -> {
                        url?.let {
                            result += ImageData(url = it, description = description)
                        }
                    }
                }
            }.build(),
    ).apply {
        write(html)
        end()
    }

    return result
}
