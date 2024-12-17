package com.livefast.eattrash.raccoonforfriendica.feature.composer.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.getFormattedDate

@Composable
internal fun CreatePostSubHeader(
    date: String? = null,
    characters: Int = 0,
    characterLimit: Int? = null,
) {
    Row(
        modifier =
            Modifier.padding(
                top = Spacing.s,
                start = Spacing.s,
                end = Spacing.s,
            ),
    ) {
        if (date != null) {
            Text(
                text =
                    buildString {
                        append(LocalStrings.current.scheduleDateIndication)
                        append(" ")
                        append(
                            getFormattedDate(
                                iso8601Timestamp = date,
                                format = "dd/MM/yy HH:mm",
                            ),
                        )
                    },
                style = MaterialTheme.typography.labelSmall,
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        if (characterLimit != null) {
            Text(
                text =
                    buildString {
                        append(characters)
                        append("/")
                        append(characterLimit)
                    },
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}
