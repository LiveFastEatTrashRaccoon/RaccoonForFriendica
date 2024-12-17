package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
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
import com.livefast.eattrash.raccoonforfriendica.core.htmlparse.parseHtml
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PreviewCardModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PreviewType

@Composable
fun ContentPreview(
    card: PreviewCardModel,
    autoloadImages: Boolean = true,
    modifier: Modifier = Modifier,
    onOpen: ((String) -> Unit)? = null,
    onOpenImage: ((String) -> Unit)? = null,
) {
    val fullColor = MaterialTheme.colorScheme.onBackground
    val ancillaryColor = MaterialTheme.colorScheme.onBackground.copy(ancillaryTextAlpha)
    val type = card.type
    val image = card.image.orEmpty()
    val url = card.url
    val title = card.title
    val description = card.description.takeIf { !it.startsWith(title) }.orEmpty()
    val hasMediaInfo = image.isNotBlank() || (type == PreviewType.Video && url.isNotBlank())
    val hasTextualInfo = title.isNotBlank() || description.isNotBlank()
    val cornerSize = CornerSize.xl
    val contentPadding = 12.dp

    if (hasMediaInfo || hasTextualInfo) {
        Column(
            modifier =
                modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClickLabel = LocalStrings.current.actionOpenLink,
                    ) {
                        if (url.isNotEmpty()) {
                            onOpen?.invoke(url)
                        }
                    }.border(
                        width = Dp.Hairline,
                        color = ancillaryColor,
                        shape = RoundedCornerShape(cornerSize),
                    ).padding(
                        bottom = contentPadding,
                    ),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            if (image.isNotBlank() && autoloadImages) {
                Box(
                    modifier =
                        Modifier.heightIn(
                            min = 50.dp,
                            max = 200.dp,
                        ),
                ) {
                    CustomImage(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .clip(
                                    RoundedCornerShape(
                                        topStart = cornerSize,
                                        topEnd = cornerSize,
                                    ),
                                ).clickable {
                                    onOpenImage?.invoke(image)
                                },
                        url = image,
                        contentDescription = LocalStrings.current.previewImage,
                        quality = FilterQuality.Low,
                        contentScale = ContentScale.FillWidth,
                    )
                }
            } else if (type == PreviewType.Video && url.isNotBlank()) {
                VideoPlayer(
                    modifier = Modifier.fillMaxWidth(),
                    url = url,
                    contentScale = ContentScale.Fit,
                )
            }

            Column(
                modifier =
                    Modifier.padding(
                        top = Spacing.s,
                        end = contentPadding,
                        start = contentPadding,
                    ),
                verticalArrangement = Arrangement.spacedBy(Spacing.s),
            ) {
                if (title.isNotBlank()) {
                    val annotatedTitle =
                        title.parseHtml(
                            linkColor = MaterialTheme.colorScheme.primary,
                            quoteColor =
                                MaterialTheme.colorScheme.onBackground.copy(
                                    ancillaryTextAlpha,
                                ),
                        )
                    Text(
                        text = annotatedTitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = fullColor,
                    )
                }
                if (description.isNotBlank()) {
                    val annotatedDescription =
                        description.parseHtml(
                            linkColor = MaterialTheme.colorScheme.primary,
                            quoteColor =
                                MaterialTheme.colorScheme.onBackground.copy(
                                    ancillaryTextAlpha,
                                ),
                        )
                    Text(
                        text = annotatedDescription,
                        style = MaterialTheme.typography.labelMedium,
                        color = ancillaryColor,
                    )
                }
            }
        }
    }

    if (type == PreviewType.Link && url.isNotEmpty()) {
        LinkBanner(
            modifier = Modifier.padding(top = Spacing.s),
            url = url,
            onClick = {
                onOpen?.invoke(url)
            },
        )
    }
}
