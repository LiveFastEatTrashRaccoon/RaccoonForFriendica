package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.unit.dp
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.CornerSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.ancillaryTextAlpha
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomImage
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

    when (card.type) {
        PreviewType.Link -> {
            if (card.type == PreviewType.Link && url.isNotEmpty()) {
                LinkBanner(
                    modifier =
                        modifier.clickable {
                            onOpen?.invoke(card.url)
                        },
                    url = card.url,
                )
            }
        }

        PreviewType.Photo,
        PreviewType.Video,
        -> {
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
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.onBackground,
                            shape = RoundedCornerShape(CornerSize.l),
                        ).padding(
                            top = Spacing.s,
                            bottom = 10.dp,
                            start = 10.dp,
                            end = 10.dp,
                        ),
                verticalArrangement = Arrangement.spacedBy(Spacing.s),
            ) {
                if (card.title.isNotEmpty()) {
                    Text(
                        text = card.title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = fullColor,
                    )
                }
                if (card.description.isNotEmpty()) {
                    Text(
                        text = card.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = ancillaryColor,
                    )
                }
                if (!image.isNullOrBlank()) {
                    CustomImage(
                        modifier =
                            Modifier
                                .clip(RoundedCornerShape(CornerSize.xl))
                                .clickable {
                                    onOpenImage?.invoke(image)
                                },
                        url = image,
                        quality = FilterQuality.Low,
                        contentScale = ContentScale.FillWidth,
                    )
                }
            }
        }

        else -> Unit
    }
}
