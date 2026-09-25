package com.livefast.eattrash.raccoonforfriendica.feature.directmessages.selectuser

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.UserPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.UserPaginationSpecification
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
@ContributesIntoMap(
    scope = AppScope::class,
    binding = binding<@ViewModelKey ViewModel>(),
)
@Inject
class SelectUserViewModel(private val userPaginationManager: UserPaginationManager) :
    ViewModel(),
    MviModelDelegate<SelectUserMviModel.Intent, SelectUserMviModel.State, SelectUserMviModel.Effect>
    by DefaultMviModelDelegate(initialState = SelectUserMviModel.State()),
    SelectUserMviModel {

    init {
        viewModelScope.launch {
            uiState
                .map { it.query }
                .distinctUntilChanged()
                .drop(1)
                .debounce(750.milliseconds)
                .onEach { query ->
                    refreshUsers(query)
                }.launchIn(this)
        }
    }

    override fun reduce(intent: SelectUserMviModel.Intent) {
        when (intent) {
            SelectUserMviModel.Intent.Clear ->
                viewModelScope.launch {
                    updateState { it.copy(users = emptyList()) }
                }

            SelectUserMviModel.Intent.LoadNextPage ->
                viewModelScope.launch {
                    loadNextPageUsers()
                }

            is SelectUserMviModel.Intent.SetQuery ->
                viewModelScope.launch {
                    updateState { it.copy(query = intent.query) }
                }
        }
    }

    private suspend fun refreshUsers(query: String) {
        userPaginationManager.reset(
            UserPaginationSpecification.SearchFollowing(
                query = query,
                withRelationship = false,
            ),
        )
        updateState { it.copy(canFetchMore = userPaginationManager.canFetchMore) }
        loadNextPageUsers()
    }

    private suspend fun loadNextPageUsers() {
        if (uiState.value.loading) {
            return
        }

        updateState { it.copy(loading = true) }
        val users = userPaginationManager.loadNextPage()
        updateState {
            it.copy(
                users = users,
                canFetchMore = userPaginationManager.canFetchMore,
                loading = false,
            )
        }
    }
}
