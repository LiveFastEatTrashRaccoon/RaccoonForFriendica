package com.livefast.eattrash.raccoonforfriendica.feature.report

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
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
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam

@Factory(binds = [CreateReportMviModel::class])
class CreateReportViewModel(
    @InjectedParam private val userId: String,
    @InjectedParam private val entryId: String?,
    private val nodeInfoRepository: NodeInfoRepository,
    private val supportedFeatureRepository: SupportedFeatureRepository,
    private val reportRepository: ReportRepository,
    private val userCache: LocalItemCache<UserModel>,
    private val entryCache: LocalItemCache<TimelineEntryModel>,
) : DefaultMviModel<CreateReportMviModel.Intent, CreateReportMviModel.State, CreateReportMviModel.Effect>(
        initialState = CreateReportMviModel.State(),
    ),
    CreateReportMviModel {
    init {
        screenModelScope.launch {
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
            val entry = entryId?.let { entryCache.get(it) }
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
                screenModelScope.launch {
                    updateState { it.copy(category = intent.category) }
                }

            is CreateReportMviModel.Intent.ChangeForward ->
                screenModelScope.launch {
                    updateState { it.copy(forward = intent.value) }
                }

            is CreateReportMviModel.Intent.ChangeViolatedRules ->
                screenModelScope.launch {
                    updateState { it.copy(violatedRuleIds = intent.ruleIds) }
                }

            is CreateReportMviModel.Intent.SetComment ->
                screenModelScope.launch {
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

        screenModelScope.launch {
            if (category == ReportCategory.Violation && ruleIds.isEmpty()) {
                emitEffect(CreateReportMviModel.Effect.ValidationError.MissingRules)
                return@launch
            }

            updateState { it.copy(loading = true) }
            val successful =
                reportRepository.create(
                    userId = userId,
                    entryIds = entryId?.let { listOf(it) },
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
