package com.livefast.eattrash.raccoonforfriendica.core.persistence.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.livefast.eattrash.raccoonforfriendica.core.persistence.entities.SettingsEntity

@Dao
interface SettingsDao {
    @Insert
    suspend fun insert(item: SettingsEntity)

    @Query("SELECT * FROM SettingsEntity WHERE accountId = :accountId")
    suspend fun getBy(accountId: Long): SettingsEntity?

    @Update
    suspend fun update(item: SettingsEntity)

    @Delete
    suspend fun delete(item: SettingsEntity)
}
