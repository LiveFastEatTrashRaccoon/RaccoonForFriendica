package com.livefast.eattrash.raccoonforfriendica.feature.composer.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.ancillaryTextAlpha
import com.livefast.eattrash.raccoonforfriendica.core.utils.compose.getAnimatedDots

@Composable
internal fun SuggestionsBar(
    modifier: Modifier = Modifier,
    suggestions: List<String> = emptyList(),
    loadingMessage: String,
    loading: Boolean = false,
    onSelected: ((Int) -> Unit)? = null,
) {
    val fullColor = MaterialTheme.colorScheme.onSurfaceVariant
    val ancillaryColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(ancillaryTextAlpha)
    Row(
        modifier =
            modifier
                .height(38.dp)
                .padding(horizontal = Spacing.xxs)
                .horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val animatingPart = getAnimatedDots()
        if (suggestions.isEmpty()) {
            if (loading) {
                Text(
                    modifier = Modifier.padding(Spacing.s),
                    text =
                        buildString {
                            append(loadingMessage)
                            append(animatingPart)
                        },
                    style = MaterialTheme.typography.labelMedium,
                    color = ancillaryColor,
                )
            }
        } else {
            suggestions.forEachIndexed { idx, title ->
                Text(
                    modifier =
                        Modifier
                            .clickable {
                                onSelected?.invoke(idx)
                            }.padding(Spacing.s),
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = fullColor,
                )
            }
            if (loading) {
                Text(
                    modifier = Modifier.padding(Spacing.s),
                    text = animatingPart,
                    style = MaterialTheme.typography.labelMedium,
                    color = fullColor,
                )
            }
        }
    }
}
