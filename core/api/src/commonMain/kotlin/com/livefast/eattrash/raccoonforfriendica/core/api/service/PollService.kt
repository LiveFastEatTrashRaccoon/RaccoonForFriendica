package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Poll
import com.livefast.eattrash.raccoonforfriendica.core.api.form.SubmitPollVoteForm
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path

interface PollService {
    @GET("v1/polls/{id}")
    suspend fun getById(
        @Path("id") id: String,
    ): Poll

    @POST("v1/polls/{id}/votes")
    @Headers("Content-Type: application/json")
    suspend fun vote(
        @Path("id") id: String,
        @Body data: SubmitPollVoteForm,
    ): Poll
}
