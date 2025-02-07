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
            unique = true,
        ),
    ],
)
data class SettingsEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(defaultValue = "0") val accountId: Long = 0,
    @ColumnInfo(defaultValue = "en") val lang: String = "en",
    @ColumnInfo(defaultValue = "0") val theme: Int = 0,
    @ColumnInfo(defaultValue = "0") val fontFamily: Int = 0,
    @ColumnInfo(defaultValue = "0") val fontScale: Int = 0,
    @ColumnInfo(defaultValue = "0") val dynamicColors: Boolean = false,
    val customSeedColor: Int? = null,
    @ColumnInfo(defaultValue = "0") val defaultTimelineType: Int = 0,
    @ColumnInfo(defaultValue = "0") val includeNsfw: Boolean = false,
    @ColumnInfo(defaultValue = "0") val blurNsfw: Boolean = true,
    @ColumnInfo(defaultValue = "0") val urlOpeningMode: Int = 0,
    @ColumnInfo(defaultValue = "0") val defaultPostVisibility: Int = 0,
    @ColumnInfo(defaultValue = "1") val defaultReplyVisibility: Int = 1,
    @ColumnInfo(defaultValue = "0") val excludeRepliesFromTimeline: Boolean = false,
    @ColumnInfo(defaultValue = "1") val openGroupsInForumModeByDefault: Boolean = true,
    @ColumnInfo(defaultValue = "0") val markupMode: Int = 0,
    @ColumnInfo(defaultValue = "0") val maxPostBodyLines: Int = 0,
    val defaultTimelineId: String? = null,
    @ColumnInfo(defaultValue = "0") val notificationMode: Int = 0,
    val pullNotificationCheckInterval: Long? = null,
    @ColumnInfo(defaultValue = "1") val autoloadImages: Int = 1,
    @ColumnInfo(defaultValue = "1") val hideNavigationBarWhileScrolling: Boolean = true,
    @ColumnInfo(defaultValue = "0") val barTheme: Int = 0,
    @ColumnInfo(defaultValue = "0") val timelineLayout: Int = 0,
    @ColumnInfo(defaultValue = "1") val replyDepth: Int = 1,
)
