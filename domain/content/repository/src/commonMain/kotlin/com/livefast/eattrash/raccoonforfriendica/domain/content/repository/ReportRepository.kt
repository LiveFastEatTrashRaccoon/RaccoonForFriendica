package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.ReportCategory

interface ReportRepository {
    suspend fun create(
        userId: String,
        entryIds: List<String>? = null,
        comment: String? = null,
        forward: Boolean = false,
        category: ReportCategory = ReportCategory.Other,
        ruleIds: List<String>? = null,
    ): Boolean
}
