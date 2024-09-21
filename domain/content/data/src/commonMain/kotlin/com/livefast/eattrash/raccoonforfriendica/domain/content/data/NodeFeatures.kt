package com.livefast.eattrash.raccoonforfriendica.domain.content.data

data class NodeFeatures(
    val supportsPhotoGallery: Boolean = false,
    val supportsDirectMessages: Boolean = false,
    val supportsEntryTitles: Boolean = false,
    val supportsPolls: Boolean = false,
    val supportsCustomCircles: Boolean = false,
    val supportReportCategoryRuleViolation: Boolean = false,
)
