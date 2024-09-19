package com.livefast.eattrash.raccoonforfriendica.domain.content.data

import kotlin.jvm.Transient

data class AttachmentModel(
    val album: String? = null,
    val blurHash: String? = null,
    val description: String? = null,
    @Transient val fromGallery: Boolean = false,
    val id: String,
    val mediaId: String = "",
    @Transient val loading: Boolean = false,
    val originalHeight: Int? = null,
    val originalWidth: Int? = null,
    val previewUrl: String? = null,
    val thumbnail: String? = null,
    val type: MediaType = MediaType.Unknown,
    val url: String,
)

val AttachmentModel.aspectRatio: Float
    get() =
        if (originalHeight == null || originalHeight == 0) {
            0f
        } else {
            (originalWidth ?: 0) / originalHeight.toFloat()
        }
