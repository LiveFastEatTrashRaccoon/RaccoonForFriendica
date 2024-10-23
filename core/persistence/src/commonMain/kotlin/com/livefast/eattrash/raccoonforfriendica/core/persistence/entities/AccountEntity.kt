package com.livefast.eattrash.raccoonforfriendica.core.persistence.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AccountEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(index = true) val handle: String,
    @ColumnInfo(defaultValue = "0") val active: Boolean = false,
    val remoteId: String? = null,
    val avatar: String? = null,
    val displayName: String? = null,
    val pushAuth: String? = null,
    val pushPubKey: String? = null,
    val pushPrivKey: String? = null,
    val pushServerKey: String? = null,
    val unifiedPushUrl: String? = null,
)
