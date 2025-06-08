package com.livefast.eattrash.raccoonforfriendica.feature.composer.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.TimelineLayout
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.CornerSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TimelineItem
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryPreviewDialog(
    entry: TimelineEntryModel,
    modifier: Modifier = Modifier,
    autoloadImages: Boolean = true,
    onDismiss: (() -> Unit)? = null,
) {
    BasicAlertDialog(
        modifier = modifier.clip(RoundedCornerShape(CornerSize.xxl)),
        onDismissRequest = {
            onDismiss?.invoke()
        },
    ) {
        Column(
            modifier =
            Modifier
                .background(color = MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp))
                .padding(Spacing.m),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
        ) {
            LazyColumn(
                modifier = Modifier.heightIn(max = 500.dp),
            ) {
                item {
                    TimelineItem(
                        modifier = Modifier.fillMaxWidth(),
                        entry = entry,
                        layout = TimelineLayout.Full,
                        autoloadImages = autoloadImages,
                        actionsEnabled = false,
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.s),
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = {
                        onDismiss?.invoke()
                    },
                ) {
                    Text(text = LocalStrings.current.buttonClose)
                }
            }
        }
    }
}
