package com.livefast.eattrash.raccoonforfriendica.feature.drawer.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.ancillaryTextAlpha
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomImage
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.PlaceholderImage
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TextWithCustomEmojis
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel

@Composable
internal fun DrawerHeader(
    modifier: Modifier = Modifier,
    user: UserModel? = null,
    node: String? = null,
    canSwitchAccount: Boolean = false,
    autoloadImages: Boolean = true,
    onOpenChangeInstance: (() -> Unit)? = null,
    onOpenSwitchAccount: (() -> Unit)? = null,
) {
    val avatarSize = 52.dp
    val fullColor = MaterialTheme.colorScheme.onBackground
    val ancillaryColor = MaterialTheme.colorScheme.onBackground.copy(alpha = ancillaryTextAlpha)
    Row(
        modifier =
            modifier.padding(
                top = Spacing.m,
                start = Spacing.s,
                end = Spacing.s,
                bottom = Spacing.s,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.m),
    ) {
        if (user != null) {
            val username = user.displayName ?: user.username ?: ""
            val userAvatar = user.avatar.orEmpty()
            if (userAvatar.isNotEmpty() && autoloadImages) {
                CustomImage(
                    modifier =
                        Modifier
                            .padding(Spacing.xxxs)
                            .size(avatarSize)
                            .clip(RoundedCornerShape(avatarSize / 2)),
                    url = userAvatar,
                    quality = FilterQuality.Low,
                    contentScale = ContentScale.FillBounds,
                )
            } else {
                PlaceholderImage(
                    size = avatarSize,
                    title = username,
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(Spacing.xxxs),
                ) {
                    TextWithCustomEmojis(
                        text = username,
                        emojis = user.emojis,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = fullColor,
                        autoloadImages = autoloadImages,
                    )
                    Text(
                        text =
                            buildString {
                                append(LocalStrings.current.nodeVia)
                                append(" ")
                                append(node)
                            },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleMedium,
                        color = ancillaryColor,
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                if (canSwitchAccount) {
                    IconButton(
                        onClick = {
                            onOpenSwitchAccount?.invoke()
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = LocalStrings.current.actionSwitchAccount,
                        )
                    }
                }
            }
        } else {
            val anonymousTitle = LocalStrings.current.sidebarAnonymousTitle
            PlaceholderImage(
                size = avatarSize,
                title = anonymousTitle,
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(Spacing.xxxs),
            ) {
                Text(
                    text = anonymousTitle,
                    style = MaterialTheme.typography.titleMedium,
                    color = fullColor,
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text =
                            buildString {
                                append(LocalStrings.current.nodeVia)
                                append(" ")
                                append(node)
                            },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleMedium,
                        color = ancillaryColor,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(
                        onClick = {
                            onOpenChangeInstance?.invoke()
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = LocalStrings.current.changeNodeDialogTitle,
                        )
                    }
                }
            }
        }
    }
}
