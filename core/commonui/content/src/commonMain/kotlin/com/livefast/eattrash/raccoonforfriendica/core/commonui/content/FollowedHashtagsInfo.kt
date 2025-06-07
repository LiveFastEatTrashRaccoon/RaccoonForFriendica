package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.CornerSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.ancillaryTextAlpha
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TagModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun FollowedHashtagsInfo(
    tags: List<TagModel>,
    modifier: Modifier = Modifier,
    iconSize: Dp = IconSize.s,
    onOpenTag: ((TagModel) -> Unit)? = null,
) {
    val ancillaryColor = MaterialTheme.colorScheme.onBackground.copy(ancillaryTextAlpha)

    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xxs),
        verticalArrangement = Arrangement.Top,
    ) {
        Icon(
            modifier = Modifier.size(iconSize),
            imageVector = Icons.Default.Tag,
            contentDescription = null,
            tint = ancillaryColor,
        )
        tags.forEachIndexed { idx, tag ->
            Text(
                modifier =
                Modifier
                    .clip(RoundedCornerShape(CornerSize.l))
                    .clickable {
                        onOpenTag?.invoke(tag)
                    }.padding(vertical = Spacing.xxs, horizontal = Spacing.xs),
                text =
                buildString {
                    append(tag.name)
                    if (idx < tags.lastIndex) {
                        append(",")
                    }
                },
                color = ancillaryColor,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
