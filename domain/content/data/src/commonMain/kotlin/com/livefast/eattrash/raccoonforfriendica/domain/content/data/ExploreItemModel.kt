package com.livefast.eattrash.raccoonforfriendica.domain.content.data

sealed interface ExploreItemModel {
    val id: String

    data class Entry(
        val entry: TimelineEntryModel,
    ) : ExploreItemModel {
        override val id = entry.id
    }

    data class HashTag(
        val hashtag: TagModel,
    ) : ExploreItemModel {
        override val id = hashtag.name
    }

    data class Link(
        val link: LinkModel,
    ) : ExploreItemModel {
        override val id = link.url
    }

    data class User(
        val user: UserModel,
    ) : ExploreItemModel {
        override val id = user.id
    }
}

val ExploreItemModel.isNsfw: Boolean
    get() =
        when (this) {
            is ExploreItemModel.Entry -> entry.isNsfw
            else -> true
        }
