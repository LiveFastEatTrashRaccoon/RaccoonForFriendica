package com.livefast.eattrash.raccoonforfriendica.core.persistence.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.livefast.eattrash.raccoonforfriendica.core.persistence.entities.AccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class AccountDao {
    @Insert
    abstract suspend fun insert(item: AccountEntity)

    @Query("SELECT * FROM AccountEntity")
    abstract suspend fun getAll(): List<AccountEntity>

    @Query("SELECT * FROM AccountEntity")
    abstract fun getAllAsFlow(): Flow<List<AccountEntity>>

    @Query("SELECT * FROM AccountEntity WHERE id = :id")
    abstract suspend fun getBy(id: Long): AccountEntity?

    @Query("SELECT * FROM AccountEntity WHERE handle = :handle")
    abstract suspend fun getBy(handle: String): AccountEntity?

    @Query("SELECT * FROM AccountEntity WHERE active = 1")
    abstract suspend fun getActive(): AccountEntity?

    @Query("SELECT * FROM AccountEntity WHERE active = 1")
    abstract fun getActiveAsFlow(): Flow<List<AccountEntity>>

    @Update
    abstract suspend fun update(item: AccountEntity)

    @Transaction
    open suspend fun replaceActive(old: AccountEntity, new: AccountEntity) {
        update(old)
        update(new)
    }

    @Delete
    abstract suspend fun delete(item: AccountEntity)
}
