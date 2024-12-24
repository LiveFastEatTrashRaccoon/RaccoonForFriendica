package com.livefast.eattrash.raccoonforfriendica.domain.content.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TimelineEntryRepository

internal class DefaultToggleEntryDislikeUseCase(
    private val entryRepository: TimelineEntryRepository,
) : ToggleEntryDislikeUseCase {
    override suspend fun invoke(entry: TimelineEntryModel): TimelineEntryModel? {
        val newValue = !entry.disliked
        val entryId = entry.id
        val shouldRemoveFavorite = newValue && entry.favorite
        val referenceEntry =
            if (shouldRemoveFavorite) {
                entryRepository.unfavorite(entryId) ?: entry
            } else {
                entry
            }
        val success =
            if (newValue) {
                entryRepository.dislike(entryId)
            } else {
                entryRepository.undislike(entryId)
            }
        return if (success) {
            if (newValue) {
                entry.copy(
                    disliked = true,
                    dislikesCount = entry.dislikesCount + 1,
                    favoriteCount = referenceEntry.favoriteCount,
                    favorite = referenceEntry.favorite,
                )
            } else {
                entry.copy(
                    disliked = false,
                    dislikesCount = (entry.dislikesCount - 1).coerceAtLeast(0),
                )
            }
        } else {
            null
        }
    }
}
