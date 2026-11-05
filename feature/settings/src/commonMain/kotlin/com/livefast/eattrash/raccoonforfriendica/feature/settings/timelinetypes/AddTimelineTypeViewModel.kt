package com.livefast.eattrash.raccoonforfriendica.feature.settings.timelinetypes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviDelegate
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.CircleModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.CirclesRepository
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

@OptIn(FlowPreview::class)
@AssistedInject
class AddTimelineTypeViewModel(
    @Assisted private val idsToExclude: List<String>,
    private val circlesRepository: CirclesRepository,
) : ViewModel(),
    MviDelegate<AddTimelineTypeMvi.Intent, AddTimelineTypeMvi.State, AddTimelineTypeMvi.Effect>
    by DefaultMviDelegate(initialState = AddTimelineTypeMvi.State()),
    AddTimelineTypeMvi {

    private var circles: List<CircleModel> = emptyList()

    init {
        viewModelScope.launch {
            uiState
                .map { it.query }
                .distinctUntilChanged()
                .drop(1)
                .debounce(750.milliseconds)
                .onEach { query ->
                    refresh(query)
                }.launchIn(this)

            populate()
        }
    }

    override fun reduce(intent: AddTimelineTypeMvi.Intent) {
        when (intent) {
            is AddTimelineTypeMvi.Intent.SetQuery -> handleSetQuery(intent.query)
        }
    }

    private fun handleSetQuery(query: String) {
        viewModelScope.launch {
            viewModelScope.launch {
                updateState { it.copy(query = query) }
            }
        }
    }

    private suspend fun populate() {
        updateState { it.copy(loading = true) }
        val remoteCircles = circlesRepository.getAll().orEmpty()
        circles = remoteCircles.filter { it.id !in idsToExclude }
        updateState {
            it.copy(
                circles = circles.toList(),
                loading = false,
            )
        }
    }

    private suspend fun refresh(query: String) {
        if (uiState.value.loading) {
            return
        }

        updateState { it.copy(loading = true) }
        val filteredResults = circles.filter { query.isEmpty() || it.name.contains(query) }
        updateState {
            it.copy(
                circles = filteredResults,
                loading = false,
            )
        }
    }

    companion object {
        fun getExtras(idsToExclude: List<String>): CreationExtras = CreationExtras {
            this[KEY_IDS_TO_EXCLUDE] = idsToExclude
        }
    }
}

private val KEY_IDS_TO_EXCLUDE = CreationExtras.Key<List<String>>()

@AssistedFactory
@ViewModelAssistedFactoryKey(AddTimelineTypeViewModel::class)
@ContributesIntoMap(AppScope::class)
fun interface AddTimelineTypeViewModelFactory : ViewModelAssistedFactory {
    override fun create(extras: CreationExtras): AddTimelineTypeViewModel =
        create(extras[KEY_IDS_TO_EXCLUDE] ?: error("idsToExclude argument not found"))

    fun create(@Assisted idsToExclude: List<String>): AddTimelineTypeViewModel
}
