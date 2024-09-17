package com.livefast.eattrash.raccoonforfriendica.feature.nodeinfo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.CornerSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.ancillaryTextAlpha
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.toWindowInsets
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomImage
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.PlaceholderImage
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.ContentBody
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.ContentTitle
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.GenericPlaceholder
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.SettingsHeader
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserItemPlaceholder
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getDetailOpener
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getNavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.utils.ellipsize
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.RuleModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import kotlinx.coroutines.launch

class NodeInfoScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val model = getScreenModel<NodeInfoMviModel>()
        val uiState by model.uiState.collectAsState()
        val topAppBarState = rememberTopAppBarState()
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
        val navigationCoordinator = remember { getNavigationCoordinator() }
        val uriHandler = LocalUriHandler.current
        val detailOpener = remember { getDetailOpener() }
        val lazyListState = rememberLazyListState()
        val scope = rememberCoroutineScope()

        suspend fun goBackToTop() {
            runCatching {
                lazyListState.scrollToItem(0)
                topAppBarState.heightOffset = 0f
                topAppBarState.contentOffset = 0f
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    modifier = Modifier.clickable { scope.launch { goBackToTop() } },
                    windowInsets = topAppBarState.toWindowInsets(),
                    scrollBehavior = scrollBehavior,
                    title = {
                        Text(
                            text = LocalStrings.current.nodeInfoTitle,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleMedium,
                        )
                    },
                    navigationIcon = {
                        if (navigationCoordinator.canPop.value) {
                            IconButton(
                                onClick = {
                                    navigationCoordinator.pop()
                                },
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                    contentDescription = null,
                                )
                            }
                        }
                    },
                )
            },
            content = { padding ->
                LazyColumn(
                    modifier =
                        Modifier
                            .padding(padding)
                            .fillMaxWidth()
                            .nestedScroll(scrollBehavior.nestedScrollConnection),
                    state = lazyListState,
                    verticalArrangement = Arrangement.spacedBy(Spacing.s),
                ) {
                    val info = uiState.info
                    if (info == null) {
                        item {
                            UserItemPlaceholder(
                                modifier = Modifier.padding(horizontal = Spacing.s).fillMaxWidth(),
                                withRelationshipButton = false,
                            )
                        }
                        item {
                            GenericPlaceholder(
                                height = 60.dp,
                                modifier = Modifier.padding(horizontal = Spacing.s).fillMaxWidth(),
                            )
                            GenericPlaceholder(
                                modifier = Modifier.padding(horizontal = Spacing.s).fillMaxWidth(),
                            )
                        }
                        item {
                            UserItemPlaceholder(
                                modifier = Modifier.padding(horizontal = Spacing.s).fillMaxWidth(),
                                withRelationshipButton = false,
                            )
                        }
                        items(5) {
                            GenericPlaceholder(
                                height = 60.dp,
                                modifier = Modifier.padding(horizontal = Spacing.s).fillMaxWidth(),
                            )
                        }
                    } else {
                        item {
                            HeaderItem(
                                modifier = Modifier.padding(horizontal = Spacing.s),
                                thumbnail = info.thumbnail,
                                uri = info.uri,
                            )
                        }

                        item {
                            SettingsHeader(
                                title = LocalStrings.current.settingsHeaderGeneral,
                                icon = Icons.Default.Info,
                            )
                        }

                        val title = info.title
                        if (title != null) {
                            item {
                                ContentTitle(
                                    modifier = Modifier.padding(horizontal = Spacing.s),
                                    content = title,
                                    onOpenUrl = {
                                        uriHandler.openUri(it)
                                    },
                                )
                            }
                        }

                        val description = info.description
                        if (description != null) {
                            item {
                                ContentBody(
                                    modifier = Modifier.padding(horizontal = Spacing.s),
                                    content = description,
                                    onOpenUrl = {
                                        uriHandler.openUri(it)
                                    },
                                )
                            }
                        }

                        val contact = info.contact
                        if (contact != null) {
                            item {
                                SettingsHeader(
                                    title = LocalStrings.current.nodeInfoSectionContact,
                                    icon = Icons.Default.AlternateEmail,
                                )
                                ContactUserItem(
                                    modifier =
                                        Modifier.padding(
                                            top = Spacing.xs,
                                            start = Spacing.s,
                                            end = Spacing.s,
                                        ),
                                    user = contact,
                                    onClick = {
                                        detailOpener.openUserDetail(contact)
                                    },
                                )
                            }
                        }

                        val rules =
                            uiState.info
                                ?.rules
                                .orEmpty()
                        if (rules.isNotEmpty()) {
                            item {
                                SettingsHeader(
                                    title = LocalStrings.current.nodeInfoSectionRules,
                                    icon = Icons.Default.Shield,
                                )
                            }
                            items(rules) { rule ->
                                RuleItem(
                                    modifier = Modifier.padding(horizontal = 10.dp),
                                    rule = rule,
                                    onOpenUrl = {
                                        uriHandler.openUri(it)
                                    },
                                )
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(Spacing.xxl))
                        }
                    }
                }
            },
        )
    }
}

@Composable
private fun HeaderItem(
    thumbnail: String?,
    uri: String?,
    modifier: Modifier = Modifier,
) {
    val thumbnailSize = IconSize.xl
    val fullColor = MaterialTheme.colorScheme.onBackground

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Spacing.s),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (!thumbnail.isNullOrEmpty()) {
            CustomImage(
                modifier =
                    Modifier
                        .size(thumbnailSize)
                        .clip(RoundedCornerShape(thumbnailSize / 2)),
                url = thumbnail,
                quality = FilterQuality.Low,
                contentScale = ContentScale.FillBounds,
            )
        } else {
            PlaceholderImage(
                size = thumbnailSize,
                title = uri ?: "?",
            )
        }

        Text(
            text = (uri ?: "").uppercase(),
            style = MaterialTheme.typography.bodyLarge,
            color = fullColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun ContactUserItem(
    user: UserModel,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val avatar = user.avatar.orEmpty()
    val avatarSize = IconSize.m
    val fullColor = MaterialTheme.colorScheme.onBackground
    val ancillaryColor = MaterialTheme.colorScheme.onBackground.copy(ancillaryTextAlpha)

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(CornerSize.xl),
        color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication =
                            rememberRipple(
                                color = MaterialTheme.colorScheme.onBackground.copy(0.5f),
                            ),
                    ) {
                        onClick?.invoke()
                    }.padding(
                        horizontal = Spacing.s,
                        vertical = Spacing.s,
                    ),
            horizontalArrangement = Arrangement.spacedBy(Spacing.s),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (avatar.isNotEmpty()) {
                CustomImage(
                    modifier =
                        Modifier
                            .size(avatarSize)
                            .clip(RoundedCornerShape(avatarSize / 2)),
                    url = avatar,
                    quality = FilterQuality.Low,
                    contentScale = ContentScale.FillBounds,
                )
            } else {
                PlaceholderImage(
                    size = avatarSize,
                    title = user.displayName ?: user.handle ?: "?",
                )
            }

            Column(
                modifier = Modifier.weight(1f).padding(vertical = Spacing.s),
                verticalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                val title = (user.displayName ?: user.username ?: "").ellipsize(30)
                val subtitle = user.handle ?: ""
                if (title.isNotBlank()) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = fullColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                if (subtitle.isNotBlank()) {
                    Text(
                        text = (user.handle ?: "").ellipsize(25),
                        style = MaterialTheme.typography.bodySmall,
                        color = ancillaryColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            Icon(
                modifier = Modifier.size(IconSize.s),
                imageVector = Icons.AutoMirrored.Default.ArrowForwardIos,
                contentDescription = null,
            )
        }
    }
}

@Composable
private fun RuleItem(
    rule: RuleModel,
    modifier: Modifier = Modifier,
    onOpenUrl: ((String) -> Unit)? = null,
) {
    ContentBody(
        modifier = modifier,
        content = rule.text,
        onOpenUrl = onOpenUrl,
    )
}
