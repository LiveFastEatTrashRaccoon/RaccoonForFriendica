package com.livefast.eattrash.raccoonforfriendica.domain.content.data

import kotlin.jvm.Transient

data class AttachmentModel(
    val description: String? = null,
    val blurHash: String? = null,
    val id: String,
    val mediaId: String = "",
    val previewUrl: String? = null,
    val type: MediaType = MediaType.Unknown,
    val url: String,
    val thumbnail: String? = null,
    @Transient
    val loading: Boolean = false,
    val album: String? = null,
    val originalWidth: Int? = null,
    val originalHeight: Int? = null,
    @Transient
    val fromGallery: Boolean = false,
)
