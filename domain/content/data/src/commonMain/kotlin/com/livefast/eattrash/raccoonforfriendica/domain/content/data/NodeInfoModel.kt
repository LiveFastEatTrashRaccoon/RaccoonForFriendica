package com.livefast.eattrash.raccoonforfriendica.domain.content.data

data class NodeInfoModel(
    val uri: String? = null,
    val domain: String? = null,
    val title: String? = null,
    val version: String? = null,
    val sourceUrl: String? = null,
    val description: String? = null,
    val activeUsers: Int? = null,
    val thumbnail: String? = null,
    val languages: List<String> = emptyList(),
    val rules: List<RuleModel> = emptyList(),
    val contact: UserModel? = null,
    val characterLimit: Int? = null,
    val attachmentLimit: Int? = null,
)

private val FRIENDICA_REGEX =
    Regex("\\(compatible; Friendica (?<version>[a-zA-Z0-9.-_]*)\\)")

val NodeInfoModel.isFriendica: Boolean
    get() =
        version
            .orEmpty()
            .contains(FRIENDICA_REGEX)
