package com.livefast.eattrash.raccoonforfriendica.domain.content.data

data class NodeInfoModel(
    val activeUsers: Int? = null,
    val attachmentLimit: Int? = null,
    val characterLimit: Int? = null,
    val contact: UserModel? = null,
    val description: String? = null,
    val domain: String? = null,
    val languages: List<String> = emptyList(),
    val rules: List<RuleModel> = emptyList(),
    val sourceUrl: String? = null,
    val thumbnail: String? = null,
    val title: String? = null,
    val uri: String? = null,
    val vapidKey: String? = null,
    val version: String? = null,
    val software: String? = null,
)
