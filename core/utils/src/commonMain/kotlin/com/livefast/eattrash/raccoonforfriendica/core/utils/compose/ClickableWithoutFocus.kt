package com.livefast.eattrash.raccoonforfriendica.core.utils.compose

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.input.pointer.pointerInput

/**
 * A modifier that makes a component clickable without it becoming focusable.
 *
 * This is particularly useful on desktop to avoid the initial focus highlight
 * on headers or other decorative elements when a screen is opened.
 *
 * @param interactionSource interaction source
 * @param onClick on click handler
 */
@Composable
fun Modifier.clickableWithoutFocus(
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    onClick: () -> Unit
): Modifier {
    return this
        .hoverable(interactionSource)
        .indication(interactionSource, LocalIndication.current)
        .pointerInput(interactionSource, onClick) {
            detectTapGestures(
                onPress = { offset ->
                    val press = PressInteraction.Press(offset)
                    interactionSource.emit(press)
                    try {
                        tryAwaitRelease()
                    } finally {
                        interactionSource.emit(PressInteraction.Release(press))
                    }
                },
                onTap = {
                    onClick()
                },
            )
        }
        .focusProperties { canFocus = false }
}
