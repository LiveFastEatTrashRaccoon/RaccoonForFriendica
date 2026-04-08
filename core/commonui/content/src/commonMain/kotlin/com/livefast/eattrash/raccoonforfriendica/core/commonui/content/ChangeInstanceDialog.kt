package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.CornerSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.SpinnerField
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.utils.validation.ValidationError
import com.livefast.eattrash.raccoonforfriendica.core.utils.validation.toReadableMessage
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.DefaultFriendicaInstances

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeInstanceDialog(
    nodeName: String,
    modifier: Modifier = Modifier,
    validationInProgress: Boolean = false,
    validationError: ValidationError? = null,
    exclusions: List<String> = emptyList(),
    onClose: (() -> Unit)? = null,
    onNodeChange: ((String) -> Unit)? = null,
    onSubmit: (() -> Unit)? = null,
) {
    BasicAlertDialog(
        modifier = modifier.clip(RoundedCornerShape(CornerSize.xxl)),
        onDismissRequest = {
            onClose?.invoke()
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
                text = LocalStrings.current.changeNodeDialogTitle,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.height(Spacing.s))

            SpinnerField(
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text(
                        text = LocalStrings.current.fieldNodeName,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                },
                isError = validationError != null,
                supportingText = {
                    if (validationError != null) {
                        Text(
                            text = validationError.toReadableMessage(),
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                },
                values =
                buildList {
                    for (instance in DefaultFriendicaInstances.filter { it.value !in exclusions }) {
                        this += buildString {
                            append(instance.value)
                            append("  ")
                            append(instance.lang)
                        } to instance.value
                    }
                    this += LocalStrings.current.itemOther to ""
                },
                value = nodeName,
                keyboardOptions =
                KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                ),
                onValueChange = { value ->
                    onNodeChange?.invoke(value)
                },
            )

            Spacer(modifier = Modifier.height(Spacing.xs))
            Button(
                onClick = {
                    onSubmit?.invoke()
                },
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.s),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (validationInProgress) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(IconSize.s),
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                    Text(text = LocalStrings.current.buttonConfirm)
                }
            }
        }
    }
}
