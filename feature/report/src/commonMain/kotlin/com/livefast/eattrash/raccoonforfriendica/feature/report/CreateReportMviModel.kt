package com.livefast.eattrash.raccoonforfriendica.feature.report

import androidx.compose.ui.text.input.TextFieldValue
import cafe.adriel.voyager.core.model.ScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.ReportCategory
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.RuleModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel

interface CreateReportMviModel :
    ScreenModel,
    MviModel<CreateReportMviModel.Intent, CreateReportMviModel.State, CreateReportMviModel.Effect> {
    sealed interface Intent {
        data class SetComment(val value: TextFieldValue) : Intent

        data class ChangeCategory(val category: ReportCategory) : Intent

        data class ChangeViolatedRules(val ruleIds: List<String>) : Intent

        data class ChangeForward(val value: Boolean) : Intent

        data object Submit : Intent
    }

    data class State(
        val user: UserModel? = null,
        val entry: TimelineEntryModel? = null,
        val loading: Boolean = false,
        val commentValue: TextFieldValue = TextFieldValue(),
        val forward: Boolean = false,
        val category: ReportCategory = ReportCategory.Other,
        val violatedRuleIds: List<String> = emptyList(),
        val availableCategories: List<ReportCategory> = emptyList(),
        val availableRules: List<RuleModel> = emptyList(),
    )

    sealed interface Effect {
        sealed interface ValidationError : Effect {
            data object MissingRules : ValidationError
        }

        data object Success : Effect

        data object Failure : Effect
    }
}
