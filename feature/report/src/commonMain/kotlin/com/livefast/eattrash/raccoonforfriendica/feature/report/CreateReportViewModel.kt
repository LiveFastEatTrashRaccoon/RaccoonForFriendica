package com.livefast.eattrash.raccoonforfriendica.feature.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviDelegate
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.ReportCategory
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.NodeInfoRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.ReportRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.SupportedFeatureRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.cache.LocalItemCache
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ViewModelAssistedFactoryKey
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AssistedInject
class CreateReportViewModel(
    @Assisted args: CreateReportViewModelArgs,
    private val nodeInfoRepository: NodeInfoRepository,
    private val supportedFeatureRepository: SupportedFeatureRepository,
    private val reportRepository: ReportRepository,
    private val userCache: LocalItemCache<UserModel>,
) : ViewModel(),
    MviDelegate<CreateReportMvi.Intent, CreateReportMvi.State, CreateReportMvi.Effect>
    by DefaultMviDelegate(initialState = CreateReportMvi.State()),
    CreateReportMvi {

    private val userId = args.userId
    private val entryId = args.entryId

    init {
        viewModelScope.launch {
            supportedFeatureRepository.features
                .onEach { features ->
                    updateState {
                        it.copy(
                            availableCategories =
                            buildList {
                                this += ReportCategory.Other
                                this += ReportCategory.Spam
                                this += ReportCategory.Legal
                                if (features.supportReportCategoryRuleViolation) {
                                    this += ReportCategory.Violation
                                }
                            },
                        )
                    }
                }.launchIn(this)
            val user = userCache.get(userId)
            val rules = nodeInfoRepository.getRules().orEmpty()
            updateState {
                it.copy(
                    user = user,
                    availableRules = rules,
                )
            }
        }
    }

    override fun reduce(intent: CreateReportMvi.Intent) {
        when (intent) {
            is CreateReportMvi.Intent.ChangeCategory ->
                viewModelScope.launch {
                    updateState { it.copy(category = intent.category) }
                }

            is CreateReportMvi.Intent.ChangeForward ->
                viewModelScope.launch {
                    updateState { it.copy(forward = intent.value) }
                }

            is CreateReportMvi.Intent.ChangeViolatedRules ->
                viewModelScope.launch {
                    updateState { it.copy(violatedRuleIds = intent.ruleIds) }
                }

            is CreateReportMvi.Intent.SetComment ->
                viewModelScope.launch {
                    updateState { it.copy(commentValue = intent.value) }
                }

            CreateReportMvi.Intent.Submit -> submit()
        }
    }

    private fun submit() {
        // validate and submit
        val currentState = uiState.value
        val category = currentState.category
        val ruleIds = currentState.violatedRuleIds

        viewModelScope.launch {
            if (category == ReportCategory.Violation && ruleIds.isEmpty()) {
                emitEffect(CreateReportMvi.Effect.ValidationError.MissingRules)
                return@launch
            }

            updateState { it.copy(loading = true) }
            val successful =
                reportRepository.create(
                    userId = userId,
                    entryIds = entryId.takeIf { it.isNotEmpty() }?.let { listOf(it) },
                    category = category,
                    comment = currentState.commentValue.text,
                    forward = currentState.forward,
                    ruleIds = ruleIds.takeIf { category == ReportCategory.Violation },
                )
            if (successful) {
                emitEffect(CreateReportMvi.Effect.Success)
            } else {
                updateState { it.copy(loading = false) }
                emitEffect(CreateReportMvi.Effect.Failure)
            }
        }
    }

    companion object {
        fun getExtras(args: CreateReportViewModelArgs) = CreationExtras {
            this[KEY_ARGS] = args
        }
    }
}

private val KEY_ARGS = CreationExtras.Key<CreateReportViewModelArgs>()

data class CreateReportViewModelArgs(val userId: String, val entryId: String)

@AssistedFactory
@ViewModelAssistedFactoryKey(CreateReportViewModel::class)
@ContributesIntoMap(AppScope::class)
interface CreateReportViewModelFactory : ViewModelAssistedFactory {
    override fun create(extras: CreationExtras): CreateReportViewModel =
        create(extras[KEY_ARGS] ?: error("ViewModel creation args not found"))

    fun create(@Assisted args: CreateReportViewModelArgs): CreateReportViewModel
}
