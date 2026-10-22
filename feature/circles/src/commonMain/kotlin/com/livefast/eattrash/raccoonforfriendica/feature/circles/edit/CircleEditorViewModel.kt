package com.livefast.eattrash.raccoonforfriendica.feature.circles.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviDelegate
import com.livefast.eattrash.raccoonforfriendica.core.utils.validation.ValidationError
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.CirclesRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ViewModelAssistedFactoryKey
import kotlinx.coroutines.launch

@AssistedInject
class CircleEditorViewModel(
    @Assisted private val data: CircleEditorData,
    private val circlesRepository: CirclesRepository,
) : ViewModel(),
    MviDelegate<CircleEditorMvi.Intent, CircleEditorMvi.State, CircleEditorMvi.Effect>
    by DefaultMviDelegate(initialState = CircleEditorMvi.State()),
    CircleEditorMvi {

    init {
        viewModelScope.launch {
            updateState {
                it.copy(data = data)
            }
        }
    }

    override fun reduce(intent: CircleEditorMvi.Intent) {
        when (intent) {
            is CircleEditorMvi.Intent.Submit -> handleSubmit()
            is CircleEditorMvi.Intent.UpdateData -> handleUpdateData(intent.data)
        }
    }

    private fun handleUpdateData(data: CircleEditorData) {
        viewModelScope.launch {
            updateState {
                it.copy(data = data)
            }
        }
    }

    private fun handleSubmit() {
        val data = uiState.value.data
        viewModelScope.launch {
            val title = data.title
            if (title.isEmpty()) {
                updateState {
                    it.copy(data = it.data.copy(titleError = ValidationError.MissingField))
                }
                return@launch
            }

            updateState {
                it.copy(data = it.data.copy(titleError = null))
            }

            if (data.id != null) {
                val item =
                    circlesRepository.update(
                        id = data.id,
                        title = title,
                        exclusive = data.exclusive,
                        replyPolicy = data.replyPolicy,
                    )
                if (item != null) {
                    emitEffect(CircleEditorMvi.Effect.Success(item))
                } else {
                    emitEffect(CircleEditorMvi.Effect.Failure)
                }
            } else {
                val item =
                    circlesRepository.create(
                        title = title,
                        exclusive = data.exclusive,
                        replyPolicy = data.replyPolicy,
                    )
                if (item != null) {
                    emitEffect(CircleEditorMvi.Effect.Success(item))
                } else {
                    emitEffect(CircleEditorMvi.Effect.Failure)
                }
            }
        }
    }

    companion object {
        fun getExtras(data: CircleEditorData) = CreationExtras {
            this[KEY_DATA] = data
        }
    }
}

private val KEY_DATA = CreationExtras.Key<CircleEditorData>()

@AssistedFactory
@ViewModelAssistedFactoryKey(CircleEditorViewModel::class)
@ContributesIntoMap(AppScope::class)
fun interface CircleEditorViewModelFactory : ViewModelAssistedFactory {
    override fun create(extras: CreationExtras): CircleEditorViewModel =
        create(extras[KEY_DATA] ?: error("data argument not found"))

    fun create(@Assisted data: CircleEditorData): CircleEditorViewModel
}
