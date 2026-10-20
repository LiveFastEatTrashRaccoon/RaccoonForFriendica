package com.livefast.eattrash.raccoonforfriendica.domain.content.data

sealed interface ExploreItemModel {
    val id: String

    data class Post(val post: PostModel) : ExploreItemModel {
        override val id = post.id
    }

    data class HashTag(val hashtag: TagModel) : ExploreItemModel {
        override val id = hashtag.name
    }

    data class Link(val link: LinkModel) : ExploreItemModel {
        override val id = link.url
    }

    data class User(val user: UserModel) : ExploreItemModel {
        override val id = user.id
    }
}

val ExploreItemModel.isNsfw: Boolean
    get() =
        when (this) {
            is ExploreItemModel.Post -> post.isNsfw
            else -> true
        }
