package com.livefast.eattrash.raccoonforfriendica.feature.announcements.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.ancillaryTextAlpha
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.ContentBody
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.prettifyDate
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AnnouncementModel

@Composable
internal fun AnnouncementCard(
    announcement: AnnouncementModel,
    modifier: Modifier = Modifier,
    autoloadImages: Boolean = true,
    onOpenUrl: ((String) -> Unit)? = null,
) {
    val ancillaryColor = MaterialTheme.colorScheme.onBackground.copy(ancillaryTextAlpha)

    Column(
        modifier =
            modifier.padding(
                vertical = Spacing.xxs,
                horizontal = Spacing.s,
            ),
        verticalArrangement = Arrangement.spacedBy(Spacing.s),
    ) {
        Row {
            Spacer(Modifier.weight(1f))
            // unread indicator
            if (!announcement.read) {
                Box(
                    modifier =
                        Modifier
                            .size(IconSize.xs)
                            .padding(2.dp)
                            .background(
                                color = MaterialTheme.colorScheme.onBackground,
                                shape = CircleShape,
                            ),
                )
            }
        }

        val body = announcement.content
        if (body.isNotBlank()) {
            ContentBody(
                modifier = Modifier.fillMaxWidth(),
                content = body,
                autoloadImages = autoloadImages,
                emojis = announcement.emojis,
                onOpenUrl = onOpenUrl,
            )
        }

        Row {
            Spacer(Modifier.weight(1f))
            // publish date
            val date = announcement.published
            Text(
                text =
                    buildString {
                        if (!date.isNullOrBlank()) {
                            append(date.prettifyDate())
                        }
                    },
                style = MaterialTheme.typography.bodySmall,
                color = ancillaryColor,
            )
        }
    }
}
