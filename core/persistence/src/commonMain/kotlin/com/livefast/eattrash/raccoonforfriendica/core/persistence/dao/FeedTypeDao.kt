package com.livefast.eattrash.raccoonforfriendica.core.persistence.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.livefast.eattrash.raccoonforfriendica.core.persistence.entities.AccountEntity
import com.livefast.eattrash.raccoonforfriendica.core.persistence.entities.FeedTypeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedTypeDao {
    @Insert
    suspend fun insert(item: FeedTypeEntity)

    @Insert
    suspend fun insertMany(vararg items: FeedTypeEntity)

    @Query("SELECT * FROM FeedTypeEntity WHERE accountId = :accountId ORDER BY `index`")
    suspend fun getAll(accountId: Long): List<FeedTypeEntity>

    @Query("SELECT * FROM FeedTypeEntity WHERE accountId = :accountId ORDER BY `index`")
    abstract fun getAllAsFlow(accountId: Long): Flow<List<FeedTypeEntity>>

    @Query("SELECT * FROM FeedTypeEntity WHERE accountId = :accountId AND type = :type")
    suspend fun getBy(accountId: Long, type: Int): FeedTypeEntity?

    @Query("SELECT * FROM FeedTypeEntity WHERE accountId = :accountId AND type = :type and info = :info")
    suspend fun getBy(accountId: Long, type: Int, info: String): FeedTypeEntity?

    @Update
    suspend fun update(item: FeedTypeEntity)

    @Update
    suspend fun updateMany(vararg items: FeedTypeEntity)

    @Delete
    suspend fun delete(item: FeedTypeEntity)
}
