package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InstanceConfiguration(@SerialName("statuses") val statuses: InstanceConfigurationStatuses? = null)
