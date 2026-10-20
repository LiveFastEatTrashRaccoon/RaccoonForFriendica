package com.livefast.eattrash.raccoonforfriendica.domain.content.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.PostRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@ContributesBinding(AppScope::class)
@Inject
class DefaultToggleFavoriteUseCase(private val postRepository: PostRepository) : ToggleFavoriteUseCase {
    override suspend fun invoke(post: PostModel): PostModel? {
        val newValue = !post.favorite
        val postId = post.id
        val shouldRemoveDislike = newValue && post.disliked
        val dislikeRemoved =
            if (shouldRemoveDislike) {
                postRepository.undislike(postId)
            } else {
                false
            }
        val success = !shouldRemoveDislike || dislikeRemoved
        return if (success) {
            if (newValue) {
                postRepository.favorite(postId)
            } else {
                postRepository.unfavorite(postId)
            }?.let { referencePost ->
                post.copy(
                    disliked = false,
                    dislikesCount =
                    if (dislikeRemoved) {
                        (post.dislikesCount - 1).coerceAtLeast(0)
                    } else {
                        post.dislikesCount
                    },
                    favorite = referencePost.favorite,
                    favoriteCount = referencePost.favoriteCount,
                )
            }
        } else {
            null
        }
    }
}
