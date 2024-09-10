package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.ancillaryTextAlpha
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationStatus
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationStatusNextAction
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.getNextAction
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toIcon

@Composable
fun UserNotificationButton(
    status: NotificationStatus,
    modifier: Modifier = Modifier,
    pending: Boolean = false,
    onClick: ((NotificationStatusNextAction) -> Unit)? = null,
) {
    Box(
        modifier = modifier,
    ) {
        if (!pending) {
            OutlinedIconButton(
                onClick = {
                    val nextAction = status.getNextAction()
                    onClick?.invoke(nextAction)
                },
            ) {
                Icon(
                    imageVector = status.toIcon(),
                    contentDescription = null,
                )
            }
        } else {
            CircularProgressIndicator(
                Modifier
                    .size(IconSize.l)
                    .border(
                        width = Dp.Hairline,
                        color =
                            MaterialTheme.colorScheme.onBackground.copy(
                                ancillaryTextAlpha,
                            ),
                        shape = CircleShape,
                    ).padding(6.dp),
                color = MaterialTheme.colorScheme.onBackground.copy(ancillaryTextAlpha),
            )
        }
    }
}
