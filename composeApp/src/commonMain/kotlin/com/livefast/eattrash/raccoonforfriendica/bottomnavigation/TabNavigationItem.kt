package com.livefast.eattrash.raccoonforfriendica.bottomnavigation

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.BottomNavigationSection

@Composable
internal fun RowScope.BottomNavigationItem(
    section: BottomNavigationSection,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }

    NavigationBarItem(
        onClick = {
            onClick?.invoke()
        },
        selected = selected,
        interactionSource = interactionSource,
        icon = {
            BadgedBox(
                badge = {
                    val unreadCount = (section as? BottomNavigationSection.Inbox)?.unreadItems ?: 0
                    if (unreadCount > 0) {
                        Badge(
                            modifier = Modifier.align(Alignment.TopEnd).offset(x = (-5).dp),
                        ) {
                            Text(
                                text = if (unreadCount <= 10) "$unreadCount" else "10+",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                            )
                        }
                    }
                },
            ) {
                Icon(
                    imageVector = section.toIcon(),
                    contentDescription = null,
                )
            }
        },
        label = {
            Text(
                modifier = Modifier,
                text = section.toReadableName(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
    )
}

private fun BottomNavigationSection.toIcon(): ImageVector =
    when (this) {
        BottomNavigationSection.Explore -> Icons.Default.Explore
        BottomNavigationSection.Home -> Icons.AutoMirrored.Default.Article
        is BottomNavigationSection.Inbox -> Icons.Default.Inbox
        BottomNavigationSection.Profile -> Icons.Default.AccountCircle
    }

@Composable
private fun BottomNavigationSection.toReadableName(): String =
    when (this) {
        BottomNavigationSection.Explore -> LocalStrings.current.sectionTitleExplore
        BottomNavigationSection.Home -> LocalStrings.current.sectionTitleHome
        is BottomNavigationSection.Inbox -> LocalStrings.current.sectionTitleInbox
        BottomNavigationSection.Profile -> LocalStrings.current.sectionTitleProfile
    }
