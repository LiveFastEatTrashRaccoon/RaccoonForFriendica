package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AttachmentModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.MediaType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.attachmentsToDisplay
import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlHandler
import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlParser

internal data class ImageData(val url: String, val description: String? = null)

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

internal val TimelineEntryModel.attachmentsToDisplayWithoutInlineImages: List<AttachmentModel>
    get() {
        val inlineImagesData = extractImagesData(content)
        // assume that the first n attachments are duplicate of the inline images
        val toDrop = attachmentsToDisplay.filter { it.type == MediaType.Image }.take(inlineImagesData.size)
        return attachmentsToDisplay - toDrop
    }

private fun extractImagesData(html: String): List<ImageData> {
    val result = mutableListOf<ImageData>()
    KsoupHtmlParser(
        KsoupHtmlHandler
            .Builder()
            .onOpenTag { name, attributes, _ ->
                when (name) {
                    "img" -> {
                        val url = attributes["src"]
                        val description = attributes["alt"]
                        url?.let {
                            result += ImageData(url = it, description = description)
                        }
                    }

                    else -> Unit
                }
            }.build(),
    ).apply {
        write(html)
        end()
    }

    return result
}
