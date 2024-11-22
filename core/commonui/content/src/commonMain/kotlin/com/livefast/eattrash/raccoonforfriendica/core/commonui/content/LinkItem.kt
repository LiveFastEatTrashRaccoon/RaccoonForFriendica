package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.CornerSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.ancillaryTextAlpha
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomImage
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.LinkModel

@Composable
fun LinkItem(
    link: LinkModel,
    autoloadImages: Boolean = true,
    modifier: Modifier = Modifier,
    onOpen: ((String) -> Unit)? = null,
) {
    val fullColor = MaterialTheme.colorScheme.onBackground
    val ancillaryColor = MaterialTheme.colorScheme.onBackground.copy(ancillaryTextAlpha)
    val url = link.url
    val image = link.image
    val title = link.title
    val description = link.description.orEmpty()
    val cornerSize = CornerSize.xl
    val contentPadding = 12.dp
    var imageHeight by remember { mutableStateOf(0f) }
    val minImageHeightForBanner = with(LocalDensity.current) { 50.dp.toPx() }

    Column(
        modifier =
            modifier
                .padding(horizontal = Spacing.s)
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
                ),
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        if (!image.isNullOrBlank()) {
            Box {
                CustomImage(
                    modifier =
                        Modifier
                            .clip(
                                RoundedCornerShape(
                                    topStart = cornerSize,
                                    topEnd = cornerSize,
                                ),
                            ).onGloballyPositioned {
                                imageHeight = it.size.toSize().height
                            },
                    url = image,
                    autoload = autoloadImages,
                    quality = FilterQuality.Low,
                    contentScale = ContentScale.FillWidth,
                )
                if (imageHeight > minImageHeightForBanner) {
                    UrlBanner(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .background(
                                    color =
                                        MaterialTheme.colorScheme
                                            .surfaceColorAtElevation(5.dp)
                                            .copy(0.45f),
                                ).padding(10.dp),
                        url = url,
                    )
                }
            }
        }

        Column(
            modifier =
                Modifier.padding(
                    top = Spacing.s,
                    end = contentPadding,
                    start = contentPadding,
                ),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            if (title.isNotEmpty()) {
                Text(
                    text = link.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = fullColor,
                )
            }
            if (description.isNotEmpty() && !description.contains(title)) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.labelMedium,
                    color = ancillaryColor,
                )
            }
        }

        if (imageHeight <= minImageHeightForBanner) {
            UrlBanner(
                url = url,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(
                            color =
                                MaterialTheme.colorScheme
                                    .surfaceColorAtElevation(5.dp)
                                    .copy(0.5f),
                            shape =
                                RoundedCornerShape(
                                    bottomStart = cornerSize,
                                    bottomEnd = cornerSize,
                                ),
                        ).padding(10.dp),
            )
        } else {
            Spacer(modifier = Modifier.height(contentPadding))
        }
    }
}

@Composable
private fun UrlBanner(
    url: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
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
