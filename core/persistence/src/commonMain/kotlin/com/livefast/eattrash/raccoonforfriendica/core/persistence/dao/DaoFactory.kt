package com.livefast.eattrash.raccoonforfriendica.core.persistence.dao

interface DaoFactory {

    fun getAccountDao(): AccountDao

    fun getSettingsDao(): SettingsDao

    fun getDraftDao(): DraftDao

    fun getUserRateLimitDao(): UserRateLimitDao
}
