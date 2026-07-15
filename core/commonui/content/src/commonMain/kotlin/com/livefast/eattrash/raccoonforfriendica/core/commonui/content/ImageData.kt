package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import com.livefast.eattrash.raccoonforfriendica.core.utils.di.getImageLoaderProvider
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.ImageLoaderProvider
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AttachmentModel
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
    get() =
        run {
            val inlineImagesData = extractImagesData(content)
            attachmentsToDisplay.filter { attachment ->
                inlineImagesData.none { it.isSameAs(attachment) }
            }
        }

private fun ImageData.isSameAs(attachment: AttachmentModel): Boolean {
    val inlineUrl = url.trim()
    if (inlineUrl.isBlank()) return false

    // URL match
    val attachmentUrls = listOf(attachment.url, attachment.previewUrl, attachment.thumbnail).mapNotNull { it?.trim() }
    if (inlineUrl in attachmentUrls) {
        return true
    }

    // description match
    if (!description.isNullOrBlank() && description.length > 3 && description == attachment.description) {
        return true
    }

    // signature match (helpful when URLs are proxied or have different versions)
    val inlineSig = inlineUrl.toSignature()
    val attachmentSignatures = listOf(
        attachment.url.toSignature(),
        attachment.previewUrl?.toSignature().orEmpty(),
        attachment.thumbnail?.toSignature().orEmpty(),
    )
    if (inlineSig.length > 8 && inlineSig in attachmentSignatures) {
        return true
    }

    getImageLoaderProvider()

    return false
}

private fun String.toSignature(): String =
    this.substringBefore('?').substringAfterLast('/')

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
