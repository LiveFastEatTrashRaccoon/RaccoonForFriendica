package com.livefast.eattrash.raccoonforfriendica.feature.circles.manage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.CirclesRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.cache.LocalItemCache
import com.livefast.eattrash.raccoonforfriendica.feature.circles.manage.ManageUserCirclesViewModel.Companion.KEY_ARGS
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
    @Assisted args: ManageUserCirclesViewModelArgs,
    private val circlesRepository: CirclesRepository,
    private val userCache: LocalItemCache<UserModel>,
    private val userRepository: UserRepository,
) : ViewModel(),
    MviModelDelegate<
        ManageUserCirclesMviModel.Intent,
        ManageUserCirclesMviModel.State,
        ManageUserCirclesMviModel.Effect,
        >
    by DefaultMviModelDelegate(initialState = ManageUserCirclesMviModel.State()),
    ManageUserCirclesMviModel {

    private val userId = args.userId

    init {
        viewModelScope.launch {
            if (uiState.value.initial) {
                val user = userCache.get(userId)
                updateState { it.copy(user = user) }
                refresh(initial = true)
            }
        }
    }

    override fun reduce(intent: ManageUserCirclesMviModel.Intent) {
        when (intent) {
            ManageUserCirclesMviModel.Intent.Refresh -> viewModelScope.launch {
                refresh()
            }

            is ManageUserCirclesMviModel.Intent.Add -> addUserToCircle(intent.circleId)
            is ManageUserCirclesMviModel.Intent.Remove -> removeUserFrom(intent.circleId)
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

    private suspend fun updateItemInState(id: String, block: (CircleBelongingUiModel) -> CircleBelongingUiModel) {
        updateState {
            it.copy(
                items =
                it.items.map { item ->
                    if (item.circle.id == id) {
                        item.let(block)
                    } else {
                        item
                    }
                },
            )
        }
    }

    private fun addUserToCircle(circleId: String) {
        viewModelScope.launch {
            updateItemInState(id = circleId) {
                it.copy(pending = true)
            }
            val success = circlesRepository.addMembers(
                id = circleId,
                userIds = listOf(userId),
            )
            if (success) {
                updateItemInState(id = circleId) {
                    it.copy(
                        belonging = true,
                        pending = false,
                    )
                }
            } else {
                updateItemInState(id = circleId) {
                    it.copy(pending = false)
                }
                emitEffect(ManageUserCirclesMviModel.Effect.Error)
            }
        }
    }

    private fun removeUserFrom(circleId: String) {
        viewModelScope.launch {
            updateItemInState(id = circleId) {
                it.copy(pending = true)
            }
            val success = circlesRepository.removeMembers(
                id = circleId,
                userIds = listOf(userId),
            )
            if (success) {
                updateItemInState(id = circleId) {
                    it.copy(
                        belonging = false,
                        pending = false,
                    )
                }
            } else {
                updateItemInState(id = circleId) {
                    it.copy(pending = false)
                }
                emitEffect(ManageUserCirclesMviModel.Effect.Error)
            }
        }
    }

    companion object {
        val KEY_ARGS = CreationExtras.Key<ManageUserCirclesViewModelArgs>()
    }
}

data class ManageUserCirclesViewModelArgs(val userId: String)

@AssistedFactory
@ViewModelAssistedFactoryKey(ManageUserCirclesViewModel::class)
@ContributesIntoMap(AppScope::class)
fun interface ManageUserCirclesViewModelFactory : ViewModelAssistedFactory {
    override fun create(extras: CreationExtras): ManageUserCirclesViewModel =
        create(extras[KEY_ARGS] ?: error("ViewModel creation args not found"))

    fun create(@Assisted args: ManageUserCirclesViewModelArgs): ManageUserCirclesViewModel
}
