package com.livefast.eattrash.raccoonforfriendica.feature.manageblocks.data

import androidx.compose.runtime.Composable
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings

sealed interface ManageBlocksSection {
    data object Muted : ManageBlocksSection

    data object Blocked : ManageBlocksSection
}

@Composable
fun ManageBlocksSection.toReadableName(): String =
    when (this) {
        ManageBlocksSection.Blocked -> LocalStrings.current.manageBlocksSectionBlocked
        ManageBlocksSection.Muted -> LocalStrings.current.manageBlocksSectionMuted
    }

fun ManageBlocksSection.toInt(): Int =
    when (this) {
        ManageBlocksSection.Muted -> 0
        ManageBlocksSection.Blocked -> 1
    }

fun Int.toManageBlocksSection(): ManageBlocksSection =
    when (this) {
        1 -> ManageBlocksSection.Blocked
        else -> ManageBlocksSection.Muted
    }
