package com.livefast.eattrash.raccoonforfriendica.feature.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getNavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.utils.compose.autofill
import com.livefast.eattrash.raccoonforfriendica.core.utils.compose.safeImePadding
import com.livefast.eattrash.raccoonforfriendica.core.utils.validation.toReadableMessage
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class LoginScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
    @Composable
    override fun Content() {
        val model = getScreenModel<LoginMviModel>()
        val uiState by model.uiState.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }
        val navigationCoordinator = remember { getNavigationCoordinator() }
        val genericError = LocalStrings.current.messageGenericError
        val successMessage = LocalStrings.current.messageSuccess
        val instanceFocusRequester = remember { FocusRequester() }
        val usernameFocusRequester = remember { FocusRequester() }
        val passwordFocusRequester = remember { FocusRequester() }
        val focusManager = LocalFocusManager.current

        LaunchedEffect(model) {
            model.effects
                .onEach { event ->
                    when (event) {
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
                    title = {
                        Text(
                            text = LocalStrings.current.loginTitle,
                            style = MaterialTheme.typography.titleMedium,
                        )
                    },
                    navigationIcon = {
                        Image(
                            modifier =
                                Modifier.clickable {
                                    navigationCoordinator.pop()
                                },
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
                        )
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
            content = { padding ->
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
                    Spacer(modifier = Modifier.height(Spacing.m))

                    // instance name
                    TextField(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .focusRequester(instanceFocusRequester),
                        label = {
                            Text(text = LocalStrings.current.fieldNodeName)
                        },
                        singleLine = true,
                        value = uiState.nodeName,
                        isError = uiState.nodeNameError != null,
                        keyboardActions =
                            KeyboardActions(
                                onNext = {
                                    usernameFocusRequester.requestFocus()
                                },
                            ),
                        keyboardOptions =
                            KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                autoCorrect = false,
                                imeAction = ImeAction.Next,
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
                        trailingIcon = {
                            if (uiState.nodeName.isNotEmpty()) {
                                Icon(
                                    modifier =
                                        Modifier.clickable {
                                            model.reduce(LoginMviModel.Intent.SetNodeName(""))
                                        },
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = null,
                                )
                            }
                        },
                    )

                    // user name
                    TextField(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .autofill(
                                    autofillTypes =
                                        listOf(
                                            AutofillType.Username,
                                            AutofillType.EmailAddress,
                                        ),
                                    onFill = { value ->
                                        model.reduce(LoginMviModel.Intent.SetUsername(value))
                                    },
                                ).focusRequester(usernameFocusRequester),
                        label = {
                            Text(text = LocalStrings.current.fieldUsername)
                        },
                        singleLine = true,
                        value = uiState.username,
                        isError = uiState.usernameError != null,
                        keyboardActions =
                            KeyboardActions(
                                onNext = {
                                    passwordFocusRequester.requestFocus()
                                },
                            ),
                        keyboardOptions =
                            KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                autoCorrect = false,
                                imeAction = ImeAction.Next,
                            ),
                        onValueChange = { value ->
                            model.reduce(LoginMviModel.Intent.SetUsername(value))
                        },
                        supportingText = {
                            val error = uiState.usernameError
                            if (error != null) {
                                Text(
                                    text = error.toReadableMessage(),
                                    color = MaterialTheme.colorScheme.error,
                                )
                            }
                        },
                    )

                    // password
                    var transformation: VisualTransformation by remember {
                        mutableStateOf(PasswordVisualTransformation())
                    }
                    TextField(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .autofill(
                                    autofillTypes = listOf(AutofillType.Password),
                                    onFill = { value ->
                                        model.reduce(LoginMviModel.Intent.SetPassword(value))
                                    },
                                ).focusRequester(passwordFocusRequester),
                        label = {
                            Text(text = LocalStrings.current.fieldPassword)
                        },
                        singleLine = true,
                        value = uiState.password,
                        isError = uiState.passwordError != null,
                        keyboardOptions =
                            KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Next,
                            ),
                        onValueChange = { value ->
                            model.reduce(LoginMviModel.Intent.SetPassword(value))
                        },
                        visualTransformation = transformation,
                        trailingIcon = {
                            Image(
                                modifier =
                                    Modifier.clickable {
                                        transformation =
                                            if (transformation == VisualTransformation.None) {
                                                PasswordVisualTransformation()
                                            } else {
                                                VisualTransformation.None
                                            }
                                    },
                                imageVector =
                                    if (transformation == VisualTransformation.None) {
                                        Icons.Default.VisibilityOff
                                    } else {
                                        Icons.Default.Visibility
                                    },
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground),
                            )
                        },
                        supportingText = {
                            val error = uiState.passwordError
                            if (error != null) {
                                Text(
                                    text = error.toReadableMessage(),
                                    color = MaterialTheme.colorScheme.error,
                                )
                            }
                        },
                    )

                    Spacer(modifier = Modifier.height(Spacing.m))

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
                            if (uiState.loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(IconSize.s),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                            }
                            Text(LocalStrings.current.buttonConfirm)
                        }
                    }
                }
            },
        )
    }
}
