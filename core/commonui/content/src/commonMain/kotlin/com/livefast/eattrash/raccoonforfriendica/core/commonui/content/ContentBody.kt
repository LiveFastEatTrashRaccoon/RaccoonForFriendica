package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.livefast.eattrash.raccoonforfriendica.core.htmlparse.parseHtml
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.EmojiModel

@Composable
fun ContentBody(
    content: String = "",
    color: Color = MaterialTheme.colorScheme.onBackground,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE,
    emojis: List<EmojiModel> = emptyList(),
    onClick: (() -> Unit)? = null,
    onOpenUrl: ((String) -> Unit)? = null,
) {
    Box(modifier = modifier) {
        val annotatedContent =
            content.parseHtml(
                linkColor = MaterialTheme.colorScheme.primary,
            )
        TextWithCustomEmojis(
            style = MaterialTheme.typography.bodyMedium.copy(color = color),
            text = annotatedContent,
            maxLines = maxLines,
            emojis = emojis,
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
