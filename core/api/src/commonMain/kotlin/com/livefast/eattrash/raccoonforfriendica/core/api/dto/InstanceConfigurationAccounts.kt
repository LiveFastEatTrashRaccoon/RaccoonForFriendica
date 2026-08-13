package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InstanceConfigurationAccounts(
    @SerialName("max_featured_tags") val maxFeaturedTags: Int? = null,
    @SerialName("max_pinned_statuses") val maxPinnedStatuses: Int? = null,
    @SerialName("max_note_length") val maxBioLength: Int? = null,
    @SerialName("max_display_name_length") val maxDisplayNameLength: Int? = null,
    @SerialName("max_profile_fields") val maxProfileFields: Int? = null,
    @SerialName("profile_field_name_limit") val profileFieldNameLimit: Int? = null,
    @SerialName("profile_field_value_limit") val profileFieldValueLimit: Int? = null,
)
