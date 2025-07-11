package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.CornerSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings

@Composable
fun LinkBanner(url: String, modifier: Modifier = Modifier, onClick: (() -> Unit)? = null) {
    if (url.isNotEmpty()) {
        Row(
            modifier =
            modifier
                .background(
                    color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(CornerSize.l),
                ).clip(
                    RoundedCornerShape(CornerSize.l),
                ).clickable {
                    onClick?.invoke()
                }.padding(
                    horizontal = Spacing.m,
                    vertical = Spacing.s,
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.m),
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = url,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelMedium,
            )
            Icon(
                imageVector = Icons.Default.Link,
                contentDescription = LocalStrings.current.actionOpenLink,
            )
        }
    }
}
