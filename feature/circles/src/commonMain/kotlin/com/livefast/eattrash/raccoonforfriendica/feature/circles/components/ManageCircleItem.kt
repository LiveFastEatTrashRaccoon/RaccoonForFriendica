package com.livefast.eattrash.raccoonforfriendica.feature.circles.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.PlaceholderImage
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TwoStateButton
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.CircleModel

@Composable
internal fun ManageCircleItem(
    circle: CircleModel,
    belonging: Boolean,
    pending: Boolean,
    modifier: Modifier = Modifier,
    onToggleBelonging: ((Boolean) -> Unit)? = null,
) {
    Row(
        modifier = modifier.padding(Spacing.s),
        horizontalArrangement = Arrangement.spacedBy(Spacing.s),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PlaceholderImage(
            size = IconSize.l,
            title = circle.name,
        )

        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = circle.name,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }

        if (onToggleBelonging != null) {
            TwoStateButton(
                isProminent = !belonging,
                label = LocalStrings.current.actionRemove,
                prominentLabel = LocalStrings.current.actionAdd,
                pending = pending,
                onValueChange = { notFollowing ->
                    onToggleBelonging(!belonging)
                },
            )
        }
    }
}
