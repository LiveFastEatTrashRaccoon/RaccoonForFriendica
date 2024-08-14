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
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.Locales
import com.livefast.eattrash.raccoonforfriendica.core.l10n.toLanguageName

@Composable
internal fun LanguageBottomSheet(
    languages: List<String> =
        listOf(
            Locales.EN,
            Locales.IT,
        ),
    onSelected: ((String) -> Unit)? = null,
) {
    Column(
        modifier = Modifier.padding(bottom = Spacing.xl),
        verticalArrangement = Arrangement.spacedBy(Spacing.s),
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            text = LocalStrings.current.settingsItemLanguage,
            style = MaterialTheme.typography.titleMedium,
        )
        LazyColumn {
            items(items = languages) { lang ->
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelected?.invoke(lang)
                            }.padding(
                                horizontal = Spacing.m,
                                vertical = Spacing.s,
                            ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.s),
                ) {
                    Text(
                        text = lang.toLanguageName().orEmpty(),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}
