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
                .map { it.userSearchQuery }
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
            SelectUserMviModel.Intent.UserSearchClear ->
                viewModelScope.launch {
                    updateState { it.copy(userSearchUsers = emptyList()) }
                }

            SelectUserMviModel.Intent.UserSearchLoadNextPage ->
                viewModelScope.launch {
                    loadNextPageUsers()
                }

            is SelectUserMviModel.Intent.UserSearchSetQuery ->
                viewModelScope.launch {
                    updateState { it.copy(userSearchQuery = intent.query) }
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
        updateState { it.copy(userSearchCanFetchMore = userPaginationManager.canFetchMore) }
        loadNextPageUsers()
    }

    private suspend fun loadNextPageUsers() {
        if (uiState.value.userSearchLoading) {
            return
        }

        updateState { it.copy(userSearchLoading = true) }
        val users = userPaginationManager.loadNextPage()
        updateState {
            it.copy(
                userSearchUsers = users,
                userSearchCanFetchMore = userPaginationManager.canFetchMore,
                userSearchLoading = false,
            )
        }
    }
}
