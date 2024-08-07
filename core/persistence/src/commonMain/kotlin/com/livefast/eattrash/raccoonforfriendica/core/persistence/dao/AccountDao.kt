package com.livefast.eattrash.raccoonforfriendica.core.persistence.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.livefast.eattrash.raccoonforfriendica.core.persistence.entities.AccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    @Insert
    suspend fun insert(item: AccountEntity)

    @Query("SELECT * FROM AccountEntity")
    suspend fun getAll(): List<AccountEntity>

    @Query("SELECT * FROM AccountEntity WHERE handle = :handle")
    suspend fun getBy(handle: String): AccountEntity?

    @Query("SELECT * FROM AccountEntity WHERE active = 1")
    suspend fun getActive(): AccountEntity?

    @Query("SELECT * FROM AccountEntity WHERE active = 1")
    fun getActiveAsFlow(): Flow<List<AccountEntity>>

    @Update
    suspend fun update(item: AccountEntity)

    @Delete
    suspend fun delete(item: AccountEntity)
}
