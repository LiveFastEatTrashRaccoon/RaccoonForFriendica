package com.livefast.eattrash.raccoonforfriendica.feature.circles.list

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.core.utils.validation.ValidationError
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.CircleModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.CircleReplyPolicy
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.isFriendica
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.CirclesRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.NodeInfoRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class CirclesViewModel(
    private val circlesRepository: CirclesRepository,
    private val nodeInfoRepository: NodeInfoRepository,
) : DefaultMviModel<CirclesMviModel.Intent, CirclesMviModel.State, CirclesMviModel.Effect>(
        initialState = CirclesMviModel.State(),
    ),
    CirclesMviModel {
    init {
        screenModelScope.launch {
            if (uiState.value.initial) {
                refresh(initial = true)
            }
        }
    }

    override fun reduce(intent: CirclesMviModel.Intent) {
        when (intent) {
            CirclesMviModel.Intent.Refresh ->
                screenModelScope.launch {
                    refresh()
                }

            is CirclesMviModel.Intent.OpenEditor ->
                screenModelScope.launch {
                    val editorData =
                        CircleEditorData(
                            id = intent.circle?.id,
                            title = intent.circle?.name.orEmpty(),
                            exclusive = intent.circle?.exclusive == true,
                            replyPolicy = intent.circle?.replyPolicy ?: CircleReplyPolicy.List,
                        )
                    updateState { it.copy(editorData = editorData) }
                }

            is CirclesMviModel.Intent.UpdateEditorData ->
                screenModelScope.launch {
                    updateState { it.copy(editorData = intent.data) }
                }

            CirclesMviModel.Intent.DismissEditor ->
                screenModelScope.launch {
                    updateState { it.copy(editorData = null) }
                }

            CirclesMviModel.Intent.SubmitEditorData -> submitEditorData()
            is CirclesMviModel.Intent.Delete -> delete(intent.circleId)
        }
    }

    private suspend fun refresh(initial: Boolean = false) {
        updateState {
            it.copy(initial = initial, refreshing = !initial)
        }
        coroutineScope {
            val circles = async { circlesRepository.getAll() }.await()
            val friendicaCircles = async { circlesRepository.getFriendicaCircles() }.await()
            val isFriendica = async { nodeInfoRepository.getInfo()?.isFriendica == true }.await()
            val items =
                circles.map { circle ->
                    // on Mastodon, all lists can be edited; on Friendica ony the user-created ones
                    val canBeEdited = !isFriendica || friendicaCircles.any { c -> c.id == circle.id }
                    circle.copy(editable = canBeEdited)
                }

            updateState {
                it.copy(
                    initial = false,
                    refreshing = false,
                    items = items,
                )
            }
        }
    }

    private suspend fun removeItemFromState(id: String) {
        updateState {
            it.copy(
                items = it.items.filter { item -> item.id != id },
            )
        }
    }

    private suspend fun updateItemInState(
        id: String,
        block: (CircleModel) -> CircleModel,
    ) {
        updateState {
            it.copy(
                items =
                    it.items.map { item ->
                        if (item.id == id) {
                            item.let(block)
                        } else {
                            item
                        }
                    },
            )
        }
    }

    private suspend fun insertItemInState(item: CircleModel) {
        updateState {
            it.copy(items = it.items + item)
        }
    }

    private fun delete(id: String) {
        screenModelScope.launch {
            val success = circlesRepository.delete(id)
            if (success) {
                removeItemFromState(id)
            } else {
                emitEffect(CirclesMviModel.Effect.Failure)
            }
        }
    }

    private fun submitEditorData() {
        val data = uiState.value.editorData ?: return

        screenModelScope.launch {
            val title = data.title
            if (title.isEmpty()) {
                updateState {
                    it.copy(editorData = it.editorData?.copy(titleError = ValidationError.MissingField))
                }
                return@launch
            }

            updateState {
                it.copy(editorData = it.editorData?.copy(titleError = null))
            }

            if (data.id != null) {
                val item =
                    circlesRepository.update(
                        id = data.id,
                        title = title,
                        exclusive = data.exclusive,
                        replyPolicy = data.replyPolicy,
                    )
                if (item != null) {
                    updateItemInState(data.id) { item }
                    updateState { it.copy(editorData = null) }
                } else {
                    emitEffect(CirclesMviModel.Effect.Failure)
                }
            } else {
                val item =
                    circlesRepository.create(
                        title = title,
                        exclusive = data.exclusive,
                        replyPolicy = data.replyPolicy,
                    )
                if (item != null) {
                    insertItemInState(item)
                    updateState { it.copy(editorData = null) }
                } else {
                    emitEffect(CirclesMviModel.Effect.Failure)
                }
            }
        }
    }
}
