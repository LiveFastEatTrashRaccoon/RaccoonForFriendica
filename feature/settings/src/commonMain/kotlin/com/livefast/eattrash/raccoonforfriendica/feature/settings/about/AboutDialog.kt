package com.livefast.eattrash.raccoonforfriendica.feature.settings.about

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.utils.di.getAppInfoRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.di.getOpenUrlUseCase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutDialog(onClose: (() -> Unit)? = null) {
    val uriHandler = LocalUriHandler.current
    val openUrl = remember { getOpenUrlUseCase(uriHandler) }
    val appInfoRepository = remember { getAppInfoRepository() }
    val appInfo by appInfoRepository.appInfo.collectAsState()

    BasicAlertDialog(
        onDismissRequest = {
            onClose?.invoke()
        },
    ) {
        Column(
            modifier =
                Modifier
                    .background(color = MaterialTheme.colorScheme.surface)
                    .padding(vertical = Spacing.s),
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
                            openUrl(AboutConstants.CHANGELOG_URL)
                        },
                    )
                }
                item {
                    Button(
                        onClick = {
                            openUrl(AboutConstants.REPORT_URL)
                        },
                    ) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = LocalStrings.current.settingsAboutReportIssue,
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                }
                item {
                    AboutItem(
                        icon = Icons.Default.Code,
                        text = LocalStrings.current.settingsAboutViewGithub,
                        textDecoration = TextDecoration.Underline,
                        onClick = {
                            openUrl(AboutConstants.WEBSITE_URL)
                        },
                    )
                }
                item {
                    AboutItem(
                        icon = Icons.Default.OpenInBrowser,
                        text = LocalStrings.current.settingsAboutViewFriendica,
                        textDecoration = TextDecoration.Underline,
                        onClick = {
                            openUrl(AboutConstants.GROUP_URL)
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
