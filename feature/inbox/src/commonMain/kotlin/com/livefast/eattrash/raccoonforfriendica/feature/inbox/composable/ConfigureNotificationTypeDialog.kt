package com.livefast.eattrash.raccoonforfriendica.feature.inbox.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.CornerSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toReadableName

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ConfigureNotificationTypeDialog(
    initialSelection: List<NotificationType>,
    availableTypes: List<NotificationType>,
    onClose: ((List<NotificationType>?) -> Unit)? = null,
) {
    val currentSelection =
        remember { mutableStateListOf<NotificationType>().apply { addAll(initialSelection) } }

    BasicAlertDialog(
        modifier = Modifier.clip(RoundedCornerShape(CornerSize.xxl)),
        onDismissRequest = {
            onClose?.invoke(null)
        },
    ) {
        Column(
            modifier =
            Modifier
                .background(color = MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp))
                .padding(Spacing.m),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
        ) {
            Text(
                text = LocalStrings.current.inboxConfigureFilterDialogTitle,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.height(Spacing.s))
            Text(
                text = LocalStrings.current.inboxConfigureFilterDialogSubtitle,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground,
            )

            Spacer(modifier = Modifier.height(Spacing.xs))

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
            ) {
                items(availableTypes) { type ->
                    val selected = currentSelection.contains(type)
                    Row(
                        modifier =
                        Modifier
                            .toggleable(
                                value = selected,
                                role = Role.Checkbox,
                                enabled = true,
                                onValueChange = {
                                    if (selected) {
                                        currentSelection -= type
                                    } else {
                                        currentSelection += type
                                    }
                                },
                            ).padding(vertical = Spacing.s, horizontal = Spacing.s),
                    ) {
                        Text(
                            text = type.toReadableName(),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Checkbox(
                            modifier = Modifier.size(IconSize.s),
                            checked = selected,
                            onCheckedChange = null,
                        )
                    }
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.s),
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = {
                        onClose?.invoke(null)
                    },
                ) {
                    Text(text = LocalStrings.current.buttonCancel)
                }
                Button(
                    onClick = {
                        onClose?.invoke(currentSelection.toList())
                    },
                ) {
                    Text(text = LocalStrings.current.buttonConfirm)
                }
            }
        }
    }
}
