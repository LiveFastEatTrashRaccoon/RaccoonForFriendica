package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.MediaAlbumModel

interface PhotoAlbumRepository {
    suspend fun getAll(): List<MediaAlbumModel>

    suspend fun update(
        oldName: String,
        newName: String,
    ): Boolean

    suspend fun delete(name: String): Boolean
}
