package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.ancillaryTextAlpha

@Composable
fun SpoilerTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    hint: String? = null,
    hintTextStyle: TextStyle = LocalTextStyle.current,
    textStyle: TextStyle = LocalTextStyle.current,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    var contentHeightPx by remember { mutableFloatStateOf(0f) }
    val contentHeightDp = with(LocalDensity.current) { contentHeightPx.toDp() }

    BasicTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        maxLines = 1,
        cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground),
        textStyle = textStyle.copy(color = MaterialTheme.colorScheme.onBackground),
        decorationBox =
        { innerTextField ->
            Row(
                modifier = Modifier.background(color = backgroundColor),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.s),
            ) {
                SpoilerBar(
                    modifier = Modifier.size(width = 6.dp, height = contentHeightDp),
                )
                Box(
                    modifier =
                    Modifier
                        .weight(1f)
                        .onGloballyPositioned {
                            contentHeightPx = it.size.toSize().height
                        }.padding(Spacing.s),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    innerTextField()

                    if (value.text.isEmpty() && hint != null) {
                        Text(
                            text = hint,
                            color =
                            MaterialTheme.colorScheme.onBackground.copy(
                                ancillaryTextAlpha,
                            ),
                            style = hintTextStyle,
                        )
                    }
                }
                SpoilerBar(
                    modifier = Modifier.size(width = 6.dp, height = contentHeightDp),
                )
            }
        },
    )
}
