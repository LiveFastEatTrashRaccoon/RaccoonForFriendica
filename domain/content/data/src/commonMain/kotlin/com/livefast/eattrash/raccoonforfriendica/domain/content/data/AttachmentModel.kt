package com.livefast.eattrash.raccoonforfriendica.domain.content.data

data class AttachmentModel(
    val description: String? = null,
    val id: String,
    val previewUrl: String? = null,
    val type: MediaType = MediaType.Unknown,
    val url: String,
)
