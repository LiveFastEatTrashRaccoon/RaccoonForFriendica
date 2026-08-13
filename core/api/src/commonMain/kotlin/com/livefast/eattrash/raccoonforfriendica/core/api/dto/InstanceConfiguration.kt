package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InstanceConfiguration(
    @SerialName("accounts") val accounts: InstanceConfigurationAccounts? = null,
    @SerialName("statuses") val statuses: InstanceConfigurationStatuses? = null,
    @SerialName("vapid") val vapid: VapidKey? = null,
)
