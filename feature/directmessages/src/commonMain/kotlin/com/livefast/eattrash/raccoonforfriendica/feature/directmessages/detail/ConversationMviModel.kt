package com.livefast.eattrash.raccoonforfriendica.feature.directmessages.detail

import androidx.compose.ui.text.input.TextFieldValue
import cafe.adriel.voyager.core.model.ScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.DirectMessageModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel

interface ConversationMviModel :
    ScreenModel,
    MviModel<ConversationMviModel.Intent, ConversationMviModel.State, ConversationMviModel.Effect> {
    sealed interface Intent {
        data object Refresh : Intent

        data object LoadNextPage : Intent

        data class SetNewMessageValue(
            val value: TextFieldValue,
        ) : Intent

        data object Submit : Intent
    }

    data class State(
        val otherUser: UserModel? = null,
        val currentUser: UserModel? = null,
        val refreshing: Boolean = false,
        val loading: Boolean = false,
        val sendInProgress: Boolean = false,
        val initial: Boolean = true,
        val canFetchMore: Boolean = true,
        val autoloadImages: Boolean = true,
        val items: List<DirectMessageModel> = emptyList(),
        val newMessageValue: TextFieldValue = TextFieldValue(),
    )

    sealed interface Effect {
        data object BackToTop : Effect

        data object Failure : Effect

        data object FollowUserRequired : Effect
    }
}
