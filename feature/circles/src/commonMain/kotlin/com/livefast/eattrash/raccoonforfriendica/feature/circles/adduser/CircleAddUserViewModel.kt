package com.livefast.eattrash.raccoonforfriendica.feature.circles.adduser

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviDelegate
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
    @Assisted private val userIdsToExclude: List<String>,
    private val searchPaginationManager: UserPaginationManager,
) : ViewModel(),
    MviDelegate<CircleAddUserMvi.Intent, CircleAddUserMvi.State, CircleAddUserMvi.Effect>
    by DefaultMviDelegate(initialState = CircleAddUserMvi.State()),
    CircleAddUserMvi {

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

    override fun reduce(intent: CircleAddUserMvi.Intent) {
        when (intent) {
            is CircleAddUserMvi.Intent.SetQuery -> handleSetQuery(intent.text)
            CircleAddUserMvi.Intent.LoadNextPage -> handleLoadNextPage()
        }
    }

    private fun handleSetQuery(query: String) {
        viewModelScope.launch {
            updateState { it.copy(query = query) }
        }
    }

    private fun handleLoadNextPage() {
        viewModelScope.launch {
            loadNextPageSearchUsers()
        }
    }

    private suspend fun refreshSearchUsers(query: String) {
        searchPaginationManager.reset(
            UserPaginationSpecification.SearchFollowing(
                query = query,
                withRelationship = false,
                excludeIds = userIdsToExclude,
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
        fun getExtras(userIdsToExclude: List<String>) = CreationExtras {
            this[KEY_USER_IDS_TO_EXCLUDE] = userIdsToExclude
        }
    }
}

private val KEY_USER_IDS_TO_EXCLUDE = CreationExtras.Key<List<String>>()

@AssistedFactory
@ViewModelAssistedFactoryKey(CircleAddUserViewModel::class)
@ContributesIntoMap(AppScope::class)
fun interface CircleAddUserFactory : ViewModelAssistedFactory {
    override fun create(extras: CreationExtras): CircleAddUserViewModel =
        create(extras[KEY_USER_IDS_TO_EXCLUDE] ?: error("userIdsToExclude argument not found"))

    fun create(@Assisted userIdsToExclude: List<String>): CircleAddUserViewModel
}
