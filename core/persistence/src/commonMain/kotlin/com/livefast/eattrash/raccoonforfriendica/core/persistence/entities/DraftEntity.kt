package com.livefast.eattrash.raccoonforfriendica.core.persistence.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DraftEntity(
    @PrimaryKey(autoGenerate = false) val id: String,
    @ColumnInfo val mediaIds: String? = null,
    @ColumnInfo val inReplyToId: String? = null,
    @ColumnInfo val lang: String? = null,
    @ColumnInfo val sensitive: Boolean? = null,
    @ColumnInfo val spoiler: String? = null,
    @ColumnInfo val title: String? = null,
    @ColumnInfo val text: String? = null,
    @ColumnInfo val created: String? = null,
    @ColumnInfo val visibility: String? = null,
    @ColumnInfo val pollExpiresAt: String? = null,
    @ColumnInfo val pollMultiple: Boolean? = null,
    @ColumnInfo val pollOptions: String? = null,
    @ColumnInfo(defaultValue = "0") val localOnly: Boolean = false,
)
