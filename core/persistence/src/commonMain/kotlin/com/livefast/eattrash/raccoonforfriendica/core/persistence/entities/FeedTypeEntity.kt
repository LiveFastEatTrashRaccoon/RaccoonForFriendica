package com.livefast.eattrash.raccoonforfriendica.core.persistence.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["accountId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(
            value = ["accountId"],
        ),
    ],
)
data class FeedTypeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(defaultValue = "0") val accountId: Long = 0,
    @ColumnInfo(defaultValue = "0") val type: Int = 0,
    @ColumnInfo(defaultValue = "0") val index: Int = 0,
    @ColumnInfo val info: String? = null,
)
