package com.livefast.eattrash.raccoonforfriendica.feature.profile.anonymous

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings

internal object AnonymousScreen : Tab {
    override val options: TabOptions
        @Composable get() =
            TabOptions(
                index = 0u,
                title = "",
            )

    @Composable
    override fun Content() {
        val model = getScreenModel<AnonymousMviModel>()

        Column(
            modifier = Modifier.padding(Spacing.s).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                modifier = Modifier.fillMaxWidth().padding(top = Spacing.m),
                text = LocalStrings.current.messageUserUnlogged,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
            )

            Spacer(modifier = Modifier.height(Spacing.xl))

            Button(
                onClick = {
                    model.reduce(AnonymousMviModel.Intent.StartOauth2Flow)
                },
            ) {
                Text(LocalStrings.current.messageLoginOAuth)
            }

            Spacer(modifier = Modifier.height(Spacing.m))
            Text(
                text = LocalStrings.current.or,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.height(Spacing.m))

            // legacy login
            Text(
                modifier =
                    Modifier.clickable {
                        model.reduce(AnonymousMviModel.Intent.StartLegacyFlow)
                    },
                text = LocalStrings.current.messageLoginLegacy,
                style = MaterialTheme.typography.bodyMedium,
                textDecoration = TextDecoration.Underline,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}
