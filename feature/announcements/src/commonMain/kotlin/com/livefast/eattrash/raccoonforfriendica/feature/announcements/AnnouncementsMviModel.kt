package com.livefast.eattrash.raccoonforfriendica.feature.announcements

import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AnnouncementModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.EmojiModel

interface AnnouncementsMviModel :
    MviModel<AnnouncementsMviModel.Intent, AnnouncementsMviModel.State, AnnouncementsMviModel.Effect> {
    sealed interface Intent {
        data object Refresh : Intent

        data class AddReaction(val id: String, val name: String) : Intent

        data class RemoveReaction(val id: String, val name: String) : Intent
    }

    data class State(
        val currentUserId: String? = null,
        val refreshing: Boolean = false,
        val initial: Boolean = true,
        val items: List<AnnouncementModel> = emptyList(),
        val autoloadImages: Boolean = true,
        val hideNavigationBarWhileScrolling: Boolean = true,
        val availableEmojis: List<EmojiModel> = emptyList(),
    )

    sealed interface Effect {
        data object BackToTop : Effect
    }
}
