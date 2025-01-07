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
            value = ["userHandle", "accountId"],
            unique = true,
        ),
    ],
)
data class UserRateLimitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(defaultValue = "0") val accountId: Long = 0,
    val userHandle: String,
    @ColumnInfo(defaultValue = "1") val rate: Double = 1.0,
)
