package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.CornerSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.ancillaryTextAlpha
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings

@Composable
fun SettingsRow(
    title: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    painter: Painter? = null,
    value: String = "",
    disclosureIndicator: Boolean = false,
    annotatedValue: AnnotatedString = AnnotatedString(""),
    subtitle: String? = null,
    onTap: (() -> Unit)? = null,
) {
    val fullColor = MaterialTheme.colorScheme.onBackground
    val ancillaryColor = MaterialTheme.colorScheme.onBackground.copy(alpha = ancillaryTextAlpha)
    Row(
        modifier =
        modifier
            .clip(
                shape = RoundedCornerShape(CornerSize.xl),
            ).then(
                if (onTap != null) {
                    Modifier.clickable {
                        onTap()
                    }
                } else {
                    Modifier
                },
            ).padding(
                vertical = 10.dp,
                horizontal = Spacing.m,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.s),
    ) {
        val imageModifier = Modifier.size(IconSize.m)
        if (icon != null) {
            Icon(
                modifier = imageModifier,
                imageVector = icon,
                contentDescription = null,
                tint = fullColor,
            )
        }
        if (painter != null) {
            Image(
                painter = painter,
                contentDescription = null,
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = fullColor,
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelMedium,
                    color = ancillaryColor,
                )
            }
        }
        if (annotatedValue.isNotEmpty()) {
            Text(
                modifier = Modifier.padding(start = Spacing.xs),
                text = annotatedValue,
                style = MaterialTheme.typography.bodyMedium,
                color = fullColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        } else {
            Text(
                modifier = Modifier.padding(start = Spacing.xs),
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = fullColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (disclosureIndicator) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                tint = fullColor,
                contentDescription = LocalStrings.current.actionOpenDetail,
            )
        }
    }
}
