package com.livefast.eattrash.raccoonforfriendica.core.persistence.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SettingsEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val accountId: Long = 0,
    val lang: String = "en",
    val theme: Int = 0,
    val fontFamily: Int = 0,
    val dynamicColors: Boolean = false,
    val customSeedColor: Int? = null,
)
