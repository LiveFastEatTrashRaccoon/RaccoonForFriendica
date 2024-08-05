package com.livefast.eattrash.raccoonforfriendica.domain.content.data

data class AccountModel(
    val avatar: String? = null,
    val bot: Boolean = false,
    val created: String? = null,
    val displayName: String? = null,
    val entryCount: Int = 0,
    val fields: List<FieldModel> = emptyList(),
    val followers: Int = 0,
    val following: Int = 0,
    val group: Boolean = false,
    val handle: String? = null,
    val header: String? = null,
    val id: String,
    val locked: Boolean = false,
    val username: String? = null,
)
