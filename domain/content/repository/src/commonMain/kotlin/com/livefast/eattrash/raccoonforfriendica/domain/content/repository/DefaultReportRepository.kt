package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.ReportCategory
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toDto
import io.ktor.client.request.forms.FormDataContent
import io.ktor.http.Parameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

internal class DefaultReportRepository(private val provider: ServiceProvider) : ReportRepository {
    override suspend fun create(
        userId: String,
        entryIds: List<String>?,
        comment: String?,
        forward: Boolean,
        category: ReportCategory,
        ruleIds: List<String>?,
    ): Boolean = withContext(Dispatchers.IO) {
        runCatching {
            val data =
                FormDataContent(
                    Parameters.build {
                        append("account_id", userId)
                        if (entryIds != null) {
                            append("status_ids", entryIds.joinToString(","))
                        }
                        append("forward", forward.toString())
                        append("category", category.toDto())
                        if (comment != null) {
                            append("comment", comment)
                        }
                        if (ruleIds != null) {
                            append("rule_ids", ruleIds.joinToString(","))
                        }
                    },
                )
            val res = provider.reports.create(data)
            res.isSuccessful
        }.getOrElse { false }
    }
}
