package com.livefast.eattrash.raccoonforfriendica.feature.circles.manage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.updateMatching
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.CirclesRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.cache.LocalItemCache
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ViewModelAssistedFactoryKey
import kotlinx.coroutines.launch

@AssistedInject
class ManageUserCirclesViewModel(
    @Assisted private val userId: String,
    private val circlesRepository: CirclesRepository,
    private val userCache: LocalItemCache<UserModel>,
    private val userRepository: UserRepository,
) : ViewModel(),
    MviDelegate<
        ManageUserCirclesMvi.Intent,
        ManageUserCirclesMvi.State,
        ManageUserCirclesMvi.Effect,
        >
    by DefaultMviDelegate(initialState = ManageUserCirclesMvi.State()),
    ManageUserCirclesMvi {

    init {
        viewModelScope.launch {
            if (uiState.value.initial) {
                val user = userCache.get(userId)
                updateState { it.copy(user = user) }
                refresh(initial = true)
            }
        }
    }

    override fun reduce(intent: ManageUserCirclesMvi.Intent) {
        when (intent) {
            ManageUserCirclesMvi.Intent.Refresh -> handleRefresh()
            is ManageUserCirclesMvi.Intent.Add -> handleAdd(intent.circleId)
            is ManageUserCirclesMvi.Intent.Remove -> handleRemove(intent.circleId)
        }
    }

    private fun handleRefresh() {
        viewModelScope.launch {
            refresh()
        }
    }

    private suspend fun refresh(initial: Boolean = false) {
        updateState {
            it.copy(initial = initial, refreshing = !initial)
        }
        val circlesWithUser = userRepository.getListsContaining(userId).orEmpty()
        val containingIds = circlesWithUser.map { it.id }
        val circlesWithoutUser = circlesRepository.getAll().orEmpty().filter { it.id !in containingIds }
        val items = buildList {
            addAll(
                circlesWithUser.map { circle ->
                    CircleBelongingUiModel(
                        circle = circle,
                        belonging = true,
                    )
                },
            )
            addAll(
                circlesWithoutUser.map { circle ->
                    CircleBelongingUiModel(
                        circle = circle,
                        belonging = false,
                    )
                },
            )
        }.sortedBy { element -> element.circle.name }
        updateState {
            it.copy(
                initial = false,
                refreshing = false,
                items = items,
            )
        }
    }

    private fun handleAdd(circleId: String) {
        viewModelScope.launch {
            updateState { state ->
                state.copy(
                    items = state.items.updateMatching(matcher = { it.circle.id == circleId }) {
                        it.copy(pending = true)
                    },
                )
            }
            val success = circlesRepository.addMembers(
                id = circleId,
                userIds = listOf(userId),
            )
            if (success) {
                updateState { state ->
                    state.copy(
                        items = state.items.updateMatching(matcher = { it.circle.id == circleId }) {
                            it.copy(
                                belonging = true,
                                pending = false,
                            )
                        },
                    )
                }
            } else {
                updateState { state ->
                    state.copy(
                        items = state.items.updateMatching(matcher = { it.circle.id == circleId }) {
                            it.copy(pending = false)
                        },
                    )
                }
                emitEffect(ManageUserCirclesMvi.Effect.Error)
            }
        }
    }

    private fun handleRemove(circleId: String) {
        viewModelScope.launch {
            updateState { state ->
                state.copy(
                    items = state.items.updateMatching(matcher = { it.circle.id == circleId }) {
                        it.copy(pending = true)
                    },
                )
            }
            val success = circlesRepository.removeMembers(
                id = circleId,
                userIds = listOf(userId),
            )
            if (success) {
                updateState { state ->
                    state.copy(
                        items = state.items.updateMatching(matcher = { it.circle.id == circleId }) {
                            it.copy(
                                belonging = false,
                                pending = false,
                            )
                        },
                    )
                }
            } else {
                updateState { state ->
                    state.copy(
                        items = state.items.updateMatching(matcher = { it.circle.id == circleId }) {
                            it.copy(pending = false)
                        },
                    )
                }
                emitEffect(ManageUserCirclesMvi.Effect.Error)
            }
        }
    }

    companion object {
        fun getExtras(userId: String) = CreationExtras {
            this[KEY_USER_ID] = userId
        }
    }
}

private val KEY_USER_ID = CreationExtras.Key<String>()

@AssistedFactory
@ViewModelAssistedFactoryKey(ManageUserCirclesViewModel::class)
@ContributesIntoMap(AppScope::class)
fun interface ManageUserCirclesViewModelFactory : ViewModelAssistedFactory {
    override fun create(extras: CreationExtras): ManageUserCirclesViewModel =
        create(extras[KEY_USER_ID] ?: error("userId argument not found"))

    fun create(@Assisted userId: String): ManageUserCirclesViewModel
}
