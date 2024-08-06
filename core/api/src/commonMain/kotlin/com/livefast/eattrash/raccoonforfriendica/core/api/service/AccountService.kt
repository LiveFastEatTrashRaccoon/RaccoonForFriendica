package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Account
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Status
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query

interface AccountService {
    @GET("accounts/{id}")
    suspend fun getById(
        @Path("id") id: String,
    ): Account

    @GET("accounts/{id}/statuses")
    suspend fun getStatuses(
        @Path("id") id: String,
        @Query("max_id") maxId: String? = null,
        @Query("min_id") minId: String? = null,
        @Query("only_media") onlyMedia: Boolean = false,
        @Query("exclude_replies") excludeReplies: Boolean = false,
        @Query("exclude_reblogs") excludeReblogs: Boolean = false,
        @Query("pinned") pinned: Boolean = false,
    ): List<Status>
}
