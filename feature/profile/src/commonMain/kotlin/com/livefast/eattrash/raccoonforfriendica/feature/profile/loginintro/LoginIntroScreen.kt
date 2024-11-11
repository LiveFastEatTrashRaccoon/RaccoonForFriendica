package com.livefast.eattrash.raccoonforfriendica.feature.profile.loginintro

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.CornerSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.resources.di.getCoreResources
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.LoginType

internal class LoginIntroScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val model = getScreenModel<LoginIntroMviModel>()
        val uriHandler = LocalUriHandler.current
        val fullColor = MaterialTheme.colorScheme.onBackground
        val resources = remember { getCoreResources() }
        var moreInfoBottomSheetOpened by remember { mutableStateOf(false) }

        Column(
            modifier =
                Modifier
                    .padding(Spacing.s)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Spacing.s),
        ) {
            Text(
                modifier =
                    Modifier.fillMaxWidth().padding(top = Spacing.m),
                text = LocalStrings.current.loginTitle,
                style = MaterialTheme.typography.titleLarge,
                color = fullColor,
            )
            Text(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            top = Spacing.s,
                            start = Spacing.xs,
                            end = Spacing.xs,
                        ),
                text = LocalStrings.current.loginSubtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = fullColor,
            )
            PlatformLink(
                title = LocalStrings.current.moreInfo,
                onClick = {
                    moreInfoBottomSheetOpened = true
                },
            )

            PlatformHeader(
                modifier = Modifier.padding(top = Spacing.m),
                title = LocalStrings.current.loginFriendicaHeader,
                painter = resources.friendicaLogo,
                onInfoClicked = {
                    uriHandler.openUri(LoginIntroLinks.ABOUT_FRIENDICA)
                },
            )
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    model.reduce(LoginIntroMviModel.Intent.StartOauth2Flow(LoginType.Friendica))
                },
            ) {
                Text(
                    text =
                        buildString {
                            append(LocalStrings.current.buttonLogin)
                        },
                )
            }
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    model.reduce(LoginIntroMviModel.Intent.StartLegacyFlow)
                },
            ) {
                Text(
                    text =
                        buildString {
                            append(LocalStrings.current.buttonLogin)
                            append(" (")
                            append(LocalStrings.current.loginMethodBasic)
                            append(")")
                        },
                )
            }
            PlatformLink(
                title = LocalStrings.current.helpMeChooseAnInstance,
                onClick = {
                    uriHandler.openUri(LoginIntroLinks.FRIENDICA_INSTANCE_HELP)
                },
            )

            HorizontalDivider(modifier = Modifier.padding(top = Spacing.s))

            PlatformHeader(
                modifier = Modifier.padding(top = Spacing.xs),
                title = LocalStrings.current.loginMastodonHeader,
                painter = resources.mastodonLogo,
                onInfoClicked = {
                    uriHandler.openUri(LoginIntroLinks.ABOUT_MASTODON)
                },
            )
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    model.reduce(
                        LoginIntroMviModel.Intent.StartOauth2Flow(LoginType.Mastodon),
                    )
                },
            ) {
                Text(text = LocalStrings.current.buttonLogin)
            }
            PlatformLink(
                title = LocalStrings.current.helpMeChooseAnInstance,
                onClick = {
                    uriHandler.openUri(LoginIntroLinks.MASTODON_INSTANCE_HELP)
                },
            )
        }

        if (moreInfoBottomSheetOpened) {
            ModalBottomSheet(
                onDismissRequest = {
                    moreInfoBottomSheetOpened = false
                },
            ) {
                Column(
                    modifier = Modifier.padding(bottom = Spacing.xl),
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        text = LocalStrings.current.buttonLogin,
                        style = MaterialTheme.typography.titleMedium,
                        color = fullColor,
                    )
                    Spacer(modifier = Modifier.height(Spacing.s))
                    Text(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.m),
                        text = LocalStrings.current.loginMoreInfoBottomSheetContent,
                        style = MaterialTheme.typography.bodyMedium,
                        color = fullColor,
                    )
                }
            }
        }
    }
}

@Composable
private fun PlatformHeader(
    title: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    painter: Painter? = null,
    onInfoClicked: (() -> Unit)? = null,
) {
    val fullColor = MaterialTheme.colorScheme.onBackground

    Row(
        modifier = modifier.padding(horizontal = Spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.s),
    ) {
        val imageSize = IconSize.m
        if (icon != null) {
            Icon(
                modifier = Modifier.size(imageSize),
                imageVector = icon,
                contentDescription = null,
                tint = fullColor,
            )
        } else if (painter != null) {
            Image(
                modifier = Modifier.size(imageSize).padding(1.5.dp),
                painter = painter,
                contentDescription = null,
            )
        }
        Text(
            modifier = Modifier.weight(1f),
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = fullColor,
        )
        if (onInfoClicked != null) {
            Icon(
                modifier =
                    Modifier
                        .padding(end = Spacing.xs)
                        .clip(CircleShape)
                        .clickable {
                            onInfoClicked()
                        },
                imageVector = Icons.Default.Info,
                contentDescription = null,
            )
        }
    }
}

@Composable
private fun PlatformLink(
    title: String,
    modifier: Modifier = Modifier,
    underline: Boolean = true,
    onClick: (() -> Unit)? = null,
) {
    Row(
        modifier =
            modifier
                .padding(start = Spacing.xs)
                .clip(RoundedCornerShape(CornerSize.l))
                .then(
                    if (onClick != null) {
                        Modifier.clickable {
                            onClick.invoke()
                        }
                    } else {
                        Modifier
                    },
                ).padding(horizontal = Spacing.xs, vertical = Spacing.xxs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            textDecoration = if (underline) TextDecoration.Underline else TextDecoration.None,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}
