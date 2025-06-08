package com.livefast.eattrash.raccoonforfriendica.feature.calendar.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.CornerSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.getFormattedDate
import com.livefast.eattrash.raccoonforfriendica.feature.calendar.list.CalendarItem

@Composable
fun CalendarHeader(header: CalendarItem.Header, modifier: Modifier = Modifier) {
    val referenceDate =
        "${header.year}-${header.month.toString().padStart(2, '0')}-01T12:00:00.000Z"
    Box(
        modifier =
        modifier
            .border(
                width = Dp.Hairline,
                color = MaterialTheme.colorScheme.onBackground,
                shape = RoundedCornerShape(CornerSize.xl),
            ).background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(CornerSize.xl),
            ).padding(vertical = Spacing.xs),
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = getFormattedDate(referenceDate, "MMMM YYYY").uppercase(),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
