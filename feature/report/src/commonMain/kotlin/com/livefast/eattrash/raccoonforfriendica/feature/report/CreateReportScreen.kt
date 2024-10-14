package com.livefast.eattrash.raccoonforfriendica.feature.report

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.toWindowInsets
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomModalBottomSheet
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomModalBottomSheetItem
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.ProgressHud
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.SettingsRow
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.SettingsSwitchRow
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getNavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.utils.compose.safeImePadding
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.ReportCategory
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toReadableName
import com.livefast.eattrash.raccoonforfriendica.feature.report.components.SelectViolatedRulesDialog
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.core.parameter.parametersOf

class CreateReportScreen(
    private val userId: String,
    private val entryId: String?,
) : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val model =
            getScreenModel<CreateReportMviModel>(
                parameters = {
                    parametersOf(userId, entryId)
                },
            )
        val uiState by model.uiState.collectAsState()
        val topAppBarState = rememberTopAppBarState()
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
        val snackbarHostState = remember { SnackbarHostState() }
        val navigationCoordinator = remember { getNavigationCoordinator() }
        var categoryBottomSheetOpened by remember { mutableStateOf(false) }
        var ruleSelectionOpened by remember { mutableStateOf(false) }
        val genericError = LocalStrings.current.messageGenericError
        val missingRulesError = LocalStrings.current.messageMissingRules

        LaunchedEffect(model) {
            model.effects
                .onEach { event ->
                    when (event) {
                        CreateReportMviModel.Effect.ValidationError.MissingRules ->
                            snackbarHostState.showSnackbar(message = missingRulesError)

                        CreateReportMviModel.Effect.Failure ->
                            snackbarHostState.showSnackbar(message = genericError)

                        CreateReportMviModel.Effect.Success -> navigationCoordinator.pop()
                    }
                }.launchIn(this)
        }

        Scaffold(
            modifier =
                Modifier
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .navigationBarsPadding()
                    .safeImePadding(),
            topBar = {
                TopAppBar(
                    windowInsets = topAppBarState.toWindowInsets(),
                    scrollBehavior = scrollBehavior,
                    title = {
                        Text(
                            text =
                                buildString {
                                    val author = uiState.user
                                    if (author != null) {
                                        if (entryId != null) {
                                            append(LocalStrings.current.createReportTitleEntry)
                                        } else {
                                            append(LocalStrings.current.createReportTitleUser)
                                        }
                                        append(" ")
                                        append(author.handle)
                                    }
                                },
                            style = MaterialTheme.typography.titleMedium,
                        )
                    },
                    navigationIcon = {
                        if (navigationCoordinator.canPop.value) {
                            IconButton(
                                onClick = {
                                    navigationCoordinator.pop()
                                },
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                    contentDescription = null,
                                )
                            }
                        }
                    },
                    actions = {
                        FilledIconButton(
                            onClick = {
                                model.reduce(CreateReportMviModel.Intent.Submit)
                            },
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.Send,
                                contentDescription = null,
                            )
                        }
                    },
                )
            },
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState,
                ) { data ->
                    Snackbar(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        snackbarData = data,
                    )
                }
            },
        ) { padding ->
            Column(
                modifier =
                    Modifier
                        .padding(
                            top = padding.calculateTopPadding(),
                        ).consumeWindowInsets(padding)
                        .verticalScroll(rememberScrollState()),
            ) {
                // category
                SettingsRow(
                    title = LocalStrings.current.createReportItemCategory,
                    value = uiState.category.toReadableName(),
                    onTap = {
                        categoryBottomSheetOpened = true
                    },
                )

                // violated rules
                if (uiState.category == ReportCategory.Violation) {
                    SettingsRow(
                        title = LocalStrings.current.createReportItemRules,
                        value =
                            buildString {
                                val count = uiState.violatedRuleIds.size
                                append(count)
                                append(" ")
                                append(LocalStrings.current.createReportSelectedRules(count))
                            },
                        onTap = {
                            ruleSelectionOpened = true
                        },
                    )
                }

                // comment body
                OutlinedTextField(
                    modifier =
                        Modifier
                            .padding(
                                top = Spacing.s,
                                start = Spacing.xs,
                                end = Spacing.xs,
                            ).fillMaxWidth()
                            .height(300.dp),
                    placeholder = {
                        Text(
                            text = LocalStrings.current.createReportCommentPlaceholder,
                        )
                    },
                    value = uiState.commentValue,
                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            capitalization = KeyboardCapitalization.Sentences,
                        ),
                    onValueChange = { value ->
                        model.reduce(
                            CreateReportMviModel.Intent.SetComment(
                                value = value,
                            ),
                        )
                    },
                )

                // forward switch
                SettingsSwitchRow(
                    title = LocalStrings.current.createReportItemForward,
                    value = uiState.forward,
                    onValueChanged = {
                        model.reduce(CreateReportMviModel.Intent.ChangeForward(it))
                    },
                )
            }
        }

        if (uiState.loading) {
            ProgressHud()
        }

        if (categoryBottomSheetOpened) {
            CustomModalBottomSheet(
                items =
                    uiState.availableCategories.map {
                        CustomModalBottomSheetItem(label = it.toReadableName())
                    },
                onSelected = { idx ->
                    categoryBottomSheetOpened = false
                    val categories = uiState.availableCategories
                    if (idx != null && idx in categories.indices) {
                        val category = categories[idx]
                        model.reduce(CreateReportMviModel.Intent.ChangeCategory(category))
                    }
                },
            )
        }

        if (ruleSelectionOpened) {
            SelectViolatedRulesDialog(
                initialSelection = uiState.violatedRuleIds,
                rules = uiState.availableRules,
                onClose = { ruleIds ->
                    ruleSelectionOpened = false
                    if (ruleIds != null) {
                        model.reduce(CreateReportMviModel.Intent.ChangeViolatedRules(ruleIds))
                    }
                },
            )
        }
    }
}
