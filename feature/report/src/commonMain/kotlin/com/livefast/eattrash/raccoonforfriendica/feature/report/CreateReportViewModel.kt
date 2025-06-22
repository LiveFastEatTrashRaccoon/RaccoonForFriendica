package com.livefast.eattrash.raccoonforfriendica.feature.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.ReportCategory
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.LocalItemCache
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.NodeInfoRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.ReportRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.SupportedFeatureRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class CreateReportViewModel(
    private val userId: String,
    private val entryId: String,
    private val nodeInfoRepository: NodeInfoRepository,
    private val supportedFeatureRepository: SupportedFeatureRepository,
    private val reportRepository: ReportRepository,
    private val userCache: LocalItemCache<UserModel>,
    private val entryCache: LocalItemCache<TimelineEntryModel>,
) : ViewModel(),
    MviModelDelegate<CreateReportMviModel.Intent, CreateReportMviModel.State, CreateReportMviModel.Effect>
    by DefaultMviModelDelegate(initialState = CreateReportMviModel.State()),
    CreateReportMviModel {
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
            val entry = entryId.takeIf { it.isNotEmpty() }?.let { entryCache.get(it) }
            val rules = nodeInfoRepository.getRules().orEmpty()
            updateState {
                it.copy(
                    user = user,
                    entry = entry,
                    availableRules = rules,
                )
            }
        }
    }

    override fun reduce(intent: CreateReportMviModel.Intent) {
        when (intent) {
            is CreateReportMviModel.Intent.ChangeCategory ->
                viewModelScope.launch {
                    updateState { it.copy(category = intent.category) }
                }

            is CreateReportMviModel.Intent.ChangeForward ->
                viewModelScope.launch {
                    updateState { it.copy(forward = intent.value) }
                }

            is CreateReportMviModel.Intent.ChangeViolatedRules ->
                viewModelScope.launch {
                    updateState { it.copy(violatedRuleIds = intent.ruleIds) }
                }

            is CreateReportMviModel.Intent.SetComment ->
                viewModelScope.launch {
                    updateState { it.copy(commentValue = intent.value) }
                }

            CreateReportMviModel.Intent.Submit -> submit()
        }
    }

    private fun submit() {
        // validate and submit
        val currentState = uiState.value
        val category = currentState.category
        val ruleIds = currentState.violatedRuleIds

        viewModelScope.launch {
            if (category == ReportCategory.Violation && ruleIds.isEmpty()) {
                emitEffect(CreateReportMviModel.Effect.ValidationError.MissingRules)
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
                emitEffect(CreateReportMviModel.Effect.Success)
            } else {
                updateState { it.copy(loading = false) }
                emitEffect(CreateReportMviModel.Effect.Failure)
            }
        }
    }
}
