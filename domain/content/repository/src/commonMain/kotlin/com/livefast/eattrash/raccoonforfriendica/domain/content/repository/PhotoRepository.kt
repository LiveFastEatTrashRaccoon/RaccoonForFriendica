package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AttachmentModel

interface PhotoRepository {
    suspend fun create(
        bytes: ByteArray,
        album: String = "",
        alt: String = "",
    ): AttachmentModel?

    suspend fun update(
        id: String,
        bytes: ByteArray? = null,
        album: String = "",
        newAlbum: String? = null,
        alt: String = "",
    ): Boolean

    suspend fun delete(id: String): Boolean
}
