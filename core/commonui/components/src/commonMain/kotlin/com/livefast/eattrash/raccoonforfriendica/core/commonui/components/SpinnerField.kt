package com.livefast.eattrash.raccoonforfriendica.core.commonui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.toSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings

@Composable
fun SpinnerField(
    modifier: Modifier = Modifier,
    value: String = "",
    label: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    values: List<Pair<String, String>> = emptyList(),
    onValueChange: ((String) -> Unit)? = null,
) {
    val focusRequester = remember { FocusRequester() }
    var readOnly by remember { mutableStateOf(true) }
    var expanded by remember { mutableStateOf(false) }
    var availableWidthPx by remember { mutableStateOf(0f) }
    val availableWidth = with(LocalDensity.current) { availableWidthPx.toDp() }

    Box(
        modifier = modifier,
    ) {
        Column {
            OutlinedTextField(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .onGloballyPositioned {
                            availableWidthPx = it.size.toSize().width
                        }.focusRequester(focusRequester),
                value = value,
                label = label,
                placeholder = placeholder,
                supportingText = supportingText,
                singleLine = true,
                isError = isError,
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                trailingIcon = {
                    IconButton(
                        onClick = {
                            expanded = !expanded
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = LocalStrings.current.actionSelect,
                        )
                    }
                },
                readOnly = readOnly,
                onValueChange = {
                    onValueChange?.invoke(it)
                },
            )
            CustomDropDown(
                modifier = Modifier.width(availableWidth),
                expanded = expanded,
                onDismiss = {
                    expanded = false
                },
            ) {
                values.forEach { value ->
                    DropdownMenuItem(
                        onClick = {
                            onValueChange?.invoke(value.second)
                            expanded = false
                            val isCustom = value.second.isBlank()
                            if (isCustom) {
                                focusRequester.requestFocus()
                            }
                            readOnly = !isCustom
                        },
                        text = {
                            Text(
                                text = value.first,
                                modifier = Modifier.wrapContentWidth().align(Alignment.Start),
                            )
                        },
                    )
                }
            }
        }

        if (readOnly) {
            Spacer(
                modifier =
                    Modifier
                        .matchParentSize()
                        .background(Color.Transparent)
                        .padding(top = Spacing.s, bottom = Spacing.m)
                        .clickable(
                            onClick = { expanded = !expanded },
                        ),
            )
        }
    }
}
