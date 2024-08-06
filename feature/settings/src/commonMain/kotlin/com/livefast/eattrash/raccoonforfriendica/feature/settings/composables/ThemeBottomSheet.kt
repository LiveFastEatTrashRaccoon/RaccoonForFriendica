package com.livefast.eattrash.raccoonforfriendica.feature.settings.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.UiTheme
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.toReadableName
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings

@Composable
internal fun ThemeBottomSheet(
    themes: List<UiTheme> =
        listOf(
            UiTheme.Light,
            UiTheme.Dark,
            UiTheme.Black,
        ),
    onSelected: ((UiTheme?) -> Unit)? = null,
) {
    Column(
        modifier =
            Modifier.padding(
                start = Spacing.m,
                end = Spacing.m,
                bottom = Spacing.l,
            ),
        verticalArrangement = Arrangement.spacedBy(Spacing.s),
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            text = LocalStrings.current.settingsItemUiTheme,
            style = MaterialTheme.typography.titleMedium,
        )
        LazyColumn {
            items(items = themes) { theme ->
                Text(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = Spacing.s)
                            .clickable {
                                onSelected?.invoke(theme)
                            },
                    text = theme.toReadableName(),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
            item {
                Text(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = Spacing.s)
                            .clickable {
                                onSelected?.invoke(null)
                            },
                    text = LocalStrings.current.systemDefault,
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}
