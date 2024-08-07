package com.livefast.eattrash.raccoonforfriendica.feature.settings.composables

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
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.UiFontFamily
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.toReadableName
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings

@Composable
internal fun FontFamilyBottomSheet(
    fontFamilies: List<UiFontFamily> =
        listOf(
            UiFontFamily.Exo2,
            UiFontFamily.NotoSans,
            UiFontFamily.Default,
        ),
    onSelected: ((UiFontFamily) -> Unit)? = null,
) {
    Column(
        modifier =
            Modifier.padding(
                start = Spacing.m,
                end = Spacing.m,
                bottom = Spacing.xl,
            ),
        verticalArrangement = Arrangement.spacedBy(Spacing.s),
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            text = LocalStrings.current.settingsItemFontFamily,
            style = MaterialTheme.typography.titleMedium,
        )
        LazyColumn {
            items(items = fontFamilies) { family ->
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelected?.invoke(family)
                            }.padding(vertical = Spacing.s),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.s),
                ) {
                    Text(
                        text = family.toReadableName(),
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        }
    }
}
