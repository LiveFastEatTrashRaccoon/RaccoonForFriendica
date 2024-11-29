package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.CornerSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.ancillaryTextAlpha
import com.livefast.eattrash.raccoonforfriendica.core.htmlparse.parseHtml
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings

@Composable
fun SpoilerCard(
    content: String,
    modifier: Modifier = Modifier,
    autoloadImages: Boolean = true,
    active: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val fullColor = MaterialTheme.colorScheme.onBackground
    val ancillaryColor = MaterialTheme.colorScheme.onBackground.copy(ancillaryTextAlpha)
    var contentHeightPx by remember { mutableStateOf(0f) }
    val contentHeightDp = with(LocalDensity.current) { contentHeightPx.toDp() }
    val annotatedContent =
        content.parseHtml(
            linkColor = MaterialTheme.colorScheme.primary,
            quoteColor = MaterialTheme.colorScheme.onBackground.copy(ancillaryTextAlpha),
        )

    Row(
        modifier =
            modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) {
                    onClick?.invoke()
                }.background(
                    color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
                    shape = RoundedCornerShape(CornerSize.l),
                ).clip(RoundedCornerShape(CornerSize.l)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.s),
    ) {
        SpoilerBar(
            modifier = Modifier.size(width = 6.dp, height = contentHeightDp),
        )
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .onGloballyPositioned {
                        contentHeightPx = it.size.toSize().height
                    }.padding(
                        vertical = Spacing.m,
                        horizontal = Spacing.s,
                    ),
            verticalArrangement = Arrangement.spacedBy(Spacing.s),
        ) {
            TextWithCustomEmojis(
                style = MaterialTheme.typography.titleMedium.copy(color = fullColor),
                text = annotatedContent,
                autoloadImages = autoloadImages,
                onClick = { _ ->
                    onClick?.invoke()
                },
            )
            Text(
                text =
                    if (active) {
                        LocalStrings.current.actionHideContent
                    } else {
                        LocalStrings.current.actionRevealContent
                    },
                style = MaterialTheme.typography.labelMedium.copy(color = ancillaryColor),
            )
        }
        SpoilerBar(
            modifier = Modifier.size(width = 6.dp, height = contentHeightDp),
            inverted = true,
        )
    }
}
