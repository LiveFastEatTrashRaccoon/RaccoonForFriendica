package com.livefast.eattrash.raccoonforfriendica.domain.content.data

import androidx.compose.runtime.Composable
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings

sealed interface UnpublishedType {
    data object Scheduled : UnpublishedType

    data object Drafts : UnpublishedType
}

@Composable
fun UnpublishedType.toReadableName(): String =
    when (this) {
        UnpublishedType.Drafts -> LocalStrings.current.unpublishedSectionDrafts
        UnpublishedType.Scheduled -> LocalStrings.current.unpublishedSectionScheduled
    }
