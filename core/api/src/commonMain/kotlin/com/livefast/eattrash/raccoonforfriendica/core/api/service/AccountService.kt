package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Account
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Relationship
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Status
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Suggestion
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

    @GET("accounts/lookup")
    suspend fun lookup(
        @Query("acct") acct: String,
    ): Account

    @GET("accounts/relationships")
    suspend fun getRelationships(
        @Query("id") id: String,
    ): List<Relationship>

    // TODO: the v1 API is deprecated
    @GET("suggestions")
    suspend fun getSuggestions(
        @Query("limit") limit: Int,
    ): List<Suggestion>
}
