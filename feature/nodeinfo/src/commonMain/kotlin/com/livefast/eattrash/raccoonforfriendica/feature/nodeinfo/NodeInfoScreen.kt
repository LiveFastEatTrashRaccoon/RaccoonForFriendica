package com.livefast.eattrash.raccoonforfriendica.feature.nodeinfo

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.CornerSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.ancillaryTextAlpha
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.toWindowInsets
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomImage
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.PlaceholderImage
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.ChangeInstanceDialog
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.ContentBody
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.ContentTitle
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.GenericPlaceholder
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.SettingsHeader
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.SettingsRow
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TextWithCustomEmojis
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserItemPlaceholder
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.rememberMainRouter
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.rememberNavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.resources.LocalResources
import com.livefast.eattrash.raccoonforfriendica.core.utils.compose.isWidthSizeClassBelow
import com.livefast.eattrash.raccoonforfriendica.core.utils.compose.optimizedForLargeScreens
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.RuleModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@Composable
fun NodeInfoScreen(model: NodeInfoMviModel, modifier: Modifier = Modifier) {
    val uiState by model.uiState.collectAsState()
    val navigationCoordinator = rememberNavigationCoordinator()
    var changeInstanceDialogOpened by remember { mutableStateOf(false) }
    val successMessage = LocalStrings.current.messageSuccess

    LaunchedEffect(model) {
        model.effects
            .onEach { event ->
                when (event) {
                    NodeInfoMviModel.Effect.AnonymousChangeNodeSuccess -> {
                        changeInstanceDialogOpened = false
                        navigationCoordinator.showGlobalMessage(successMessage)
                    }
                }
            }.launchIn(this)
    }

    NodeInfoScreenScaffold(
        modifier = modifier,
        uiState = uiState,
        onOpenChangeInstance = {
            changeInstanceDialogOpened = true
        },
    )

    if (changeInstanceDialogOpened) {
        ChangeInstanceDialog(
            nodeName = uiState.anonymousChangeNodeName,
            validationInProgress = uiState.anonymousChangeNodeValidationInProgress,
            validationError = uiState.anonymousChangeNodeNameError,
            onClose = {
                changeInstanceDialogOpened = false
            },
            onNodeChange = { value ->
                model.reduce(NodeInfoMviModel.Intent.SetAnonymousChangeNode(value))
            },
            onSubmit = {
                model.reduce(NodeInfoMviModel.Intent.SubmitAnonymousChangeNode)
            },
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
@VisibleForTesting
fun NodeInfoScreenScaffold(
    uiState: NodeInfoMviModel.State,
    modifier: Modifier = Modifier,
    onOpenChangeInstance: (() -> Unit)? = null,
) {
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
    val navigationCoordinator = rememberNavigationCoordinator()
    val uriHandler = LocalUriHandler.current
    val mainRouter = rememberMainRouter()
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
        modifier = modifier,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                modifier = Modifier.clickable { scope.launch { goBackToTop() } },
                windowInsets = topAppBarState.toWindowInsets().optimizedForLargeScreens(),
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
                    if (navigationCoordinator.canPop.value && isWidthSizeClassBelow(WindowWidthSizeClass.Expanded)) {
                        IconButton(
                            onClick = {
                                navigationCoordinator.pop()
                            },
                        ) {
                            Icon(
                                imageVector = LocalResources.current.arrowBack,
                                contentDescription = LocalStrings.current.actionGoBack,
                            )
                        }
                    }
                },
                actions = {
                    if (!uiState.isLogged) {
                        IconButton(
                            onClick = {
                                onOpenChangeInstance?.invoke()
                            },
                        ) {
                            Icon(
                                imageVector = LocalResources.current.changeCircle,
                                contentDescription = LocalStrings.current.changeNodeDialogTitle,
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
                    .testTag(NodeInfoTestTags.COLUMN)
                    .padding(padding)
                    .fillMaxWidth()
                    .then(
                        if (uiState.hideNavigationBarWhileScrolling) {
                            Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
                        } else {
                            Modifier
                        },
                    ),
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
                            autoloadImages = uiState.autoloadImages,
                        )
                    }

                    item {
                        SettingsHeader(
                            title = LocalStrings.current.settingsHeaderGeneral,
                            icon = LocalResources.current.info,
                        )
                    }

                    val title = info.title
                    if (title != null) {
                        item {
                            ContentTitle(
                                modifier = Modifier.padding(horizontal = Spacing.m),
                                content = title,
                                autoloadImages = uiState.autoloadImages,
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
                                modifier = Modifier.padding(horizontal = Spacing.m),
                                content = description,
                                autoloadImages = uiState.autoloadImages,
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
                                icon = LocalResources.current.alternateEmail,
                            )
                            ContactUserItem(
                                modifier =
                                Modifier.padding(
                                    top = Spacing.xs,
                                    start = Spacing.s,
                                    end = Spacing.s,
                                ),
                                user = contact,
                                autoloadImages = uiState.autoloadImages,
                                onClick = {
                                    mainRouter.openUserDetail(contact)
                                },
                            )
                        }
                    }

                    val rules = uiState.info.rules
                    if (rules.isNotEmpty()) {
                        item {
                            SettingsHeader(
                                title = LocalStrings.current.nodeInfoSectionRules,
                                icon = LocalResources.current.shield,
                            )
                        }
                        items(rules) { rule ->
                            RuleItem(
                                modifier = Modifier.padding(horizontal = Spacing.m),
                                rule = rule,
                                onOpenUrl = {
                                    uriHandler.openUri(it)
                                },
                            )
                        }
                    }

                    item {
                        SettingsHeader(
                            title = LocalStrings.current.itemOther,
                            icon = LocalResources.current.api,
                        )
                    }

                    item {
                        SettingsRow(
                            title = LocalStrings.current.settingsAboutAppVersion,
                            value =
                            uiState.info.version
                                ?: LocalStrings.current.shortUnavailable,
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(Spacing.xxl))
                    }
                }
            }
        },
    )
}

@Composable
private fun HeaderItem(
    thumbnail: String?,
    uri: String?,
    modifier: Modifier = Modifier,
    autoloadImages: Boolean = true,
) {
    val thumbnailSize = IconSize.xl
    val fullColor = MaterialTheme.colorScheme.onBackground

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Spacing.s),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (!thumbnail.isNullOrEmpty() && autoloadImages) {
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
    autoloadImages: Boolean = true,
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
                .clickable {
                    onClick?.invoke()
                }.padding(
                    start = Spacing.m,
                    end = Spacing.s,
                    top = Spacing.s,
                    bottom = Spacing.s,
                ),
            horizontalArrangement = Arrangement.spacedBy(Spacing.m),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (avatar.isNotEmpty() && autoloadImages) {
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
                val title = user.displayName ?: user.username ?: ""
                val subtitle = user.handle ?: ""
                if (title.isNotBlank()) {
                    TextWithCustomEmojis(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium,
                        emojis = user.emojis,
                        color = fullColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        autoloadImages = autoloadImages,
                    )
                }
                if (subtitle.isNotBlank()) {
                    Text(
                        text = user.handle ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = ancillaryColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            Icon(
                modifier = Modifier.size(IconSize.s),
                imageVector = LocalResources.current.chevronForward,
                contentDescription = LocalStrings.current.actionOpenDetail,
            )
        }
    }
}

@Composable
private fun RuleItem(rule: RuleModel, modifier: Modifier = Modifier, onOpenUrl: ((String) -> Unit)? = null) {
    ContentBody(
        modifier = modifier,
        content = rule.text,
        onOpenUrl = onOpenUrl,
    )
}

internal object NodeInfoTestTags {
    const val COLUMN = "column"
}
