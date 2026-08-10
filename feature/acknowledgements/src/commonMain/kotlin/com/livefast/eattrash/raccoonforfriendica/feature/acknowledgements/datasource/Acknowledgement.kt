package com.livefast.eattrash.raccoonforfriendica.feature.acknowledgements.datasource

import kotlinx.serialization.Serializable

@Serializable
internal data class Acknowledgement(
    val title: String? = null,
    val subtitle: String? = null,
    val avatar: String? = null,
    val url: String? = null,
)
