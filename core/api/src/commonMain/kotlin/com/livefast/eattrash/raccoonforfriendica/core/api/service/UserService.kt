package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Account
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.CredentialAccount
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Relationship
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Status
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Suggestion
import com.livefast.eattrash.raccoonforfriendica.core.api.form.FollowUserForm
import com.livefast.eattrash.raccoonforfriendica.core.api.form.MuteUserForm
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.PATCH
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.forms.MultiPartFormDataContent

interface UserService {
    @GET("v1/accounts/verify_credentials")
    suspend fun verifyCredentials(): CredentialAccount

    @GET("v1/accounts/{id}")
    suspend fun getById(@Path("id") id: String): Account

    @GET("v1/accounts/search")
    suspend fun search(
        @Query("q") query: String = "",
        @Query("offset") offset: Int = 0,
        @Query("resolve") resolve: Boolean = false,
        @Query("following") following: Boolean = false,
    ): List<Account>

    @GET("v1/accounts/{id}/statuses")
    suspend fun getStatuses(
        @Path("id") id: String,
        @Query("max_id") maxId: String? = null,
        @Query("min_id") minId: String? = null,
        @Query("only_media") onlyMedia: Boolean = false,
        @Query("exclude_replies") excludeReplies: Boolean = false,
        @Query("exclude_reblogs") excludeReblogs: Boolean = false,
        @Query("pinned") pinned: Boolean = false,
        @Query("limit") limit: Int = 20,
    ): List<Status>

    @GET("v1/accounts/relationships")
    suspend fun getRelationships(@Query("id[]") id: List<String>): List<Relationship>

    @GET("v2/suggestions")
    suspend fun getSuggestions(@Query("limit") limit: Int): List<Suggestion>

    @GET("v1/accounts/{id}/followers")
    suspend fun getFollowers(
        @Path("id") id: String,
        @Query("max_id") maxId: String? = null,
        @Query("min_id") minId: String? = null,
        @Query("limit") limit: Int = 20,
    ): List<Account>

    @GET("v1/accounts/{id}/following")
    suspend fun getFollowing(
        @Path("id") id: String,
        @Query("max_id") maxId: String? = null,
        @Query("min_id") minId: String? = null,
        @Query("limit") limit: Int = 20,
    ): List<Account>

    @POST("v1/accounts/{id}/follow")
    @Headers("Content-Type: application/json")
    suspend fun follow(@Path("id") id: String, @Body data: FollowUserForm): Relationship

    @POST("v1/accounts/{id}/unfollow")
    @Headers("Content-Type: application/json")
    suspend fun unfollow(@Path("id") id: String): Relationship

    @GET("v1/favourites")
    suspend fun getFavorites(
        @Query("max_id") maxId: String? = null,
        @Query("min_id") minId: String? = null,
        @Query("limit") limit: Int = 20,
    ): List<Status>

    @GET("v1/bookmarks")
    suspend fun getBookmarks(
        @Query("max_id") maxId: String? = null,
        @Query("min_id") minId: String? = null,
        @Query("limit") limit: Int = 20,
    ): List<Status>

    @POST("v1/accounts/{id}/mute")
    @Headers("Content-Type: application/json")
    suspend fun mute(@Path("id") id: String, @Body data: MuteUserForm): Relationship

    @POST("v1/accounts/{id}/unmute")
    suspend fun unmute(@Path("id") id: String): Relationship

    @POST("v1/accounts/{id}/block")
    @Headers("Content-Type: application/json")
    suspend fun block(@Path("id") id: String): Relationship

    @POST("v1/accounts/{id}/unblock")
    suspend fun unblock(@Path("id") id: String): Relationship

    @GET("v1/mutes")
    suspend fun getMuted(@Query("max_id") maxId: String? = null, @Query("limit") limit: Int = 20): List<Account>

    @GET("v1/blocks")
    suspend fun getBlocked(@Query("max_id") maxId: String? = null, @Query("limit") limit: Int = 20): List<Account>

    @PATCH("v1/accounts/update_credentials")
    suspend fun updateProfile(@Body content: FormDataContent): Account

    @PATCH("v1/accounts/update_credentials")
    suspend fun updateProfileImage(@Body content: MultiPartFormDataContent): Account

    @POST("v1/accounts/{id}/note")
    suspend fun updatePersonalNote(@Path("id") id: String, @Body data: FormDataContent): Relationship
}
