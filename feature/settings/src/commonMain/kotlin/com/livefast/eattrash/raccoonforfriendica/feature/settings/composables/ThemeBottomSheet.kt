package com.livefast.eattrash.raccoonforfriendica.feature.settings.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.UiTheme
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.toIcon
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.toReadableName
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings

@Composable
internal fun ThemeBottomSheet(
    themes: List<UiTheme> =
        listOf(
            UiTheme.Light,
            UiTheme.Dark,
            UiTheme.Black,
            UiTheme.Default,
        ),
    onSelected: ((UiTheme) -> Unit)? = null,
) {
    Column(
        modifier = Modifier.padding(bottom = Spacing.xl),
        verticalArrangement = Arrangement.spacedBy(Spacing.s),
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            text = LocalStrings.current.settingsItemTheme,
            style = MaterialTheme.typography.titleMedium,
        )
        LazyColumn {
            items(items = themes) { theme ->
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelected?.invoke(theme)
                            }.padding(
                                horizontal = Spacing.m,
                                vertical = Spacing.s,
                            ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.s),
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = theme.toReadableName(),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    theme.toIcon()?.also { icon ->
                        Icon(
                            modifier = Modifier.size(IconSize.s),
                            imageVector = icon,
                            contentDescription = null,
                        )
                    }
                }
            }
        }
    }
}
