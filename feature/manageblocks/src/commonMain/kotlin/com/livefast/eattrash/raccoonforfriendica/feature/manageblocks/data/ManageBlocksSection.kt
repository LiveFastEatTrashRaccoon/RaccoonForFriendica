package com.livefast.eattrash.raccoonforfriendica.feature.manageblocks.data

import androidx.compose.runtime.Composable
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings

sealed interface ManageBlocksSection {
    data object Muted : ManageBlocksSection

    data object Blocked : ManageBlocksSection

    data object Limited : ManageBlocksSection
}

@Composable
fun ManageBlocksSection.toReadableName(): String =
    when (this) {
        ManageBlocksSection.Blocked -> LocalStrings.current.manageBlocksSectionBlocked
        ManageBlocksSection.Muted -> LocalStrings.current.manageBlocksSectionMuted
        ManageBlocksSection.Limited -> LocalStrings.current.manageBlocksSectionLimited
    }
