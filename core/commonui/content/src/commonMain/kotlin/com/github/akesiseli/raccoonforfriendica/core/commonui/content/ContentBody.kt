package com.github.akesiseli.raccoonforfriendica.core.commonui.content

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.akesiseli.raccoonforfriendica.core.htmlparse.parseHtml

@Composable
fun ContentBody(
    content: String = "",
    modifier: Modifier = Modifier,
    onOpenUrl: ((String) -> Unit)? = null,
) {
    val fullColor = MaterialTheme.colorScheme.onBackground
    Box(modifier = modifier) {
        val annotatedContent =
            content.parseHtml(
                linkColor = MaterialTheme.colorScheme.primary,
            )
        ClickableText(
            style = MaterialTheme.typography.bodyMedium.copy(color = fullColor),
            text = annotatedContent,
            onClick = { offset ->
                val url =
                    annotatedContent
                        .getStringAnnotations(start = offset, end = offset)
                        .firstOrNull()
                        ?.item
                if (!url.isNullOrBlank()) {
                    onOpenUrl?.invoke(url)
                }
            },
        )
    }
}
