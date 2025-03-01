package com.livefast.eattrash.raccoonforfriendica.domain.content.data

data class NodeFeatures(
    val supportsPhotoGallery: Boolean = false,
    val supportsDirectMessages: Boolean = false,
    val supportsEntryTitles: Boolean = false,
    val supportsPolls: Boolean = false,
    val supportsCustomCircles: Boolean = false,
    val supportReportCategoryRuleViolation: Boolean = false,
    val supportsBBCode: Boolean = false,
    val supportsMarkdown: Boolean = false,
    val supportsEntryShare: Boolean = false,
    val supportsCalendar: Boolean = false,
    val supportsAnnouncements: Boolean = false,
    val supportsDislike: Boolean = false,
    val supportsTranslation: Boolean = false,
    val supportsInlineImages: Boolean = false,
    val supportsLocalVisibility: Boolean = false,
)
