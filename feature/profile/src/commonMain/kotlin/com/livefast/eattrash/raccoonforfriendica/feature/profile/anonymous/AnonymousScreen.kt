package com.livefast.eattrash.raccoonforfriendica.feature.profile.anonymous

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getDetailOpener

internal class AnonymousScreen : Screen {
    @Composable
    override fun Content() {
        val detailOpener = remember { getDetailOpener() }

        Column(
            modifier = Modifier.padding(Spacing.s),
        ) {
            Text(
                modifier = Modifier.padding(horizontal = Spacing.m),
                text = LocalStrings.current.messageUserUnlogged,
                style = MaterialTheme.typography.titleMedium,
            )

            Spacer(modifier = Modifier.height(Spacing.xl))

            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = {
                    detailOpener.openLogin()
                },
            ) {
                Text(LocalStrings.current.loginTitle)
            }
        }
    }
}
