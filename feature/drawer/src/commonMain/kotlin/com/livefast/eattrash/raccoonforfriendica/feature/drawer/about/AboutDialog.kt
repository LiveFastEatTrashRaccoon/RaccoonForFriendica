package com.livefast.eattrash.raccoonforfriendica.feature.drawer.about

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.VolunteerActivism
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.CornerSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getDetailOpener
import com.livefast.eattrash.raccoonforfriendica.core.utils.di.getAppInfoRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutDialog(onClose: (() -> Unit)? = null) {
    val uriHandler = LocalUriHandler.current
    val appInfoRepository = remember { getAppInfoRepository() }
    val appInfo by appInfoRepository.appInfo.collectAsState()
    val detailOpener = remember { getDetailOpener() }

    fun handleAction(block: () -> Unit) {
        onClose?.invoke()
        block()
    }

    BasicAlertDialog(
        modifier = Modifier.clip(RoundedCornerShape(CornerSize.xxl)),
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
                        icon = Icons.AutoMirrored.Default.Article,
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
                                detailOpener.openUserFeedback()
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
                                imageVector = Icons.Default.BugReport,
                                contentDescription = null,
                            )
                        }
                    }
                }
                item {
                    AboutItem(
                        icon = Icons.Default.Code,
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
                        icon = Icons.Default.OpenInBrowser,
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
                        icon = Icons.AutoMirrored.Default.Chat,
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
                        icon = Icons.Default.Book,
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
                        icon = Icons.Default.VolunteerActivism,
                        textDecoration = TextDecoration.Underline,
                        onClick = {
                            handleAction {
                                detailOpener.openAcknowledgements()
                            }
                        },
                    )
                }
                item {
                    AboutItem(
                        text = LocalStrings.current.settingsAboutLicences,
                        icon = Icons.Default.Gavel,
                        textDecoration = TextDecoration.Underline,
                        onClick = {
                            handleAction {
                                detailOpener.openLicences()
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
