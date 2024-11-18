package com.livefast.eattrash.raccoonforfriendica.feature.composer.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.ancillaryTextAlpha
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.utils.compose.getAnimatedDots
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel

@Composable
internal fun MentionsBar(
    modifier: Modifier = Modifier,
    suggestions: List<UserModel> = emptyList(),
    loading: Boolean = false,
    onSelected: ((UserModel) -> Unit)? = null,
) {
    val fullColor = MaterialTheme.colorScheme.onSurfaceVariant
    val ancillaryColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(ancillaryTextAlpha)
    Row(
        modifier =
            modifier
                .height(38.dp)
                .padding(horizontal = Spacing.xxs)
                .horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val users = suggestions.filter { !it.handle.isNullOrBlank() }
        val animatingPart = getAnimatedDots()
        if (users.isEmpty()) {
            if (loading) {
                Text(
                    modifier = Modifier.padding(Spacing.s),
                    text =
                        buildString {
                            append(LocalStrings.current.messageLoadingUsers)
                            append(animatingPart)
                        },
                    style = MaterialTheme.typography.labelMedium,
                    color = ancillaryColor,
                )
            }
        } else {
            for (user in users) {
                Text(
                    modifier =
                        Modifier
                            .clickable {
                                onSelected?.invoke(user)
                            }.padding(Spacing.s),
                    text = user.handle.orEmpty(),
                    style = MaterialTheme.typography.labelMedium,
                    color = fullColor,
                )
            }
            if (loading) {
                Text(
                    modifier = Modifier.padding(Spacing.s),
                    text = animatingPart,
                    style = MaterialTheme.typography.labelMedium,
                    color = fullColor,
                )
            }
        }
    }
}
