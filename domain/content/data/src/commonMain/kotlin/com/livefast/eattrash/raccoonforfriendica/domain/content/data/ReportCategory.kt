package com.livefast.eattrash.raccoonforfriendica.domain.content.data

import androidx.compose.runtime.Composable
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings

sealed interface ReportCategory {
    data object Spam : ReportCategory

    data object Legal : ReportCategory

    data object Violation : ReportCategory

    data object Other : ReportCategory
}

@Composable
fun ReportCategory.toReadableName(): String =
    when (this) {
        ReportCategory.Legal -> LocalStrings.current.reportCategoryLegal
        ReportCategory.Other -> LocalStrings.current.reportCategoryOther
        ReportCategory.Spam -> LocalStrings.current.reportCategorySpam
        ReportCategory.Violation -> LocalStrings.current.reportCategoryViolation
    }
