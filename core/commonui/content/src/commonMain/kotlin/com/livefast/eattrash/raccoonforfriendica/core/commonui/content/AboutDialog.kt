package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.CornerSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.rememberMainRouter
import com.livefast.eattrash.raccoonforfriendica.core.resources.di.rememberCoreResources
import com.livefast.eattrash.raccoonforfriendica.core.utils.di.rememberAppInfoRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutDialog(modifier: Modifier = Modifier, onClose: (() -> Unit)? = null) {
    val uriHandler = LocalUriHandler.current
    val appInfoRepository = rememberAppInfoRepository()
    val appInfo by appInfoRepository.appInfo.collectAsState()
    val mainRouter = rememberMainRouter()
    val coreResources = rememberCoreResources()

    fun handleAction(block: () -> Unit) {
        onClose?.invoke()
        block()
    }

    BasicAlertDialog(
        modifier = modifier.clip(RoundedCornerShape(CornerSize.xxl)),
        onDismissRequest = {
            onClose?.invoke()
        },
    ) {
        Column(
            modifier =
            Modifier
                .background(color = MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp))
                .padding(Spacing.m),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = LocalStrings.current.settingsAbout,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.height(Spacing.s))
            LazyColumn(
                modifier =
                Modifier
                    .padding(vertical = Spacing.s, horizontal = Spacing.m)
                    .heightIn(max = 400.dp),
                verticalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                item {
                    AboutItem(
                        text = LocalStrings.current.settingsAboutAppVersion,
                        value = appInfo?.versionCode.orEmpty(),
                    )
                }
                item {
                    AboutItem(
                        text = LocalStrings.current.settingsAboutChangelog,
                        icon = coreResources.article,
                        textDecoration = TextDecoration.Underline,
                        onClick = {
                            handleAction {
                                uriHandler.openUri(AboutConstants.CHANGELOG_URL)
                            }
                        },
                    )
                }
                item {
                    Button(
                        onClick = {
                            handleAction {
                                mainRouter.openUserFeedback()
                            }
                        },
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(Spacing.s),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                modifier = Modifier.weight(1f),
                                text = LocalStrings.current.settingsAboutReportIssue,
                                style = MaterialTheme.typography.labelLarge,
                            )
                            Icon(
                                imageVector = coreResources.bugReport,
                                contentDescription = null,
                            )
                        }
                    }
                }
                item {
                    AboutItem(
                        icon = coreResources.code,
                        text = LocalStrings.current.settingsAboutViewGithub,
                        textDecoration = TextDecoration.Underline,
                        onClick = {
                            handleAction {
                                uriHandler.openUri(AboutConstants.WEBSITE_URL)
                            }
                        },
                    )
                }
                item {
                    AboutItem(
                        icon = coreResources.openInBrowser,
                        text = LocalStrings.current.settingsAboutViewFriendica,
                        textDecoration = TextDecoration.Underline,
                        onClick = {
                            handleAction {
                                uriHandler.openUri(AboutConstants.GROUP_URL)
                            }
                        },
                    )
                }
                item {
                    AboutItem(
                        icon = coreResources.chatFill,
                        text = LocalStrings.current.settingsAboutMatrix,
                        textDecoration = TextDecoration.Underline,
                        onClick = {
                            handleAction {
                                uriHandler.openUri(AboutConstants.MATRIX_URL)
                            }
                        },
                    )
                }
                item {
                    AboutItem(
                        text = LocalStrings.current.settingsAboutUserManual,
                        icon = coreResources.book,
                        textDecoration = TextDecoration.Underline,
                        onClick = {
                            handleAction {
                                uriHandler.openUri(AboutConstants.MANUAL_URL)
                            }
                        },
                    )
                }
                item {
                    AboutItem(
                        text = LocalStrings.current.settingsAboutAcknowledgements,
                        icon = coreResources.volunteerActivism,
                        textDecoration = TextDecoration.Underline,
                        onClick = {
                            handleAction {
                                mainRouter.openAcknowledgements()
                            }
                        },
                    )
                }
                item {
                    AboutItem(
                        text = LocalStrings.current.settingsAboutLicences,
                        icon = coreResources.gavel,
                        textDecoration = TextDecoration.Underline,
                        onClick = {
                            handleAction {
                                mainRouter.openLicences()
                            }
                        },
                    )
                }
            }
            Button(
                onClick = {
                    onClose?.invoke()
                },
            ) {
                Text(text = LocalStrings.current.buttonClose)
            }
        }
    }
}

@Composable
private fun AboutItem(
    text: String,
    modifier: Modifier = Modifier,
    painter: Painter? = null,
    icon: ImageVector? = null,
    textDecoration: TextDecoration = TextDecoration.None,
    value: String = "",
    onClick: (() -> Unit)? = null,
) {
    Row(
        modifier =
        modifier
            .padding(
                horizontal = Spacing.xs,
                vertical = Spacing.s,
            ).clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) {
                onClick?.invoke()
            },
        horizontalArrangement = Arrangement.spacedBy(Spacing.s),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val imageModifier = Modifier.Companion.size(22.dp)
        if (painter != null) {
            Image(
                modifier = imageModifier,
                painter = painter,
                contentDescription = null,
                colorFilter = ColorFilter.Companion.tint(MaterialTheme.colorScheme.onBackground),
            )
        } else if (icon != null) {
            Image(
                modifier = imageModifier,
                imageVector = icon,
                contentDescription = null,
                colorFilter = ColorFilter.Companion.tint(MaterialTheme.colorScheme.onBackground),
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            textDecoration = textDecoration,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Spacer(modifier = Modifier.weight(1f))

        if (value.isNotEmpty()) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
    }
}

private object AboutConstants {
    const val CHANGELOG_URL =
        "https://github.com/LiveFastEatTrashRaccoon/RaccoonForFriendica/releases/latest"
    const val WEBSITE_URL = "https://github.com/LiveFastEatTrashRaccoon/RaccoonForFriendica"
    const val GROUP_URL = "https://poliverso.org/profile/raccoonforfriendicaapp"
    const val MANUAL_URL =
        "https://livefasteattrashraccoon.github.io/RaccoonForFriendica/manual/main"
    const val MATRIX_URL = "https://matrix.to/#/#raccoonforfriendicaapp:matrix.org"
}
