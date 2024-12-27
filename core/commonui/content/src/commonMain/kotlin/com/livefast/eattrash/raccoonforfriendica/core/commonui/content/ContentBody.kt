package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.CornerSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.ancillaryTextAlpha
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomImage
import com.livefast.eattrash.raccoonforfriendica.core.htmlparse.parseHtml
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.EmojiModel

internal val IMAGE_REGEX = Regex("<img.*?/>")

@Composable
fun ContentBody(
    content: String = "",
    color: Color = MaterialTheme.colorScheme.onBackground,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE,
    autoloadImages: Boolean = true,
    emojis: List<EmojiModel> = emptyList(),
    onClick: (() -> Unit)? = null,
    onOpenImage: ((String) -> Unit)? = null,
    onOpenUrl: ((String) -> Unit)? = null,
) {
    Box(modifier = modifier) {
        val chunks = content.splitTextAndImages()
        Column {
            for (chunk in chunks) {
                if (IMAGE_REGEX.matches(chunk)) {
                    extractImageData(chunk)?.also { data ->
                        CustomImage(
                            modifier =
                                Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(CornerSize.xl))
                                    .clickable {
                                        onOpenImage?.invoke(data.url)
                                    },
                            url = data.url,
                            contentDescription = data.description,
                        )
                    }
                } else {
                    val annotatedContent =
                        chunk.parseHtml(
                            linkColor = MaterialTheme.colorScheme.primary,
                            quoteColor =
                                MaterialTheme.colorScheme.onBackground.copy(
                                    ancillaryTextAlpha,
                                ),
                        )
                    TextWithCustomEmojis(
                        style = MaterialTheme.typography.bodyMedium.copy(color = color),
                        text = annotatedContent,
                        maxLines = maxLines,
                        overflow = TextOverflow.Ellipsis,
                        emojis = emojis,
                        autoloadImages = autoloadImages,
                        onClick = { offset ->
                            val url =
                                annotatedContent
                                    .getStringAnnotations(start = offset, end = offset)
                                    .firstOrNull()
                                    ?.item
                            if (!url.isNullOrBlank()) {
                                onOpenUrl?.invoke(url)
                            } else {
                                onClick?.invoke()
                            }
                        },
                    )
                }
            }
        }
    }
}

private fun String.splitTextAndImages(): List<String> =
    buildList {
        val original = this@splitTextAndImages
        val matches = IMAGE_REGEX.findAll(original).toList()
        var index = 0
        for (match in matches) {
            val range = match.range
            add(original.substring(index, range.first).trim())
            val htmlImage = original.substring(range)
            val data = extractImageData(htmlImage)
            if (data != null && data.description?.looksLikeAnEmoji != true) {
                add(htmlImage.trim())
            }
            index = range.last + 1
        }
        if (index < original.length) {
            add(original.substring(index, original.length).trim())
        }
    }
