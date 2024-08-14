package com.livefast.eattrash.raccoonforfriendica.feature.timeline.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toReadableName

@Composable
internal fun TimelineTypeBottomSheet(
    types: List<TimelineType> =
        listOf(
            TimelineType.All,
            TimelineType.Subscriptions,
        ),
    onSelected: ((TimelineType) -> Unit)? = null,
) {
    Column(
        modifier = Modifier.padding(bottom = Spacing.xl),
        verticalArrangement = Arrangement.spacedBy(Spacing.s),
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            text = LocalStrings.current.feedTypeTitle,
            style = MaterialTheme.typography.titleMedium,
        )
        LazyColumn {
            items(items = types) { type ->
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelected?.invoke(type)
                            }.padding(
                                horizontal = Spacing.m,
                                vertical = Spacing.s,
                            ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.s),
                ) {
                    Text(
                        text = type.toReadableName(),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}
