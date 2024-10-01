package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.ancillaryTextAlpha
import com.livefast.eattrash.raccoonforfriendica.core.htmlparse.parseHtml
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.FieldModel

@Composable
fun UserFields(
    fields: List<FieldModel> = emptyList(),
    modifier: Modifier = Modifier,
    onOpenUrl: ((String) -> Unit)? = null,
) {
    val ancillaryColor = MaterialTheme.colorScheme.onBackground.copy(ancillaryTextAlpha)

    Column(
        modifier = modifier.padding(horizontal = Spacing.s),
        verticalArrangement = Arrangement.spacedBy(Spacing.s),
    ) {
        for (field in fields) {
            Row {
                Text(
                    modifier = Modifier.weight(0.5f),
                    text = field.key.uppercase(),
                    color = ancillaryColor,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight(350)),
                )
                Box(modifier = Modifier.weight(1f)) {
                    val annotatedContent =
                        field.value.parseHtml(
                            linkColor = MaterialTheme.colorScheme.primary,
                        )
                    TextWithCustomEmojis(
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
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
                if (field.verified) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = null,
                        tint = Color.Green,
                    )
                }
            }
        }
    }
}
