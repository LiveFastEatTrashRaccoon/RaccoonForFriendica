package com.github.akesiseli.raccoonforfriendica.core.commonui.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import com.github.akesiseli.raccoonforfriendica.core.appearance.theme.IconSize
import com.github.akesiseli.raccoonforfriendica.core.appearance.theme.Spacing
import com.github.akesiseli.raccoonforfriendica.core.commonui.components.CustomImage
import com.github.akesiseli.raccoonforfriendica.core.commonui.components.PlaceholderImage
import com.github.akesiseli.raccoonforfriendica.domain.content.data.AccountModel

@Composable
fun ContentHeader(
    modifier: Modifier = Modifier,
    account: AccountModel? = null,
    iconSize: Dp = IconSize.l,
) {
    val creatorName = account?.let { it.displayName ?: it.handle }.orEmpty()
    val creatorAvatar = account?.avatar.orEmpty()
    val fullColor = MaterialTheme.colorScheme.onBackground

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.s),
    ) {
        if (creatorAvatar.isNotEmpty()) {
            CustomImage(
                modifier =
                    Modifier
                        .padding(Spacing.xxxs)
                        .size(iconSize)
                        .clip(RoundedCornerShape(iconSize / 2)),
                url = creatorAvatar,
                quality = FilterQuality.Low,
                contentScale = ContentScale.FillBounds,
            )
        } else {
            PlaceholderImage(
                size = iconSize,
                title = creatorName,
            )
        }

        Text(
            modifier = Modifier.weight(1f),
            text = creatorName,
            style = MaterialTheme.typography.bodySmall,
            color = fullColor,
        )
    }
}
