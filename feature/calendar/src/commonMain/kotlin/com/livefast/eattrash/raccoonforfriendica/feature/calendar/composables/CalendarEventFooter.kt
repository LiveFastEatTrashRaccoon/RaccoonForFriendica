package com.livefast.eattrash.raccoonforfriendica.feature.calendar.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.ancillaryTextAlpha
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.getFormattedDate

@Composable
fun CalendarEventFooter(
    modifier: Modifier = Modifier,
    startDate: String? = null,
    endDate: String? = null,
    place: String? = null,
) {
    val ancillaryColor = MaterialTheme.colorScheme.onBackground.copy(ancillaryTextAlpha)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
    ) {
        val startDateString =
            startDate?.let {
                getFormattedDate(
                    iso8601Timestamp = it,
                    format = "dd/MM/yy HH:mm",
                )
            }
        val endDateString =
            endDate?.let {
                getFormattedDate(
                    iso8601Timestamp = it,
                    format = "dd/MM/yy HH:mm",
                )
            }
        Text(
            text =
                buildString {
                    append(startDateString)
                    if (!endDateString.isNullOrEmpty()) {
                        append(" – ")
                        append(endDateString)
                    }
                },
            style = MaterialTheme.typography.bodyMedium,
            color = ancillaryColor,
        )

        if (!place.isNullOrEmpty()) {
            Text(
                text = place,
                style = MaterialTheme.typography.bodyMedium,
                color = ancillaryColor,
            )
        }
    }
}
