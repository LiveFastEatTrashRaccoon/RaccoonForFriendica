package com.livefast.eattrash.raccoonforfriendica.domain.content.data

import androidx.compose.runtime.Composable
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings

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

fun UnpublishedType.toInt(): Int =
    when (this) {
        UnpublishedType.Drafts -> 1
        UnpublishedType.Scheduled -> 0
    }

fun Int.toUnpublishedType(): UnpublishedType =
    when (this) {
        1 -> UnpublishedType.Drafts
        else -> UnpublishedType.Scheduled
    }
