package com.livefast.eattrash.raccoonforfriendica.feature.composer.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpOffset
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.ancillaryTextAlpha
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomDropDown
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomImage
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.PlaceholderImage
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TextWithCustomEmojis
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.Visibility
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toIcon
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toReadableName

@Composable
internal fun CreatePostHeader(
    modifier: Modifier = Modifier,
    author: UserModel? = null,
    autoloadImages: Boolean = true,
    visibility: Visibility = Visibility.Public,
    availableVisibilities: List<Visibility> =
        listOf(
            Visibility.Public,
            Visibility.Unlisted,
            Visibility.Private,
        ),
    changeVisibilityEnabled: Boolean = true,
    onChangeVisibility: ((Visibility) -> Unit)? = null,
) {
    val fullColor = MaterialTheme.colorScheme.onBackground
    val ancillaryColor = MaterialTheme.colorScheme.onBackground.copy(ancillaryTextAlpha)

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.s),
    ) {
        val creatorName = author?.displayName ?: author?.username ?: ""
        val creatorAvatar = author?.avatar.orEmpty()
        val iconSize = IconSize.l
        if (creatorAvatar.isNotEmpty() && autoloadImages) {
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

        Column(
            modifier = Modifier.weight(1f),
        ) {
            TextWithCustomEmojis(
                text = creatorName,
                emojis = author?.emojis.orEmpty(),
                autoloadImages = autoloadImages,
                style = MaterialTheme.typography.bodyMedium,
                color = fullColor,
            )
            Text(
                text = author?.handle.orEmpty(),
                style = MaterialTheme.typography.bodyMedium,
                color = ancillaryColor,
            )
        }

        var optionsOffset by remember { mutableStateOf(Offset.Zero) }
        var visibilityDropDownOpen by remember { mutableStateOf(false) }
        Box {
            Row(
                modifier =
                Modifier
                    .onGloballyPositioned {
                        optionsOffset = it.positionInParent()
                    }.then(
                        if (changeVisibilityEnabled) {
                            Modifier.clickable {
                                visibilityDropDownOpen = true
                            }
                        } else {
                            Modifier
                        },
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                Icon(
                    modifier = Modifier.size(IconSize.s),
                    imageVector = visibility.toIcon(),
                    contentDescription = visibility.toReadableName(),
                )
                Text(
                    text = visibility.toReadableName(),
                )
                if (changeVisibilityEnabled) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = LocalStrings.current.actionChangeVisibility,
                    )
                } else {
                    Spacer(modifier = Modifier.width(Spacing.xxs))
                }
            }
            CustomDropDown(
                expanded = visibilityDropDownOpen,
                onDismiss = {
                    visibilityDropDownOpen = false
                },
                offset =
                with(LocalDensity.current) {
                    DpOffset(
                        x = optionsOffset.x.toDp(),
                        y = optionsOffset.y.toDp(),
                    )
                },
            ) {
                for (value in availableVisibilities) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                modifier = Modifier.padding(end = Spacing.s),
                                text = value.toReadableName(),
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = value.toIcon(),
                                contentDescription = null,
                            )
                        },
                        onClick = {
                            onChangeVisibility?.invoke(value)
                            visibilityDropDownOpen = false
                        },
                    )
                }
            }
        }
    }
}
