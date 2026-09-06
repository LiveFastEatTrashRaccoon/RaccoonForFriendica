package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Collection
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.CollectionItem
import com.livefast.eattrash.raccoonforfriendica.core.api.form.AddCollectionItemForm
import com.livefast.eattrash.raccoonforfriendica.core.api.form.CreateCollectionForm

interface CollectionService {
    suspend fun create(data: CreateCollectionForm): Collection
    suspend fun get(id: String): Collection
    suspend fun delete(id: String): Boolean
    suspend fun update(id: String, data: CreateCollectionForm): Collection
    suspend fun addItem(id: String, data: AddCollectionItemForm): CollectionItem
    suspend fun deleteItem(id: String, itemId: String): Boolean
    suspend fun revokeInclusion(id: String, itemId: String): Boolean
}
