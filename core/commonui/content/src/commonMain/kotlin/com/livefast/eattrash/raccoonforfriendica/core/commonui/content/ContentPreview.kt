package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.CornerSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.ancillaryTextAlpha
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomImage
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.VideoPlayer
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PreviewCardModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PreviewType

@Composable
fun ContentPreview(
    card: PreviewCardModel,
    modifier: Modifier = Modifier,
    onOpen: ((String) -> Unit)? = null,
    onOpenImage: ((String) -> Unit)? = null,
) {
    val fullColor = MaterialTheme.colorScheme.onBackground
    val ancillaryColor = MaterialTheme.colorScheme.onBackground.copy(ancillaryTextAlpha)
    val url = card.url
    val image = card.image
    val cornerSize = CornerSize.xl

    Column(
        modifier =
            modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) {
                    if (url.isNotEmpty()) {
                        onOpen?.invoke(url)
                    }
                }.border(
                    width = Dp.Hairline,
                    color = ancillaryColor,
                    shape = RoundedCornerShape(cornerSize),
                ).padding(
                    bottom = 12.dp,
                ),
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        if (!image.isNullOrBlank()) {
            CustomImage(
                modifier =
                    Modifier
                        .clip(
                            RoundedCornerShape(
                                topStart = cornerSize,
                                topEnd = cornerSize,
                            ),
                        ).clickable {
                            onOpenImage?.invoke(image)
                        },
                url = image,
                quality = FilterQuality.Low,
                contentScale = ContentScale.FillWidth,
            )
        } else if (card.type == PreviewType.Video) {
            VideoPlayer(
                modifier =
                    Modifier.fillMaxWidth().aspectRatio(16 / 9f),
                url = url,
            )
        }

        Column(
            modifier =
                Modifier.padding(
                    top = Spacing.s,
                    end = 10.dp,
                    start = 10.dp,
                ),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            if (card.title.isNotEmpty()) {
                Text(
                    text = card.title,
                    style = MaterialTheme.typography.bodySmall,
                    color = fullColor,
                )
            }
            if (card.description.isNotEmpty()) {
                Text(
                    text = card.description,
                    style = MaterialTheme.typography.labelSmall,
                    color = ancillaryColor,
                )
            }
        }
    }

    if (card.type == PreviewType.Link && url.isNotEmpty()) {
        LinkBanner(
            modifier =
                modifier.clickable {
                    onOpen?.invoke(url)
                },
            url = card.url,
        )
    }
}
