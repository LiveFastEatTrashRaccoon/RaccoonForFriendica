package com.livefast.eattrash.raccoonforfriendica.core.api.form

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.ContentVisibility
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReblogPostForm(
    @SerialName("visibility") val visibility: String = ContentVisibility.PUBLIC,
)
