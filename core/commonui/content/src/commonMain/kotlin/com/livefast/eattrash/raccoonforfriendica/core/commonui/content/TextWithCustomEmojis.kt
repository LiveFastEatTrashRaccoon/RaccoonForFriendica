package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.em
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomImage
import com.livefast.eattrash.raccoonforfriendica.core.utils.substituteAllOccurrences
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.EmojiModel

private val EMOJI_REGEX = Regex(":(\\w+):")
private val EMOJI_SIZE = 1.15.em

internal val String.looksLikeAnEmoji: Boolean get() = EMOJI_REGEX.matches(this)

@Composable
fun TextWithCustomEmojis(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    emojis: List<EmojiModel> = emptyList(),
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified,
    softWrap: Boolean = true,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    autoloadImages: Boolean = true,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    onClick: ((Int) -> Unit) = {},
) {
    val foundEmojis = mutableListOf<EmojiModel>()
    val processedText =
        text
            .run {
                EMOJI_REGEX.substituteAllOccurrences(this) { occurrence ->
                    val emojiCode = occurrence.groups[1]?.value.orEmpty()
                    val foundEmoji = emojis.firstOrNull { it.code == emojiCode }
                    if (foundEmoji != null) {
                        if (autoloadImages) {
                            appendInlineContent(
                                id = emojiCode,
                                alternateText = occurrence.value,
                            )
                            foundEmojis += foundEmoji
                        }
                    } else {
                        append(occurrence.value)
                    }
                }
            }.run {
                IMAGE_REGEX.substituteAllOccurrences(this) { occurrence ->
                    val rawString = occurrence.value
                    val imageData = extractImageData(rawString)
                    val alternateText =
                        imageData?.description.takeIf {
                            !it.isNullOrEmpty()
                        } ?: rawString
                    // on some instances (e.g. anonsys.net) emojis are sent as <img /> elements
                    val looksLikeAnEmoji = alternateText.looksLikeAnEmoji
                    if (looksLikeAnEmoji) {
                        val emojiCode = alternateText.replace(":", "")
                        val foundEmoji = emojis.firstOrNull { it.code == emojiCode }
                        if (foundEmoji != null) {
                            if (autoloadImages) {
                                appendInlineContent(
                                    id = emojiCode,
                                    alternateText = occurrence.value,
                                )
                                foundEmojis += foundEmoji
                            }
                        }
                    }
                }
            }
    val inlineContentEmojis =
        foundEmojis.associate { emoji ->
            emoji.code to
                InlineTextContent(
                    placeholder =
                    Placeholder(
                        width = EMOJI_SIZE,
                        height = EMOJI_SIZE,
                        placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter,
                    ),
                ) {
                    CustomImage(
                        modifier = Modifier.size(IconSize.m),
                        url = emoji.url,
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds,
                    )
                }
        }

    val layoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }
    val pressIndicator =
        Modifier.pointerInput(onClick) {
            detectTapGestures { pos ->
                layoutResult.value?.let { layoutResult ->
                    onClick(layoutResult.getOffsetForPosition(pos))
                }
            }
        }
    BasicText(
        modifier = modifier.then(pressIndicator),
        text = processedText,
        inlineContent = inlineContentEmojis,
        style = style,
        color = { color },
        softWrap = softWrap,
        overflow = overflow,
        maxLines = maxLines,
        onTextLayout = {
            layoutResult.value = it
            onTextLayout(it)
        },
    )
}

@Composable
fun TextWithCustomEmojis(
    text: String,
    modifier: Modifier = Modifier,
    emojis: List<EmojiModel> = emptyList(),
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified,
    softWrap: Boolean = true,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    autoloadImages: Boolean = true,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    onClick: ((Int) -> Unit) = {},
) {
    TextWithCustomEmojis(
        text = AnnotatedString(text),
        modifier = modifier,
        emojis = emojis,
        style = style,
        color = color,
        softWrap = softWrap,
        overflow = overflow,
        maxLines = maxLines,
        autoloadImages = autoloadImages,
        onTextLayout = onTextLayout,
        onClick = onClick,
    )
}
