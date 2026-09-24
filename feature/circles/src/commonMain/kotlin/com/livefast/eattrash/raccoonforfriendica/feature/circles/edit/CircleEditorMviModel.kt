package com.livefast.eattrash.raccoonforfriendica.feature.circles.edit

import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.core.utils.validation.ValidationError
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.CircleModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.CircleReplyPolicy

data class CircleEditorData(
    val id: String? = null,
    val title: String = "",
    val titleError: ValidationError? = null,
    val replyPolicy: CircleReplyPolicy = CircleReplyPolicy.List,
    val exclusive: Boolean = false,
)

interface CircleEditorMviModel :
    MviModel<CircleEditorMviModel.Intent, CircleEditorMviModel.State, CircleEditorMviModel.Effect> {
    sealed interface Intent {
        data class UpdateData(val data: CircleEditorData) : Intent

        data object Submit : Intent
    }

    data class State(val data: CircleEditorData = CircleEditorData())

    sealed interface Effect {
        data class Success(val circle: CircleModel) : Effect

        data object Failure : Effect
    }
}
