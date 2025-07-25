package com.livefast.eattrash.raccoonforfriendica.feature.circles.list

import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.core.utils.validation.ValidationError
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.CircleModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.CircleReplyPolicy
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.CircleType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel

data class CircleEditorData(
    val id: String? = null,
    val title: String = "",
    val titleError: ValidationError? = null,
    val replyPolicy: CircleReplyPolicy = CircleReplyPolicy.List,
    val exclusive: Boolean = false,
)

sealed interface CircleListItem {
    data class Header(val type: CircleType) : CircleListItem

    data class Circle(val circle: CircleModel) : CircleListItem
}

interface CirclesMviModel : MviModel<CirclesMviModel.Intent, CirclesMviModel.State, CirclesMviModel.Effect> {
    sealed interface Intent {
        data object Refresh : Intent

        data class OpenEditor(val circle: CircleModel? = null) : Intent

        data class UpdateEditorData(val data: CircleEditorData) : Intent

        data object DismissEditor : Intent

        data object SubmitEditorData : Intent

        data class Delete(val circleId: String) : Intent

        data class OpenDetail(val circle: CircleModel) : Intent
    }

    data class State(
        val initial: Boolean = true,
        val refreshing: Boolean = false,
        val loading: Boolean = false,
        val items: List<CircleListItem> = emptyList(),
        val editorData: CircleEditorData? = null,
        val hideNavigationBarWhileScrolling: Boolean = true,
        val operationInProgress: Boolean = false,
    )

    sealed interface Effect {
        data object Failure : Effect

        data class OpenUser(val user: UserModel) : Effect

        data class OpenCircle(val circle: CircleModel) : Effect
    }
}
