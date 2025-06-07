package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Account
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.EditListForm
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.EditListMembersForm
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.FriendicaCircle
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.UserList
import de.jensklingenberg.ktorfit.Response
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.HTTP
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query

interface ListService {
    @GET("v1/lists")
    suspend fun getAll(): List<UserList>

    @GET("friendica/group_show")
    suspend fun getFriendicaCircles(): List<FriendicaCircle>

    @GET("v1/lists/{id}")
    suspend fun getBy(@Path("id") id: String): UserList

    @GET("v1/lists/{id}/accounts")
    suspend fun getMembers(
        @Path("id") id: String,
        @Query("max_id") maxId: String? = null,
        @Query("limit") limit: Int = 20,
    ): List<Account>

    @POST("v1/lists")
    @Headers("Content-Type: application/json")
    suspend fun create(@Body data: EditListForm): UserList

    @PUT("v1/lists/{id}")
    @Headers("Content-Type: application/json")
    suspend fun update(@Path("id") id: String, @Body data: EditListForm): UserList

    @DELETE("v1/lists/{id}")
    suspend fun delete(@Path("id") id: String): Response<Unit>

    @POST("v1/lists/{id}/accounts")
    @Headers("Content-Type: application/json")
    suspend fun addMembers(@Path("id") id: String, @Body data: EditListMembersForm): Response<Unit>

    @HTTP(method = "DELETE", path = "v1/lists/{id}/accounts", hasBody = true)
    @Headers("Content-Type: application/json")
    suspend fun removeMembers(@Path("id") id: String, @Body data: EditListMembersForm): Response<Unit>
}
