package com.livefast.eattrash.raccoonforfriendica.feature.circles.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.CircleModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.CircleType
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.CirclesRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@ContributesIntoMap(
    scope = AppScope::class,
    binding = binding<@ViewModelKey ViewModel>(),
)
@Inject
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

            is CirclesMviModel.Intent.Delete -> delete(intent.circleId)
            is CirclesMviModel.Intent.OpenDetail -> handleOpenDetail(intent.circle)
            is CirclesMviModel.Intent.Upsert -> viewModelScope.launch {
                val new = uiState.value.items.none { it is CircleListItem.Circle && it.circle.id == intent.circle.id }
                if (new) {
                    insertItemInState(intent.circle)
                } else {
                    updateItemInState(intent.circle.id) { intent.circle }
                }
            }
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
        val items = get(type).orEmpty()
        if (items.isNotEmpty()) {
            this += CircleListItem.Header(type = type)
            this += items.map { CircleListItem.Circle(circle = it) }.sortedBy { it.circle.name }
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
