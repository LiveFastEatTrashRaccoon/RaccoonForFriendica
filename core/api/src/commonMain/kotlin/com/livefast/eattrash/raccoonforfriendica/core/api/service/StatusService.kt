package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Account
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.ScheduledStatus
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Status
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.StatusContext
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.StatusSource
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Translation
import com.livefast.eattrash.raccoonforfriendica.core.api.form.CreateStatusForm
import io.ktor.client.request.forms.FormDataContent

interface StatusService {
    suspend fun get(id: String): Status

    suspend fun getContext(id: String): StatusContext

    suspend fun getSource(id: String): StatusSource

    suspend fun reblog(id: String, data: FormDataContent): Status

    suspend fun unreblog(id: String): Status

    suspend fun pin(id: String): Status

    suspend fun unpin(id: String): Status

    suspend fun bookmark(id: String): Status

    suspend fun unbookmark(id: String): Status

    suspend fun favorite(id: String): Status

    suspend fun unfavorite(id: String): Status

    suspend fun getFavoritedBy(id: String, maxId: String? = null, limit: Int = 20): List<Account>

    suspend fun getRebloggedBy(id: String, maxId: String? = null, limit: Int = 20): List<Account>

    suspend fun create(key: String, data: CreateStatusForm): Status

    suspend fun update(id: String, data: CreateStatusForm): Status

    suspend fun delete(id: String): Boolean

    suspend fun getScheduled(maxId: String? = null, minId: String? = null, limit: Int = 20): List<ScheduledStatus>

    suspend fun getScheduledById(id: String): ScheduledStatus

    suspend fun updateScheduled(id: String, data: FormDataContent): ScheduledStatus

    suspend fun deleteScheduled(id: String): Boolean

    suspend fun dislike(data: FormDataContent): Boolean

    suspend fun undislike(data: FormDataContent): Boolean

    suspend fun translate(id: String, data: FormDataContent): Translation
}
