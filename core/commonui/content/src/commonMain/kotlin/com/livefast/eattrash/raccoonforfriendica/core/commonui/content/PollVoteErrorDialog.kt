package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalUriHandler
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings

@Composable
fun PollVoteErrorDialog(onDismissRequest: () -> Unit) {
    val issueLink = "https://github.com/friendica/friendica/issues/11093"
    val uriHandler = LocalUriHandler.current
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = LocalStrings.current.dialogErrorTitle)
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                Text(text = LocalStrings.current.messagePollVoteErrorBody)
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    onDismissRequest()
                },
            ) {
                Text(text = LocalStrings.current.buttonClose)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    uriHandler.openUri(issueLink)
                },
            ) {
                Text(text = LocalStrings.current.buttonPollErrorOpenIssue)
            }
        },
    )
}
