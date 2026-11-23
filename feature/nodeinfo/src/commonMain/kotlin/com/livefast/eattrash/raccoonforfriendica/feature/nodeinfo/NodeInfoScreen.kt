package com.livefast.eattrash.raccoonforfriendica.feature.nodeinfo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.ChangeInstanceDialog
import com.livefast.eattrash.raccoonforfriendica.core.di.utils.LocalUiDeps
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Composable
fun NodeInfoScreen(model: NodeInfoMvi, modifier: Modifier = Modifier) {
    val uiState by model.uiState.collectAsStateWithLifecycle()
    val navigationCoordinator = LocalUiDeps.current.navigationCoordinator
    var changeInstanceDialogOpened by remember { mutableStateOf(false) }
    val successMessage = LocalStrings.current.messageSuccess

    LaunchedEffect(model) {
        model.effects
            .onEach { event ->
                when (event) {
                    NodeInfoMvi.Effect.AnonymousChangeNodeSuccess -> {
                        changeInstanceDialogOpened = false
                        navigationCoordinator.showGlobalMessage(successMessage)
                    }
                }
            }.launchIn(this)
    }

    NodeInfoScreenContent(
        modifier = modifier,
        uiState = uiState,
        onOpenChangeInstance = {
            changeInstanceDialogOpened = true
        },
    )

    if (changeInstanceDialogOpened) {
        ChangeInstanceDialog(
            nodeName = uiState.anonymousChangeNodeName,
            validationInProgress = uiState.anonymousChangeNodeValidationInProgress,
            validationError = uiState.anonymousChangeNodeNameError,
            onClose = {
                changeInstanceDialogOpened = false
            },
            onNodeChange = { value ->
                model.reduce(NodeInfoMvi.Intent.SetAnonymousChangeNode(value))
            },
            onSubmit = {
                model.reduce(NodeInfoMvi.Intent.SubmitAnonymousChangeNode)
            },
        )
    }
}
