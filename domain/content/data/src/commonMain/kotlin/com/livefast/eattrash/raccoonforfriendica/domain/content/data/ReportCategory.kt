package com.livefast.eattrash.raccoonforfriendica.domain.content.data

sealed interface ReportCategory {
    data object Spam : ReportCategory

    data object Legal : ReportCategory

    data object Violation : ReportCategory

    data object Other : ReportCategory
}
