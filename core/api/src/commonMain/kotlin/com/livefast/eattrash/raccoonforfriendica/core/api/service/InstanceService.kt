package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Instance
import de.jensklingenberg.ktorfit.http.GET

interface InstanceService {
    @GET("v1/instance")
    suspend fun getInfo(): Instance
}
