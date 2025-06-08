package com.livefast.eattrash.raccoonforfriendica.feature.drawer.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing

@Composable
internal fun AboutItem(
    text: String,
    modifier: Modifier = Modifier,
    painter: Painter? = null,
    icon: ImageVector? = null,
    textDecoration: TextDecoration = TextDecoration.None,
    value: String = "",
    onClick: (() -> Unit)? = null,
) {
    Row(
        modifier =
        modifier
            .padding(
                horizontal = Spacing.xs,
                vertical = Spacing.s,
            ).clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) {
                onClick?.invoke()
            },
        horizontalArrangement = Arrangement.spacedBy(Spacing.s),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val imageModifier = Modifier.size(22.dp)
        if (painter != null) {
            Image(
                modifier = imageModifier,
                painter = painter,
                contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
            )
        } else if (icon != null) {
            Image(
                modifier = imageModifier,
                imageVector = icon,
                contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            textDecoration = textDecoration,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Spacer(modifier = Modifier.weight(1f))

        if (value.isNotEmpty()) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
    }
}
