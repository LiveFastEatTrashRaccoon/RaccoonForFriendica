package com.livefast.eattrash.raccoonforfriendica.feature.settings.feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.material.icons.filled.Clear
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.toWindowInsets
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.ProgressHud
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getNavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.utils.compose.safeImePadding
import com.livefast.eattrash.raccoonforfriendica.core.utils.validation.toReadableMessage
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class UserFeedbackScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val model = getScreenModel<UserFeedbackMviModel>()
        val uiState by model.uiState.collectAsState()
        val topAppBarState = rememberTopAppBarState()
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
        val snackbarHostState = remember { SnackbarHostState() }
        val navigationCoordinator = remember { getNavigationCoordinator() }
        val genericError = LocalStrings.current.messageGenericError

        LaunchedEffect(model) {
            model.effects
                .onEach { event ->
                    when (event) {
                        is UserFeedbackMviModel.Effect.Failure -> {
                            snackbarHostState.showSnackbar(
                                message = event.message ?: genericError,
                            )
                        }

                        UserFeedbackMviModel.Effect.Success -> {
                            navigationCoordinator.pop()
                        }
                    }
                }.launchIn(this)
        }

        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            modifier =
                Modifier
                    .navigationBarsPadding()
                    .safeImePadding(),
            topBar = {
                TopAppBar(
                    windowInsets = topAppBarState.toWindowInsets(),
                    scrollBehavior = scrollBehavior,
                    title = {
                        Text(
                            text = LocalStrings.current.settingsAboutReportIssue,
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
                                model.reduce(UserFeedbackMviModel.Intent.Submit)
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
                            start = Spacing.l,
                            end = Spacing.l,
                        ).consumeWindowInsets(padding)
                        .safeImePadding()
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.s),
            ) {
                Spacer(modifier = Modifier.height(Spacing.s))
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(text = LocalStrings.current.userFeedbackFieldEmail)
                    },
                    singleLine = true,
                    value = uiState.email,
                    isError = uiState.emailError != null,
                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            autoCorrectEnabled = false,
                        ),
                    onValueChange = { value ->
                        model.reduce(UserFeedbackMviModel.Intent.SetEmail(value))
                    },
                    supportingText = {
                        val error = uiState.emailError
                        if (error != null) {
                            Text(
                                text = error.toReadableMessage(),
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    },
                    trailingIcon = {
                        if (uiState.email.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    model.reduce(UserFeedbackMviModel.Intent.SetEmail(""))
                                },
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = null,
                                )
                            }
                        }
                    },
                )

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth().height(300.dp),
                    label = {
                        Text(text = LocalStrings.current.userFeedbackFieldComment)
                    },
                    value = uiState.comment,
                    isError = uiState.commentError != null,
                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            capitalization = KeyboardCapitalization.Sentences,
                        ),
                    placeholder = {
                        Text(
                            text = LocalStrings.current.userFeedbackCommentPlaceholder,
                        )
                    },
                    onValueChange = { value ->
                        model.reduce(UserFeedbackMviModel.Intent.SetComment(value))
                    },
                    supportingText = {
                        val error = uiState.commentError
                        if (error != null) {
                            Text(
                                text = error.toReadableMessage(),
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    },
                )
            }
        }

        if (uiState.loading) {
            ProgressHud()
        }
    }
}
