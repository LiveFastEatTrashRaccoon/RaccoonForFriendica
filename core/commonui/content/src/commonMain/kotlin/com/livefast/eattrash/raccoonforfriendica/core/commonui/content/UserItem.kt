package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.ancillaryTextAlpha
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomDropDown
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomImage
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.PlaceholderImage
import com.livefast.eattrash.raccoonforfriendica.core.utils.ellipsize
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.RelationshipStatusNextAction
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel

@Composable
fun UserItem(
    user: UserModel,
    modifier: Modifier = Modifier,
    options: List<Option> = emptyList(),
    onClick: (() -> Unit)? = null,
    onRelationshipClicked: ((RelationshipStatusNextAction) -> Unit)? = null,
    onOptionSelected: ((OptionId) -> Unit)? = null,
) {
    val avatar = user.avatar.orEmpty()
    val avatarSize = IconSize.l
    val fullColor = MaterialTheme.colorScheme.onBackground
    val ancillaryColor = MaterialTheme.colorScheme.onBackground.copy(ancillaryTextAlpha)
    val relationshipStatus = user.relationshipStatus
    var optionsOffset by remember { mutableStateOf(Offset.Zero) }
    var optionsMenuOpen by remember { mutableStateOf(false) }

    Row(
        modifier =
            modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) {
                    onClick?.invoke()
                }.padding(Spacing.s),
        horizontalArrangement = Arrangement.spacedBy(Spacing.s),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (avatar.isNotEmpty()) {
            CustomImage(
                modifier =
                    Modifier
                        .size(avatarSize)
                        .clip(RoundedCornerShape(avatarSize / 2)),
                url = avatar,
                quality = FilterQuality.Low,
                contentScale = ContentScale.FillBounds,
            )
        } else {
            PlaceholderImage(
                size = avatarSize,
                title = user.displayName ?: user.handle ?: "?",
            )
        }

        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = (user.displayName ?: user.username ?: "").ellipsize(30),
                style = MaterialTheme.typography.titleMedium,
                color = fullColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = (user.handle ?: user.username ?: "").ellipsize(25),
                style = MaterialTheme.typography.titleMedium,
                color = ancillaryColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        if (relationshipStatus != null) {
            UserRelationshipButton(
                status = relationshipStatus,
                pending = user.relationshipStatusPending,
                locked = user.locked,
                onClick = onRelationshipClicked,
            )
        }
        if (options.isNotEmpty()) {
            Box {
                IconButton(
                    modifier =
                        Modifier.onGloballyPositioned {
                            optionsOffset = it.positionInParent()
                        },
                    onClick = {
                        optionsMenuOpen = true
                    },
                ) {
                    Icon(
                        modifier = Modifier.size(IconSize.s),
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground,
                    )
                }

                CustomDropDown(
                    expanded = optionsMenuOpen,
                    onDismiss = {
                        optionsMenuOpen = false
                    },
                    offset =
                        with(LocalDensity.current) {
                            DpOffset(
                                x = optionsOffset.x.toDp(),
                                y = optionsOffset.y.toDp(),
                            )
                        },
                ) {
                    options.forEach { option ->
                        DropdownMenuItem(
                            text = {
                                Text(option.label)
                            },
                            onClick = {
                                optionsMenuOpen = false
                                onOptionSelected?.invoke(option.id)
                            },
                        )
                    }
                }
            }
        }
    }
}
