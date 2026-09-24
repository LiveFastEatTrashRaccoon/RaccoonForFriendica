package com.livefast.eattrash.raccoonforfriendica.feature.circles.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.core.utils.validation.ValidationError
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.CirclesRepository
import com.livefast.eattrash.raccoonforfriendica.feature.circles.edit.CircleEditorViewModel.Companion.KEY_ARGS
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
    @Assisted args: CircleEditorViewModelArgs,
    private val circlesRepository: CirclesRepository,
) : ViewModel(),
    MviModelDelegate<CircleEditorMviModel.Intent, CircleEditorMviModel.State, CircleEditorMviModel.Effect>
    by DefaultMviModelDelegate(initialState = CircleEditorMviModel.State()),
    CircleEditorMviModel {

    init {
        viewModelScope.launch {
            updateState {
                it.copy(data = args.data)
            }
        }
    }

    override fun reduce(intent: CircleEditorMviModel.Intent) {
        when (intent) {
            is CircleEditorMviModel.Intent.Submit -> submit()

            is CircleEditorMviModel.Intent.UpdateData -> viewModelScope.launch {
                updateState {
                    it.copy(data = intent.data)
                }
            }
        }
    }

    private fun submit() {
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
                    emitEffect(CircleEditorMviModel.Effect.Success(item))
                } else {
                    emitEffect(CircleEditorMviModel.Effect.Failure)
                }
            } else {
                val item =
                    circlesRepository.create(
                        title = title,
                        exclusive = data.exclusive,
                        replyPolicy = data.replyPolicy,
                    )
                if (item != null) {
                    emitEffect(CircleEditorMviModel.Effect.Success(item))
                } else {
                    emitEffect(CircleEditorMviModel.Effect.Failure)
                }
            }
        }
    }

    companion object {
        val KEY_ARGS = CreationExtras.Key<CircleEditorViewModelArgs>()
    }
}

data class CircleEditorViewModelArgs(val data: CircleEditorData)

@AssistedFactory
@ViewModelAssistedFactoryKey(CircleEditorViewModel::class)
@ContributesIntoMap(AppScope::class)
fun interface CircleEditorViewModelFactory : ViewModelAssistedFactory {
    override fun create(extras: CreationExtras): CircleEditorViewModel {
        val args = extras[KEY_ARGS] ?: error("ViewModel creation args not found")
        return create(args)
    }

    fun create(@Assisted args: CircleEditorViewModelArgs): CircleEditorViewModel
}
