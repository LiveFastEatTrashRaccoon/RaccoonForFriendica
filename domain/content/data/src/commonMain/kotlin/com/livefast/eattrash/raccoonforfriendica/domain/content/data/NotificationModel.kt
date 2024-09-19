package com.livefast.eattrash.raccoonforfriendica.domain.content.data

data class NotificationModel(
    val entry: TimelineEntryModel? = null,
    val id: String,
    val read: Boolean = true,
    val type: NotificationType = NotificationType.Unknown,
    val user: UserModel? = null,
)

val NotificationModel.urlsForPreload: List<String>
    get() =
        buildList {
            entry
                ?.attachments
                ?.mapNotNull { attachment ->
                    if (attachment.type != MediaType.Image) {
                        null
                    } else {
                        attachment.url.takeUnless { it.isNotBlank() }
                    }
                }?.also { urls ->
                    addAll(urls)
                }
            entry
                ?.creator
                ?.avatar
                ?.takeUnless { it.isNotBlank() }
                ?.also { add(it) }
            entry
                ?.card
                ?.image
                ?.takeUnless { it.isNotBlank() }
                ?.also { add(it) }

            user?.header?.takeUnless { it.isNotBlank() }?.also { add(it) }
            user?.avatar?.takeUnless { it.isNotBlank() }?.also { add(it) }
        }
