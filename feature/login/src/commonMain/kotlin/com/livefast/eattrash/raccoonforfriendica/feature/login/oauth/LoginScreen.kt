package com.livefast.eattrash.raccoonforfriendica.feature.login.oauth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.toWindowInsets
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.ProgressHud
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.SpinnerField
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getNavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.utils.compose.safeImePadding
import com.livefast.eattrash.raccoonforfriendica.core.utils.validation.toReadableMessage
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.DefaultFriendicaInstances
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class LoginScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val model = getScreenModel<LoginMviModel>()
        val uiState by model.uiState.collectAsState()
        val topAppBarState = rememberTopAppBarState()
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
        val snackbarHostState = remember { SnackbarHostState() }
        val navigationCoordinator = remember { getNavigationCoordinator() }
        val uriHandler = LocalUriHandler.current
        val focusManager = LocalFocusManager.current
        val genericError = LocalStrings.current.messageGenericError
        val successMessage = LocalStrings.current.messageSuccess

        LaunchedEffect(model) {
            model.effects
                .onEach { event ->
                    when (event) {
                        is LoginMviModel.Effect.OpenUrl -> {
                            uriHandler.openUri(event.url)
                        }
                        is LoginMviModel.Effect.Failure -> {
                            snackbarHostState.showSnackbar(
                                message = event.message ?: genericError,
                            )
                        }

                        LoginMviModel.Effect.Success -> {
                            snackbarHostState.showSnackbar(
                                message = successMessage,
                            )
                            navigationCoordinator.pop()
                        }
                    }
                }.launchIn(this)
        }

        Scaffold(
            modifier = Modifier.navigationBarsPadding(),
            topBar = {
                TopAppBar(
                    windowInsets = topAppBarState.toWindowInsets(),
                    scrollBehavior = scrollBehavior,
                    title = {
                        Text(
                            text = LocalStrings.current.loginTitle,
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
            ) {
                Spacer(modifier = Modifier.height(Spacing.s))

                // instance name
                SpinnerField(
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(
                            text = LocalStrings.current.fieldNodeName,
                        )
                    },
                    values =
                        buildList {
                            for (instance in DefaultFriendicaInstances) {
                                this += buildString {
                                    append(instance.value)
                                    append("  ")
                                    append(instance.lang)
                                } to instance.value
                            }
                            this += LocalStrings.current.itemOther to ""
                        },
                    value = uiState.nodeName,
                    isError = uiState.nodeNameError != null,
                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            autoCorrectEnabled = false,
                            imeAction = ImeAction.Done,
                        ),
                    keyboardActions =
                        KeyboardActions(
                            onNext = {
                                model.reduce(LoginMviModel.Intent.Submit)
                            },
                        ),
                    onValueChange = { value ->
                        model.reduce(LoginMviModel.Intent.SetNodeName(value))
                    },
                    supportingText = {
                        val error = uiState.nodeNameError
                        if (error != null) {
                            Text(
                                text = error.toReadableMessage(),
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    },
                )

                Button(
                    modifier = Modifier.padding(top = Spacing.l),
                    onClick = {
                        focusManager.clearFocus()
                        model.reduce(LoginMviModel.Intent.Submit)
                    },
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.s),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(LocalStrings.current.buttonConfirm)
                    }
                }
            }
        }

        if (uiState.loading) {
            ProgressHud()
        }
    }
}
