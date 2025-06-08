package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AnnouncementModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

internal class DefaultAnnouncementRepository(private val provider: ServiceProvider) : AnnouncementRepository {
    private val mutex = Mutex()
    private val cachedValues: MutableList<AnnouncementModel> = mutableListOf()

    override suspend fun getAll(refresh: Boolean): List<AnnouncementModel>? = withContext(Dispatchers.IO) {
        if (refresh) {
            mutex.withLock {
                cachedValues.clear()
            }
        }
        if (cachedValues.isNotEmpty()) {
            // map to a new immutable list or Compose freaks out
            return@withContext cachedValues.map { it }
        }
        runCatching {
            val response = provider.announcements.getAll()
            response
                .map { it.toModel() }
                .also {
                    mutex.withLock {
                        cachedValues.addAll(it)
                    }
                }
        }.getOrNull()
    }

    override suspend fun markAsRead(id: String): Boolean = withContext(Dispatchers.IO) {
        runCatching {
            val res = provider.announcements.dismiss(id)
            res.isSuccessful
        }.getOrElse { false }
    }

    override suspend fun addReaction(id: String, reaction: String): Boolean = withContext(Dispatchers.IO) {
        runCatching {
            val res =
                provider.announcements.addReaction(
                    id = id,
                    name = reaction,
                )
            res.isSuccessful
        }.getOrElse { false }
    }

    override suspend fun removeReaction(id: String, reaction: String): Boolean = withContext(Dispatchers.IO) {
        runCatching {
            val res =
                provider.announcements.removeReaction(
                    id = id,
                    name = reaction,
                )
            res.isSuccessful
        }.getOrElse { false }
    }
}
