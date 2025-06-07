package com.livefast.eattrash.raccoonforfriendica.core.persistence.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.livefast.eattrash.raccoonforfriendica.core.persistence.entities.UserRateLimitEntity

@Dao
interface UserRateLimitDao {
    @Insert
    suspend fun insert(item: UserRateLimitEntity)

    @Query("SELECT * FROM UserRateLimitEntity WHERE accountId = :accountId")
    suspend fun getAll(accountId: Long): List<UserRateLimitEntity>

    @Query("SELECT * FROM UserRateLimitEntity WHERE accountId = :accountId AND userHandle = :handle")
    suspend fun getBy(accountId: Long, handle: String): UserRateLimitEntity?

    @Update
    suspend fun update(item: UserRateLimitEntity)

    @Delete
    suspend fun delete(item: UserRateLimitEntity)
}
