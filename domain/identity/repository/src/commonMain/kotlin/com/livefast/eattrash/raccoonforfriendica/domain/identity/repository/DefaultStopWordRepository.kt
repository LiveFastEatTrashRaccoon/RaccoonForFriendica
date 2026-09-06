package com.livefast.eattrash.raccoonforfriendica.domain.identity.repository

import com.livefast.eattrash.raccoonforfriendica.core.preferences.store.TemporaryKeyStore
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class DefaultStopWordRepository(private val keyStore: TemporaryKeyStore) : StopWordRepository {
    override suspend fun get(accountId: Long?): List<String> {
        val key = getKey(accountId)
        val res = keyStore.get(key, emptyList())
        return res.filter { it.isNotEmpty() }
    }

    override suspend fun update(accountId: Long?, items: List<String>) {
        val key = getKey(accountId)
        keyStore.save(key, items)
    }

    private fun getKey(accountId: Long?): String = buildString {
        append("StopWordRepository")
        if (accountId != null) {
            append(".")
            append(accountId)
        }
        append(".items")
    }
}
