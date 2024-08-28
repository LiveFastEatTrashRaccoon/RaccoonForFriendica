package com.livefast.eattrash.raccoonforfriendica.core.persistence.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AccountEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(index = true) val handle: String,
    @ColumnInfo(defaultValue = "0") val active: Boolean = false,
    @ColumnInfo val remoteId: String? = null,
    @ColumnInfo val avatar: String? = null,
    @ColumnInfo val displayName: String? = null,
)
