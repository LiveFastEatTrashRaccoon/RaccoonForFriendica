package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.FriendicaCircle
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query

interface CircleService {
    @GET("friendica/group_show")
    suspend fun getAll(): List<FriendicaCircle>

    @GET("friendica/group_show")
    suspend fun getBy(
        @Query("gid") id: String,
    ): FriendicaCircle
}
