package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.CornerSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.ancillaryTextAlpha
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomImage
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.LinkModel

@Composable
fun LinkItem(
    link: LinkModel,
    modifier: Modifier = Modifier,
    onOpen: ((String) -> Unit)? = null,
) {
    val fullColor = MaterialTheme.colorScheme.onBackground
    val ancillaryColor = MaterialTheme.colorScheme.onBackground.copy(ancillaryTextAlpha)
    val url = link.url
    val imageSize = 100.dp
    val image = link.image

    Row(
        modifier =
            modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) {
                    if (url != null) {
                        onOpen?.invoke(url)
                    }
                }.border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onBackground,
                    shape = RoundedCornerShape(CornerSize.l),
                ).padding(Spacing.s),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Spacing.s),
        ) {
            link.authorName?.also { author ->
                Text(
                    text = author,
                    style = MaterialTheme.typography.labelMedium,
                    color = ancillaryColor,
                )
            }
            Text(
                text = link.title,
                style = MaterialTheme.typography.bodyMedium,
                color = fullColor,
            )
        }
        if (!image.isNullOrBlank()) {
            CustomImage(
                modifier =
                    Modifier
                        .width(imageSize)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(CornerSize.l)),
                url = image,
                contentScale = ContentScale.Crop,
            )
        }
    }
}
