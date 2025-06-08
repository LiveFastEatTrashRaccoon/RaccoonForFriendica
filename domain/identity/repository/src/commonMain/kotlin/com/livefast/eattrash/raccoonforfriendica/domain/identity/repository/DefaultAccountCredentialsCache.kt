package com.livefast.eattrash.raccoonforfriendica.domain.identity.repository

import com.livefast.eattrash.raccoonforfriendica.core.preferences.store.TemporaryKeyStore

internal class DefaultAccountCredentialsCache(private val keyStore: TemporaryKeyStore) : AccountCredentialsCache {
    override fun get(accountId: Long): ApiCredentials? {
        val type = keyStore[getKeyForType(accountId), ""]
        val part1 = keyStore[getKeyForPart1(accountId), ""]
        val part2 = keyStore[getKeyForPart2(accountId), ""]
        return when {
            type == METHOD_BASIC && part1.isNotEmpty() && part2.isNotEmpty() ->
                ApiCredentials.HttpBasic(
                    user = part1,
                    pass = part2,
                )

            type == METHOD_OAUTH_2 && part1.isNotEmpty() ->
                ApiCredentials.OAuth2(
                    accessToken = part1,
                    refreshToken = part2,
                )

            else -> null
        }
    }

    override fun save(accountId: Long, credentials: ApiCredentials) {
        val type: String
        val part1: String
        val part2: String
        when (credentials) {
            is ApiCredentials.HttpBasic -> {
                type = METHOD_BASIC
                part1 = credentials.user
                part2 = credentials.pass
            }

            is ApiCredentials.OAuth2 -> {
                type = METHOD_OAUTH_2
                part1 = credentials.accessToken
                part2 = credentials.refreshToken
            }
        }
        keyStore.save(getKeyForType(accountId), type)
        keyStore.save(getKeyForPart1(accountId), part1)
        keyStore.save(getKeyForPart2(accountId), part2)
    }

    override fun remove(accountId: Long) {
        keyStore.remove(getKeyForType(accountId))
        keyStore.remove(getKeyForPart1(accountId))
        keyStore.remove(getKeyForPart2(accountId))
    }

    private fun getKeyForPart1(accountId: Long) = "$PREFIX.$accountId.$KEY_PART_1"

    private fun getKeyForPart2(accountId: Long) = "$PREFIX.$accountId.$KEY_PART_2"

    private fun getKeyForType(accountId: Long) = "$PREFIX.$accountId.$KEY_TYPE"

    companion object {
        private const val PREFIX = "AccountCredentialsRepository"
        private const val KEY_PART_1 = "part1"
        private const val KEY_PART_2 = "part2"
        private const val KEY_TYPE = "type"
        private const val METHOD_BASIC = "HTTPBasic"
        private const val METHOD_OAUTH_2 = "OAuth2"
    }
}
