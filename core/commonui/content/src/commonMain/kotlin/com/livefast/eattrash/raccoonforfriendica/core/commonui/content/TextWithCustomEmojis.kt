package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.em
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomImage
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.EmojiModel

private val EMOJI_REGEX = Regex(":(\\w+):")
private val EMOJI_SIZE = 1.15.em

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
    onTextLayout: (TextLayoutResult) -> Unit = {},
    onClick: ((Int) -> Unit) = {},
) {
    var index = 0
    val occurrences = EMOJI_REGEX.findAll(text)
    val foundEmojis = mutableListOf<EmojiModel>()
    val processedText =
        buildAnnotatedString {
            for (occurrence in occurrences) {
                val (start, end) = occurrence.range.first to occurrence.range.last
                if (start > index) {
                    append(text.subSequence(startIndex = index, endIndex = start))
                }
                val emojiCode =
                    occurrence.groups
                        .first()
                        ?.value
                        .orEmpty()
                        .trim(':')
                val foundEmoji = emojis.firstOrNull { it.code == emojiCode }
                if (foundEmoji != null) {
                    appendInlineContent(
                        id = emojiCode,
                        alternateText = occurrence.value,
                    )
                    foundEmojis += foundEmoji
                } else {
                    append(occurrence.value)
                }
                index = end + 1
            }
            if (index < text.length) {
                append(text.subSequence(startIndex = index, endIndex = text.length))
            }
        }
    val inlineContent =
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
                        modifier = Modifier.fillMaxSize(),
                        url = emoji.url,
                        contentScale = ContentScale.FillWidth,
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
        inlineContent = inlineContent,
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
        onTextLayout = onTextLayout,
        onClick = onClick,
    )
}
