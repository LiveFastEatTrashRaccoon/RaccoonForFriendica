package com.livefast.eattrash.raccoonforfriendica.feature.drawer.components

import androidx.compose.foundation.clickable
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
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel

@Composable
internal fun DrawerHeader(
    modifier: Modifier = Modifier,
    user: UserModel? = null,
    node: String? = null,
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
            if (userAvatar.isNotEmpty()) {
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

            Row {
                Column(
                    verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
                ) {
                    Text(
                        text = username,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = fullColor,
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
                        style = MaterialTheme.typography.titleSmall,
                        color = ancillaryColor,
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    modifier =
                        Modifier.clickable {
                            onOpenSwitchAccount?.invoke()
                        },
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                )
            }
        } else {
            val anonymousTitle = LocalStrings.current.sidebarAnonymousTitle
            PlaceholderImage(
                size = avatarSize,
                title = anonymousTitle,
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
            ) {
                Text(
                    text = anonymousTitle,
                    style = MaterialTheme.typography.titleMedium,
                    color = fullColor,
                )
                Row {
                    Text(
                        text = node.orEmpty(),
                        style = MaterialTheme.typography.titleSmall,
                        color = ancillaryColor,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        modifier =
                            Modifier.clickable {
                                onOpenChangeInstance?.invoke()
                            },
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                    )
                }
            }
        }
    }
}
