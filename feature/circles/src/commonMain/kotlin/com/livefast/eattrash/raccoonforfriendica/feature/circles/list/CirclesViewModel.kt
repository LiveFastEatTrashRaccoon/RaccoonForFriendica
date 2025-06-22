package com.livefast.eattrash.raccoonforfriendica.feature.circles.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.core.utils.validation.ValidationError
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.CircleModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.CircleReplyPolicy
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.CircleType
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.CirclesRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class CirclesViewModel(
    private val circlesRepository: CirclesRepository,
    private val settingsRepository: SettingsRepository,
    private val userRepository: UserRepository,
) : ViewModel(),
    MviModelDelegate<CirclesMviModel.Intent, CirclesMviModel.State, CirclesMviModel.Effect>
    by DefaultMviModelDelegate(initialState = CirclesMviModel.State()),
    CirclesMviModel {
    init {
        viewModelScope.launch {
            settingsRepository.current
                .onEach { settings ->
                    updateState {
                        it.copy(
                            hideNavigationBarWhileScrolling =
                            settings?.hideNavigationBarWhileScrolling ?: true,
                        )
                    }
                }.launchIn(this)

            if (uiState.value.initial) {
                refresh(initial = true)
            }
        }
    }

    override fun reduce(intent: CirclesMviModel.Intent) {
        when (intent) {
            CirclesMviModel.Intent.Refresh ->
                viewModelScope.launch {
                    refresh()
                }

            is CirclesMviModel.Intent.OpenEditor ->
                viewModelScope.launch {
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
                viewModelScope.launch {
                    updateState { it.copy(editorData = intent.data) }
                }

            CirclesMviModel.Intent.DismissEditor ->
                viewModelScope.launch {
                    updateState { it.copy(editorData = null) }
                }

            CirclesMviModel.Intent.SubmitEditorData -> submitEditorData()
            is CirclesMviModel.Intent.Delete -> delete(intent.circleId)
            is CirclesMviModel.Intent.OpenDetail -> handleOpenDetail(intent.circle)
        }
    }

    private suspend fun refresh(initial: Boolean = false) {
        updateState {
            it.copy(initial = initial, refreshing = !initial)
        }
        val groupedCircles = circlesRepository.getAll().orEmpty().groupBy { it.type }
        val items = groupedCircles.toListItems()

        updateState {
            it.copy(
                initial = false,
                refreshing = false,
                items = items,
            )
        }
    }

    private fun Map<CircleType, List<CircleModel>>.toListItems() = buildList {
        val types =
            listOf(
                CircleType.UserDefined,
                CircleType.Predefined,
                CircleType.Group,
                CircleType.Other,
            )
        for (type in types) {
            val items = generateSection(type)
            if (items.isNotEmpty()) {
                addAll(items)
            }
        }
    }

    private fun Map<CircleType, List<CircleModel>>.generateSection(type: CircleType) = buildList<CircleListItem> {
        val items =
            get(type)
                .orEmpty()
                .sortedBy { it.name }
                .map { CircleListItem.Circle(circle = it) }
        if (items.isNotEmpty()) {
            this += CircleListItem.Header(type = type)
            this += get(type).orEmpty().map { CircleListItem.Circle(circle = it) }
        }
    }

    private suspend fun removeItemFromState(id: String) {
        updateState {
            val circles =
                it.items
                    .mapNotNull { listItem ->
                        if (listItem is CircleListItem.Circle && listItem.circle.id != id) {
                            listItem.circle
                        } else {
                            null
                        }
                    }
            val groupedCircles = circles.groupBy { circle -> circle.type }
            it.copy(items = groupedCircles.toListItems())
        }
    }

    private suspend fun updateItemInState(id: String, block: (CircleModel) -> CircleModel) {
        updateState {
            it.copy(
                items =
                it.items.map { item ->
                    if (item is CircleListItem.Circle && item.circle.id == id) {
                        item.copy(circle = item.circle.let(block))
                    } else {
                        item
                    }
                },
            )
        }
    }

    private suspend fun insertItemInState(circle: CircleModel) {
        updateState {
            val circles =
                listOf(circle) +
                    it.items
                        .mapNotNull { listItem ->
                            if (listItem is CircleListItem.Circle) {
                                listItem.circle
                            } else {
                                null
                            }
                        }
            val groupedCircles = circles.groupBy { circle -> circle.type }
            it.copy(items = groupedCircles.toListItems())
        }
    }

    private fun delete(id: String) {
        viewModelScope.launch {
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

        viewModelScope.launch {
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

    private fun handleOpenDetail(circle: CircleModel) {
        viewModelScope.launch {
            if (circle.type == CircleType.Group) {
                updateState { it.copy(operationInProgress = true) }
                val user =
                    userRepository
                        .search(
                            query = circle.name,
                            following = true,
                            offset = 0,
                        )?.firstOrNull()
                updateState { it.copy(operationInProgress = false) }
                if (user != null) {
                    emitEffect(CirclesMviModel.Effect.OpenUser(user))
                }
            } else {
                emitEffect(CirclesMviModel.Effect.OpenCircle(circle))
            }
        }
    }
}
