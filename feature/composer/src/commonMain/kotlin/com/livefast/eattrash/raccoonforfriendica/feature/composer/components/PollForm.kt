package com.livefast.eattrash.raccoonforfriendica.feature.composer.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.SettingsRow
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.SettingsSwitchRow
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.getFormattedDate
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PollModel

@Composable
internal fun PollForm(
    poll: PollModel,
    optionLimit: Int = Int.MAX_VALUE,
    onChangeMultiple: ((Boolean) -> Unit)? = null,
    onAddOption: (() -> Unit)? = null,
    onRemoveOption: ((Int) -> Unit)? = null,
    onEditOption: ((Int, String) -> Unit)? = null,
    onEditExpirationDate: (() -> Unit)? = null,
) {
    val focusManager = LocalFocusManager.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier =
                Modifier.padding(
                    top = Spacing.m,
                    start = 10.dp,
                    end = 10.dp,
                ),
            text = LocalStrings.current.createPostPollSection,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(modifier = Modifier.weight(1f))
        if (poll.options.size < optionLimit) {
            IconButton(
                onClick = {
                    onAddOption?.invoke()
                },
            ) {
                Icon(
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = null,
                )
            }
        }
    }
    poll.options.forEachIndexed { idx, option ->
        OutlinedTextField(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        vertical = Spacing.xs,
                        horizontal = Spacing.s,
                    ),
            label = {
                Text(
                    text =
                        buildString {
                            append(LocalStrings.current.createPostPollOptionLabel)
                            append(" $idx")
                        },
                )
            },
            textStyle = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            value = option.title,
            onValueChange = {
                onEditOption?.invoke(idx, it)
            },
            keyboardOptions =
                KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    autoCorrect = true,
                    imeAction = ImeAction.Next,
                ),
            keyboardActions =
                KeyboardActions(
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                }
            ),
                    trailingIcon = {
                IconButton(
                    onClick = {
                        onRemoveOption?.invoke(idx)
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                    )
                }
            },
        )
    }
    SettingsSwitchRow(
        title = LocalStrings.current.createPostPollItemMultiple,
        value = poll.multiple,
        onValueChanged = {
            onChangeMultiple?.invoke(it)
        },
    )
    SettingsRow(
        title = LocalStrings.current.createPostPollItemExpirationDate,
        value =
            buildString {
                if (poll.expiresAt.isNullOrBlank()) {
                    append(LocalStrings.current.shortUnavailable)
                } else {
                    append(
                        getFormattedDate(
                            iso8601Timestamp = poll.expiresAt.orEmpty(),
                            format = "dd/MM/yy HH:mm",
                        ),
                    )
                }
            },
        onTap = {
            onEditExpirationDate?.invoke()
        },
    )
}
