package com.livefast.eattrash.raccoonforfriendica.feature.circles.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.removeMatching
import com.livefast.eattrash.raccoonforfriendica.core.architecture.updateMatching
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
    MviDelegate<CirclesMvi.Intent, CirclesMvi.State, CirclesMvi.Effect>
    by DefaultMviDelegate(initialState = CirclesMvi.State()),
    CirclesMvi {

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

    override fun reduce(intent: CirclesMvi.Intent) {
        when (intent) {
            CirclesMvi.Intent.Refresh -> handleRefresh()
            is CirclesMvi.Intent.Delete -> handleDelete(intent.circleId)
            is CirclesMvi.Intent.OpenDetail -> handleOpenDetail(intent.circle)
            is CirclesMvi.Intent.Upsert -> handleUpsert(intent.circle)
        }
    }

    private fun handleRefresh() {
        viewModelScope.launch {
            refresh()
        }
    }

    private fun handleUpsert(circle: CircleModel) {
        viewModelScope.launch {
            val new = uiState.value.items.none { it is CircleListItem.Circle && it.circle.id == circle.id }
            if (new) {
                updateState { state ->
                    val circles = state.items.mapNotNull { (it as? CircleListItem.Circle)?.circle }
                    val newCircles = listOf(circle) + circles
                    val groupedCircles = newCircles.groupBy { c -> c.type }
                    state.copy(items = groupedCircles.toListItems())
                }
            } else {
                updateState { state ->
                    state.copy(
                        items = state.items.updateMatching(
                            matcher = { (it as? CircleListItem.Circle)?.circle?.id == circle.id },
                        ) {
                            val item = it as CircleListItem.Circle
                            item.copy(circle = circle)
                        },
                    )
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

    private fun handleDelete(id: String) {
        viewModelScope.launch {
            val success = circlesRepository.delete(id)
            if (success) {
                updateState { state ->
                    val circles = state.items.mapNotNull { (it as? CircleListItem.Circle)?.circle }
                    val newCircles = circles.removeMatching { it.id == id }
                    val groupedCircles = newCircles.groupBy { c -> c.type }
                    state.copy(items = groupedCircles.toListItems())
                }
            } else {
                emitEffect(CirclesMvi.Effect.Failure)
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
                    emitEffect(CirclesMvi.Effect.OpenUser(user))
                }
            } else {
                emitEffect(CirclesMvi.Effect.OpenCircle(circle))
            }
        }
    }
}
