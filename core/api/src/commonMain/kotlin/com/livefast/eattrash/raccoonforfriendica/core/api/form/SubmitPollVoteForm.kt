package com.livefast.eattrash.raccoonforfriendica.core.api.form

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SubmitPollVoteForm(@SerialName("choices") val choices: List<Int>)
