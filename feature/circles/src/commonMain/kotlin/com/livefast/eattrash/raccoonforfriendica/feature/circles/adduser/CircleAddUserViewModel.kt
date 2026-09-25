package com.livefast.eattrash.raccoonforfriendica.feature.circles.adduser

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.UserPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.UserPaginationSpecification
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ViewModelAssistedFactoryKey
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@AssistedInject
@OptIn(FlowPreview::class)
class CircleAddUserViewModel(
    @Assisted private val args: CircleAddUserViewModelArgs,
    private val searchPaginationManager: UserPaginationManager,
) : ViewModel(),
    MviModelDelegate<CircleAddUserMviModel.Intent, CircleAddUserMviModel.State, CircleAddUserMviModel.Effect>
    by DefaultMviModelDelegate(initialState = CircleAddUserMviModel.State()),
    CircleAddUserMviModel {

    init {
        viewModelScope.launch {
            uiState
                .map { it.query }
                .distinctUntilChanged()
                .drop(1)
                .debounce(750.milliseconds)
                .onEach { query ->
                    refreshSearchUsers(query)
                }.launchIn(this)
        }
    }

    override fun reduce(intent: CircleAddUserMviModel.Intent) {
        when (intent) {
            is CircleAddUserMviModel.Intent.SetQuery ->
                viewModelScope.launch {
                    updateState { it.copy(query = intent.text) }
                }

            CircleAddUserMviModel.Intent.LoadNextPage ->
                viewModelScope.launch {
                    loadNextPageSearchUsers()
                }
        }
    }

    private suspend fun refreshSearchUsers(query: String) {
        searchPaginationManager.reset(
            UserPaginationSpecification.SearchFollowing(
                query = query,
                withRelationship = false,
                excludeIds = args.userIdsToExclude,
            ),
        )
        updateState { it.copy(canFetchMore = searchPaginationManager.canFetchMore) }
        loadNextPageSearchUsers()
    }

    private suspend fun loadNextPageSearchUsers() {
        if (uiState.value.loading) {
            return
        }

        updateState { it.copy(loading = true) }
        val users = searchPaginationManager.loadNextPage()
        updateState {
            it.copy(
                users = users,
                canFetchMore = searchPaginationManager.canFetchMore,
                loading = false,
            )
        }
    }

    companion object {
        fun getExtras(args: CircleAddUserViewModelArgs) = CreationExtras {
            this[KEY_ARGS] = args
        }
    }
}

private val KEY_ARGS = CreationExtras.Key<CircleAddUserViewModelArgs>()

data class CircleAddUserViewModelArgs(val userIdsToExclude: List<String>)

@AssistedFactory
@ViewModelAssistedFactoryKey(CircleAddUserViewModel::class)
@ContributesIntoMap(AppScope::class)
fun interface CircleAddUserFactory : ViewModelAssistedFactory {
    override fun create(extras: CreationExtras): CircleAddUserViewModel {
        val args = extras[KEY_ARGS] ?: error("ViewModel creation args not found")
        return create(args)
    }
    fun create(@Assisted args: CircleAddUserViewModelArgs): CircleAddUserViewModel
}
