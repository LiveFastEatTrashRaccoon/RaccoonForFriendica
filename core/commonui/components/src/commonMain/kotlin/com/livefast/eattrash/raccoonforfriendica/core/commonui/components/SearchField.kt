package com.livefast.eattrash.raccoonforfriendica.core.commonui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.ancillaryTextAlpha
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings

@Composable
fun SearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp),
    hint: String? = null,
    hintTextStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    onClear: (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions =
        KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search,
        ),
) {
    val textColor = MaterialTheme.colorScheme.onBackground
    var textFieldValue by remember {
        mutableStateOf(
            TextFieldValue(text = value),
        )
    }
    val valueChangedCallback by rememberUpdatedState(onValueChange)
    LaunchedEffect(textFieldValue) {
        valueChangedCallback(textFieldValue.text)
    }
    var height by remember { mutableFloatStateOf(0f) }

    BasicTextField(
        modifier =
        modifier.onGloballyPositioned {
            height = it.size.toSize().height
        },
        value = textFieldValue,
        onValueChange = { newValue ->
            textFieldValue = newValue
        },
        keyboardOptions = keyboardOptions,
        singleLine = true,
        cursorBrush = SolidColor(textColor),
        textStyle = textStyle.copy(color = textColor),
        decorationBox =
        { innerTextField ->
            Row(
                modifier =
                Modifier
                    .fillMaxWidth()
                    .background(
                        color = backgroundColor,
                        shape =
                        with(LocalDensity.current) {
                            RoundedCornerShape((height / 2).toDp())
                        },
                    ).padding(
                        horizontal = 12.dp,
                        vertical = Spacing.s,
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                val iconModifier = Modifier.size(IconSize.m).padding(2.5.dp)
                Icon(
                    modifier = iconModifier,
                    imageVector = Icons.Default.Search,
                    contentDescription = LocalStrings.current.actionSearch,
                    tint = textColor,
                )
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    innerTextField()

                    if (textFieldValue.text.isEmpty() && hint != null) {
                        Text(
                            text = hint,
                            color = textColor.copy(ancillaryTextAlpha),
                            style = hintTextStyle,
                        )
                    }
                }

                if (textFieldValue.text.isNotEmpty() && onClear != null) {
                    Icon(
                        modifier =
                        iconModifier.clickable {
                            textFieldValue = TextFieldValue()
                        },
                        imageVector = Icons.Default.Clear,
                        contentDescription = LocalStrings.current.actionClear,
                        tint = textColor,
                    )
                }
            }
        },
    )
}
