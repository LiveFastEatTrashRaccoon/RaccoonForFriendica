package com.livefast.eattrash.raccoonforfriendica.bottomnavigation

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
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
            Icon(
                imageVector = section.toIcon(),
                contentDescription = null,
            )
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
        BottomNavigationSection.Home -> Icons.Default.Home
        BottomNavigationSection.Inbox -> Icons.Default.Inbox
        BottomNavigationSection.Profile -> Icons.Default.AccountCircle
    }

@Composable
private fun BottomNavigationSection.toReadableName(): String =
    when (this) {
        BottomNavigationSection.Explore -> LocalStrings.current.sectionTitleExplore
        BottomNavigationSection.Home -> LocalStrings.current.sectionTitleHome
        BottomNavigationSection.Inbox -> LocalStrings.current.sectionTitleInbox
        BottomNavigationSection.Profile -> LocalStrings.current.sectionTitleProfile
    }
