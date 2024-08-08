package com.livefast.eattrash.raccoonforfriendica.feature.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getDetailOpener

class ProfileScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val model = getScreenModel<ProfileMviModel>()
        val uiState by model.uiState.collectAsState()
        val topAppBarState = rememberTopAppBarState()
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
        val detailOpener = remember { getDetailOpener() }

        Scaffold(
            topBar = {
                TopAppBar(
                    scrollBehavior = scrollBehavior,
                    title = {
                        Text(
                            modifier = Modifier.padding(horizontal = Spacing.s),
                            text = LocalStrings.current.sectionTitleProfile,
                            style = MaterialTheme.typography.titleMedium,
                        )
                    },
                    actions = {
                        if (uiState.isLogged) {
                            Icon(
                                modifier =
                                    Modifier
                                        .padding(horizontal = Spacing.xs)
                                        .clickable {
                                            model.reduce(ProfileMviModel.Intent.Logout)
                                        },
                                imageVector = Icons.AutoMirrored.Default.Logout,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onBackground,
                            )
                        }
                        Icon(
                            modifier =
                                Modifier
                                    .padding(horizontal = Spacing.xs)
                                    .clickable {
                                        detailOpener.openSettings()
                                    },
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground,
                        )
                    },
                )
            },
            content = { padding ->
                Box(
                    modifier =
                        Modifier
                            .padding(padding)
                            .nestedScroll(scrollBehavior.nestedScrollConnection),
                ) {
                    if (!uiState.isLogged) {
                        Column(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(Spacing.s),
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
            },
        )
    }
}
