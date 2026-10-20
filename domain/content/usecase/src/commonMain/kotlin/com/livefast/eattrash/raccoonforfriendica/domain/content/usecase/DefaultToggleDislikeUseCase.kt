package com.livefast.eattrash.raccoonforfriendica.domain.content.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.PostRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@ContributesBinding(AppScope::class)
@Inject
class DefaultToggleDislikeUseCase(private val postRepository: PostRepository) : ToggleDislikeUseCase {
    override suspend fun invoke(post: PostModel): PostModel? {
        val newValue = !post.disliked
        val postId = post.id
        val shouldRemoveFavorite = newValue && post.favorite
        val referencePost =
            if (shouldRemoveFavorite) {
                postRepository.unfavorite(postId)
            } else {
                post
            }
        val success =
            when {
                referencePost == null -> false
                newValue -> postRepository.dislike(postId)
                else -> postRepository.undislike(postId)
            }
        return if (success) {
            if (newValue) {
                post.copy(
                    disliked = true,
                    dislikesCount = post.dislikesCount + 1,
                    favoriteCount = (referencePost ?: post).favoriteCount,
                    favorite = (referencePost ?: post).favorite,
                )
            } else {
                post.copy(
                    disliked = false,
                    dislikesCount = (post.dislikesCount - 1).coerceAtLeast(0),
                )
            }
        } else {
            null
        }
    }
}
