package com.livefast.eattrash.raccoonforfriendica.core.persistence.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.livefast.eattrash.raccoonforfriendica.core.persistence.entities.DraftEntity

@Dao
interface DraftDao {
    @Query("SELECT * FROM DraftEntity LIMIT :limit OFFSET :offset")
    suspend fun getAll(offset: Int, limit: Int): List<DraftEntity>

    @Insert
    suspend fun insert(item: DraftEntity)

    @Query("SELECT * FROM DraftEntity WHERE id = :id")
    suspend fun getBy(id: String): DraftEntity?

    @Update
    suspend fun update(item: DraftEntity)

    @Delete
    suspend fun delete(item: DraftEntity)
}
