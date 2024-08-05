package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Account
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path

interface AccountService {
    @GET("accounts/{id}")
    suspend fun getById(
        @Path("id") id: String,
    ): Account
}
