package com.livefast.eattrash.raccoonforfriendica.feat.licences.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.CornerSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.ancillaryTextAlpha
import com.livefast.eattrash.raccoonforfriendica.feat.licences.models.LicenceItem
import com.livefast.eattrash.raccoonforfriendica.feat.licences.models.LicenceItemType
import com.livefast.eattrash.raccoonforfriendica.feat.licences.models.toIcon

@Composable
internal fun LicenceItem(item: LicenceItem, modifier: Modifier = Modifier, onClick: (() -> Unit)? = null) {
    val fullColor = MaterialTheme.colorScheme.onBackground
    val ancillaryColor = MaterialTheme.colorScheme.onBackground.copy(alpha = ancillaryTextAlpha)

    Row(
        modifier =
        modifier
            .clip(
                shape = RoundedCornerShape(CornerSize.xl),
            ).then(
                if (onClick != null) {
                    Modifier.clickable {
                        onClick()
                    }
                } else {
                    Modifier
                },
            ).padding(
                vertical = Spacing.xs,
                horizontal = Spacing.s,
            ),
        horizontalArrangement = Arrangement.spacedBy(Spacing.m),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        item.type?.also { type ->
            Icon(
                modifier = Modifier.size(IconSize.m),
                imageVector = type.toIcon(),
                contentDescription =
                when (type) {
                    LicenceItemType.Library -> "Library"
                    LicenceItemType.Resource -> "Resource"
                },
                tint = fullColor,
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleSmall,
                color = fullColor,
            )
            if (item.subtitle.isNotBlank()) {
                Text(
                    text = item.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = ancillaryColor,
                )
            }
        }
    }
}
