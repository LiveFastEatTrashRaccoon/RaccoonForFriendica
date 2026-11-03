package com.livefast.eattrash.raccoonforfriendica.feature.settings.timelinetypes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviDelegate
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.FeedTypeModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toInt
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toTimelineType
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.FeedTypeRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.GetTimelineTypesUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AccountRepository
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
class ConfigureTimelineTypesViewModel(
    private val getTimelineTypes: GetTimelineTypesUseCase,
    private val accountRepository: AccountRepository,
    private val feedTypeRepository: FeedTypeRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel(),
    MviDelegate<ConfigureTimelineTypesMvi.Intent, ConfigureTimelineTypesMvi.State, ConfigureTimelineTypesMvi.Effect>
    by DefaultMviDelegate(initialState = ConfigureTimelineTypesMvi.State()),
    ConfigureTimelineTypesMvi {

    init {
        viewModelScope.launch {
            getTimelineTypes()
                .onEach { types ->
                    updateState {
                        it.copy(types = types)
                    }
                }.launchIn(this)
        }
    }

    override fun reduce(intent: ConfigureTimelineTypesMvi.Intent) {
        when (intent) {
            is ConfigureTimelineTypesMvi.Intent.Add -> handleAdd(intent.type)

            is ConfigureTimelineTypesMvi.Intent.Delete -> handleDelete(intent.type)

            ConfigureTimelineTypesMvi.Intent.EnterReorderMode -> handleEnterReorderMode()

            ConfigureTimelineTypesMvi.Intent.ExitReorderMode -> handleExitReorderMode()

            is ConfigureTimelineTypesMvi.Intent.Reorder -> handleReorder(
                fromIndex = intent.fromIndex,
                toIndex = intent.toIndex,
            )
        }
    }

    private fun handleAdd(type: TimelineType) {
        viewModelScope.launch {
            val account = accountRepository.getActive() ?: return@launch

            val newIndex = uiState.value.types.size
            val feedType = FeedTypeModel(
                accountId = account.id,
                type = type,
                info = (type as? TimelineType.Circle)?.circle?.id,
                index = newIndex,
            )
            feedTypeRepository.create(feedType)
        }
    }

    private fun handleDelete(type: TimelineType) {
        viewModelScope.launch {
            val account = accountRepository.getActive() ?: return@launch

            val info = (type as? TimelineType.Circle)?.circle?.id
            val feedType = feedTypeRepository.getBy(
                accountId = account.id,
                type = type.toInt(),
                info = info,
            )
            if (feedType != null) {
                feedTypeRepository.delete(feedType)
            }

            val currentSettings = settingsRepository.current.value
            if (currentSettings != null) {
                val defaultTimelineType = currentSettings.defaultTimelineType.toTimelineType()
                val defaultTimelineInfo = currentSettings.defaultTimelineId
                if (type == defaultTimelineType && defaultTimelineInfo == info) {
                    // the default feed was deleted, resets to local
                    val newSettings = currentSettings.copy(
                        defaultTimelineType = TimelineType.Local.toInt(),
                        defaultTimelineId = null,
                    )
                    settingsRepository.update(newSettings)
                    settingsRepository.changeCurrent(newSettings)
                }
            }
        }
    }

    private fun handleEnterReorderMode() {
        viewModelScope.launch {
            updateState { it.copy(reorderMode = true) }
        }
    }

    private fun handleExitReorderMode() {
        viewModelScope.launch {
            val account = accountRepository.getActive() ?: return@launch

            val items = uiState.value.types.mapIndexedNotNull { index, type ->
                val item = feedTypeRepository.getBy(
                    accountId = account.id,
                    type = type.toInt(),
                    info = (type as? TimelineType.Circle)?.circle?.id,
                )
                item?.copy(index = index)
            }
            feedTypeRepository.update(items)

            updateState {
                it.copy(reorderMode = false)
            }
        }
    }

    private fun handleReorder(fromIndex: Int, toIndex: Int) {
        viewModelScope.launch {
            updateState {
                val newTypes = it.types.toMutableList().apply {
                    add(toIndex, removeAt(fromIndex))
                }
                it.copy(types = newTypes)
            }
        }
    }
}
