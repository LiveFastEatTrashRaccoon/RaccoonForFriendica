package com.livefast.eattrash.raccoonforfriendica.domain.content.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TimelineEntryRepository

internal class DefaultToggleEntryFavoriteUseCase(private val entryRepository: TimelineEntryRepository) :
    ToggleEntryFavoriteUseCase {
    override suspend fun invoke(entry: TimelineEntryModel): TimelineEntryModel? {
        val newValue = !entry.favorite
        val entryId = entry.id
        val shouldRemoveDislike = newValue && entry.disliked
        val dislikeRemoved =
            if (shouldRemoveDislike) {
                entryRepository.undislike(entryId)
            } else {
                false
            }
        val success = !shouldRemoveDislike || dislikeRemoved
        return if (success) {
            if (newValue) {
                entryRepository.favorite(entryId)
            } else {
                entryRepository.unfavorite(entryId)
            }?.let { referenceEntry ->
                entry.copy(
                    disliked = false,
                    dislikesCount =
                    if (dislikeRemoved) {
                        (entry.dislikesCount - 1).coerceAtLeast(0)
                    } else {
                        entry.dislikesCount
                    },
                    favorite = referenceEntry.favorite,
                    favoriteCount = referenceEntry.favoriteCount,
                )
            }
        } else {
            null
        }
    }
}
