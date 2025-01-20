package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
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
    onShowOriginal: (() -> Unit)? = null,
) {
    val translationFooterContent =
        buildAnnotatedString {
            when {
                translationLoading -> {
                    append(LocalStrings.current.messageLoadingTranslation)
                    append(" ")
                    val animatedPart = getAnimatedDots().padEnd(3, ' ')
                    append(animatedPart)
                }

                isShowingTranslation -> {
                    if (lang != null) {
                        append(LocalStrings.current.translatedFrom(lang.uppercase()))
                        if (!provider.isNullOrEmpty()) {
                            append(" (")
                            append(LocalStrings.current.translatedUsing(provider))
                            append(")")
                        }
                        if (onShowOriginal != null) {
                            append(" ")
                            withLink(
                                LinkAnnotation.Clickable(
                                    tag = "action-show-original",
                                    linkInteractionListener = {
                                        onShowOriginal()
                                    },
                                    styles =
                                        TextLinkStyles(
                                            style =
                                                SpanStyle(
                                                    color = MaterialTheme.colorScheme.primary,
                                                    textDecoration = TextDecoration.Underline,
                                                ),
                                        ),
                                ),
                            ) {
                                append(LocalStrings.current.actionShowOriginal)
                            }
                        }
                    }
                }

                else -> Unit
            }
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
