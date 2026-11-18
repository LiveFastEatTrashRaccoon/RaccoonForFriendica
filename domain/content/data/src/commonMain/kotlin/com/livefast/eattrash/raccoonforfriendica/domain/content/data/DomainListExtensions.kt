package com.livefast.eattrash.raccoonforfriendica.domain.content.data

import kotlin.jvm.JvmName

/**
 * Returns a new list where any [PostModel] matching [id] (either directly or via its reblog)
 * is updated by invoking [block].
 */
@JvmName("updateMatchingPost")
fun List<PostModel>.updateMatching(id: String, block: (PostModel) -> PostModel): List<PostModel> =
    map { it.updateMatching(id, block) }

/**
 * Returns a new list excluding any [PostModel] matching [id] (either directly or via its reblog).
 */
@JvmName("removeMatchingPost")
fun List<PostModel>.removeMatching(id: String): List<PostModel> = filterNot { it.matches(id) }

/**
 * Returns a new list where any [ExploreItemModel.Post] matching [id] is updated by invoking [block].
 */
@JvmName("updateMatchingExploreItem")
fun List<ExploreItemModel>.updateMatching(id: String, block: (PostModel) -> PostModel): List<ExploreItemModel> =
    map { item ->
        if (item is ExploreItemModel.Post) {
            item.copy(post = item.post.updateMatching(id, block))
        } else {
            item
        }
    }

/**
 * Returns a new list excluding any [ExploreItemModel.Post] matching [id].
 */
@JvmName("removeMatchingExploreItem")
fun List<ExploreItemModel>.removeMatching(id: String): List<ExploreItemModel> = filterNot { item ->
    (item is ExploreItemModel.Post) && item.post.matches(id)
}

/**
 * Returns a new list of lists where any [PostModel] matching [id] in the list at [index]
 * is updated by invoking [block].
 */
@JvmName("updateMatchingPostList")
fun List<List<PostModel>>.updateMatching(
    index: Int,
    id: String,
    block: (PostModel) -> PostModel,
): List<List<PostModel>> = mapIndexed { idx, list ->
    if (idx == index) {
        list.updateMatching(id, block)
    } else {
        list
    }
}

/**
 * Returns a new list of lists where any [PostModel] matching [id] in the list at [index] is removed.
 */
@JvmName("removeMatchingPostList")
fun List<List<PostModel>>.removeMatching(index: Int, id: String): List<List<PostModel>> = mapIndexed { idx, list ->
    if (idx == index) {
        list.removeMatching(id)
    } else {
        list
    }
}

private fun PostModel.matches(id: String): Boolean = (this.id == id) || (this.reblog?.id == id)

private fun PostModel.updateMatching(id: String, block: (PostModel) -> PostModel): PostModel = when {
    this.id == id -> block(this)
    this.reblog?.id == id -> copy(reblog = this.reblog.let(block))
    else -> this
}
