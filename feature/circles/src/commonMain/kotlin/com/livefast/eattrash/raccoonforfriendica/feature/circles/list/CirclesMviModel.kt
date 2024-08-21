package com.livefast.eattrash.raccoonforfriendica.feature.circles.list

import cafe.adriel.voyager.core.model.ScreenModel
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

interface CirclesMviModel :
    ScreenModel,
    MviModel<CirclesMviModel.Intent, CirclesMviModel.State, CirclesMviModel.Effect> {
    sealed interface Intent {
        data object Refresh : Intent

        data class OpenEditor(
            val circle: CircleModel? = null,
        ) : Intent

        data class UpdateEditorData(
            val data: CircleEditorData,
        ) : Intent

        data object DismissEditor : Intent

        data object SubmitEditorData : Intent

        data class Delete(
            val circleId: String,
        ) : Intent
    }

    data class State(
        val initial: Boolean = true,
        val refreshing: Boolean = false,
        val loading: Boolean = false,
        val items: List<CircleModel> = emptyList(),
        val editorData: CircleEditorData? = null,
    )

    sealed interface Effect {
        data object Failure : Effect
    }
}
