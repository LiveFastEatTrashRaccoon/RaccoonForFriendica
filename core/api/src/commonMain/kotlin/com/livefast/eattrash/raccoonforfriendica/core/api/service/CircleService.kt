package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.FriendicaCircle
import de.jensklingenberg.ktorfit.http.GET

interface CircleService {
    @GET("friendica/group_show")
    suspend fun getAll(): List<FriendicaCircle>
}
