package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.ancillaryTextAlpha
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.utils.compose.getAnimatedDots

@Composable
internal fun TranslationFooter(
    modifier: Modifier = Modifier,
    lang: String? = null,
    provider: String? = null,
    translationLoading: Boolean = false,
    isShowingTranslation: Boolean = false,
) {
    val translationFooterContent =
        when {
            translationLoading ->
                buildString {
                    append(LocalStrings.current.messageLoadingTranslation)
                    append(" ")
                    val animatedPart = getAnimatedDots().padEnd(3, ' ')
                    append(animatedPart)
                }

            isShowingTranslation ->
                buildString {
                    if (lang != null) {
                        append(LocalStrings.current.translatedFrom(lang))
                        if (!provider.isNullOrEmpty()) {
                            append(" (")
                            append(LocalStrings.current.translatedUsing(provider))
                            append(")")
                        }
                    }
                }

            else -> ""
        }
    if (translationFooterContent.isNotEmpty()) {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.End,
        ) {
            Text(
                text = translationFooterContent,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(ancillaryTextAlpha),
            )
        }
    }
}
