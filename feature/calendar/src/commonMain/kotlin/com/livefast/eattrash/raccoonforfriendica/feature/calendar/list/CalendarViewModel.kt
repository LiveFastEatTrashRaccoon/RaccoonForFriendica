package com.livefast.eattrash.raccoonforfriendica.feature.calendar.list

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.extractDatePart
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.toEpochMillis
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.EventPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.EventsPaginationSpecification
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class CalendarViewModel(
    private val identityRepository: IdentityRepository,
    private val settingsRepository: SettingsRepository,
    private val paginationManager: EventPaginationManager,
) : DefaultMviModel<CalendarMviModel.Intent, CalendarMviModel.State, CalendarMviModel.Effect>(
    initialState = CalendarMviModel.State(),
),
    CalendarMviModel {
    init {
        screenModelScope.launch {
            identityRepository.currentUser
                .onEach {
                    refresh(initial = true)
                }.launchIn(this)

            settingsRepository.current
                .onEach { settings ->
                    updateState {
                        it.copy(
                            hideNavigationBarWhileScrolling =
                            settings?.hideNavigationBarWhileScrolling ?: true,
                        )
                    }
                }.launchIn(this)
        }
    }

    override fun reduce(intent: CalendarMviModel.Intent) {
        when (intent) {
            CalendarMviModel.Intent.LoadNextPage ->
                screenModelScope.launch {
                    loadNextPage()
                }

            CalendarMviModel.Intent.Refresh ->
                screenModelScope.launch {
                    refresh()
                }
        }
    }

    private suspend fun refresh(initial: Boolean = false) {
        updateState {
            it.copy(initial = initial, refreshing = !initial)
        }
        paginationManager.reset(EventsPaginationSpecification.All)
        loadNextPage()
    }

    private suspend fun loadNextPage() {
        check(!uiState.value.loading) { return }
        val wasRefreshing = uiState.value.refreshing
        updateState { it.copy(loading = true) }
        val events = paginationManager.loadNextPage()
        val eventsGrouped =
            events.groupBy {
                val timestamp = it.startTime.toEpochMillis()
                val (year, month) = timestamp.extractDatePart()
                CalendarItem.Header(year = year, month = month)
            }
        val keys =
            eventsGrouped.keys.sortedByDescending {
                val monthPart = "${it.month}".padStart(2, '0')
                "${it.year}-$monthPart"
            }
        val items =
            buildList {
                for (key in keys) {
                    this += key
                    val monthEvents =
                        eventsGrouped[key]?.sortedByDescending { it.startTime }.orEmpty()
                    for (event in monthEvents) {
                        this += CalendarItem.EventItem(event)
                    }
                }
            }
        updateState {
            it.copy(
                items = items,
                canFetchMore = paginationManager.canFetchMore,
                loading = false,
                initial = false,
                refreshing = false,
            )
        }
        if (wasRefreshing) {
            emitEffect(CalendarMviModel.Effect.BackToTop)
        }
    }
}
