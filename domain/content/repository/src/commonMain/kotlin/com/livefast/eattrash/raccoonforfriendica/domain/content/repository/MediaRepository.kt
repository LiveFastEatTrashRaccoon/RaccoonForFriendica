package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AttachmentModel

interface MediaRepository {
    suspend fun getBy(id: String): AttachmentModel?

    suspend fun create(bytes: ByteArray, album: String = "", alt: String = ""): AttachmentModel?

    suspend fun update(id: String, alt: String = ""): Boolean

    suspend fun delete(id: String): Boolean
}
