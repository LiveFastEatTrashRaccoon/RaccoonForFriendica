package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Poll
import com.livefast.eattrash.raccoonforfriendica.core.api.form.SubmitPollVoteForm

interface PollService {
    suspend fun getById(id: String): Poll

    suspend fun vote(id: String, data: SubmitPollVoteForm): Poll
}
