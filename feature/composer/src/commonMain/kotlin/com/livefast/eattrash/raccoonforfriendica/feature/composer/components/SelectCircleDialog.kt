package com.livefast.eattrash.raccoonforfriendica.feature.composer.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.CornerSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.PlaceholderImage
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.CircleModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SelectCircleDialog(
    circles: List<CircleModel> = emptyList(),
    onClose: ((CircleModel?) -> Unit)? = null,
) {
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
                text = LocalStrings.current.selectCircleDialogTitle,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.height(Spacing.s))

            LazyColumn(
                modifier = Modifier.fillMaxWidth().heightIn(max = 200.dp),
            ) {
                if (circles.isEmpty()) {
                    item {
                        Text(
                            modifier = Modifier.fillMaxWidth().padding(top = Spacing.s),
                            text = LocalStrings.current.messageEmptyList,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                }

                items(circles) { circle ->
                    CircleResultItem(
                        circle = circle,
                        onClick = {
                            onClose?.invoke(circle)
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun CircleResultItem(
    circle: CircleModel,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val avatarSize = IconSize.m
    val fullColor = MaterialTheme.colorScheme.onBackground

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(CornerSize.xxl),
        color = Color.Transparent,
    ) {
        Row(
            modifier =
                Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication =
                            rememberRipple(
                                color = MaterialTheme.colorScheme.onBackground.copy(0.5f),
                            ),
                    ) {
                        onClick?.invoke()
                    }.padding(Spacing.s),
            horizontalArrangement = Arrangement.spacedBy(Spacing.s),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PlaceholderImage(
                size = avatarSize,
                title = circle.name,
            )

            Text(
                modifier = Modifier.weight(1f),
                text = circle.name,
                style = MaterialTheme.typography.titleMedium,
                color = fullColor,
            )
        }
    }
}
