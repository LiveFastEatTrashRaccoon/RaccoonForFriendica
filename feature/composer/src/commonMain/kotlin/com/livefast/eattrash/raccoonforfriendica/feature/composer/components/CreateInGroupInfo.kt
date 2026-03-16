package com.livefast.eattrash.raccoonforfriendica.feature.composer.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.ancillaryTextAlpha
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.resources.di.getCoreResources

@Composable
internal fun CreateInGroupInfo(username: String, modifier: Modifier = Modifier) {
    val coreResources = remember { getCoreResources() }
    val fullColor = MaterialTheme.colorScheme.onBackground
    val ancillaryColor = MaterialTheme.colorScheme.onBackground.copy(ancillaryTextAlpha)
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.s),
    ) {
        Icon(
            modifier = Modifier.size(IconSize.s),
            imageVector = coreResources.postAdd,
            contentDescription = null,
            tint = ancillaryColor,
        )
        Text(
            text =
            buildAnnotatedString {
                append(LocalStrings.current.actionCreateThreadInGroup)
                append(" ")
                withStyle(SpanStyle(color = fullColor)) {
                    append(username)
                }
            },
            style = MaterialTheme.typography.bodyMedium,
            color = ancillaryColor,
        )
    }
}
