package com.livefast.eattrash.raccoonforfriendica.feaure.search

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.toWindowInsets
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.ListLoadingIndicator
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.SearchField
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.SectionSelector
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.FollowHashtagItem
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.GenericPlaceholder
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.OptionId
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TimelineItem
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TimelineItemPlaceholder
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserItem
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserItemPlaceholder
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.toOption
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getDetailOpener
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getNavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.utils.di.getShareHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.ExploreItemModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.RelationshipStatusNextAction
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.safeKey
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.di.getOpenUrlUseCase
import com.livefast.eattrash.raccoonforfriendica.feaure.search.data.SearchSection
import com.livefast.eattrash.raccoonforfriendica.feaure.search.data.toInt
import com.livefast.eattrash.raccoonforfriendica.feaure.search.data.toReadableName
import com.livefast.eattrash.raccoonforfriendica.feaure.search.data.toSearchSection
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class SearchScreen : Screen {
    @OptIn(
        ExperimentalMaterial3Api::class,
        ExperimentalMaterialApi::class,
        ExperimentalFoundationApi::class,
    )
    @Composable
    override fun Content() {
        val model = getScreenModel<SearchMviModel>()
        val uiState by model.uiState.collectAsState()
        val navigationCoordinator = remember { getNavigationCoordinator() }
        val topAppBarState = rememberTopAppBarState()
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
        val uriHandler = LocalUriHandler.current
        val openUrl = remember { getOpenUrlUseCase(uriHandler) }
        val detailOpener = remember { getDetailOpener() }
        val lazyListState = rememberLazyListState()
        val scope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }
        val shareHelper = remember { getShareHelper() }
        val copyToClipboardSuccess = LocalStrings.current.messageTextCopiedToClipboard
        val clipboardManager = LocalClipboardManager.current
        var confirmUnfollowDialogUserId by remember { mutableStateOf<String?>(null) }
        var confirmDeleteFollowRequestDialogUserId by remember { mutableStateOf<String?>(null) }
        var confirmUnfollowHashtagName by remember { mutableStateOf<String?>(null) }
        var confirmDeleteEntryId by remember { mutableStateOf<String?>(null) }
        var confirmMuteEntry by remember { mutableStateOf<TimelineEntryModel?>(null) }
        var confirmBlockEntry by remember { mutableStateOf<TimelineEntryModel?>(null) }

        suspend fun goBackToTop() {
            runCatching {
                lazyListState.scrollToItem(0)
                topAppBarState.heightOffset = 0f
                topAppBarState.contentOffset = 0f
            }
        }

        LaunchedEffect(model) {
            model.effects
                .onEach { event ->
                    when (event) {
                        SearchMviModel.Effect.BackToTop -> goBackToTop()
                    }
                }.launchIn(this)
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    modifier = Modifier.clickable { scope.launch { goBackToTop() } },
                    windowInsets = topAppBarState.toWindowInsets(),
                    scrollBehavior = scrollBehavior,
                    title = {
                        SearchField(
                            value = uiState.query,
                            hint = LocalStrings.current.searchPlaceholder,
                            onClear = {
                                model.reduce(SearchMviModel.Intent.SetSearch(""))
                            },
                            onValueChange = {
                                model.reduce(SearchMviModel.Intent.SetSearch(it))
                            },
                        )
                    },
                    navigationIcon = {
                        if (navigationCoordinator.canPop.value) {
                            Image(
                                modifier =
                                    Modifier.clickable {
                                        navigationCoordinator.pop()
                                    },
                                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
                            )
                        }
                    },
                )
            },
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState,
                ) { data ->
                    Snackbar(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        snackbarData = data,
                    )
                }
            },
        ) { padding ->
            val pullRefreshState =
                rememberPullRefreshState(
                    refreshing = uiState.refreshing,
                    onRefresh = {
                        model.reduce(SearchMviModel.Intent.Refresh)
                    },
                )
            Box(
                modifier =
                    Modifier
                        .padding(padding)
                        .fillMaxWidth()
                        .nestedScroll(scrollBehavior.nestedScrollConnection)
                        .pullRefresh(pullRefreshState),
            ) {
                LazyColumn(
                    state = lazyListState,
                ) {
                    stickyHeader {
                        SectionSelector(
                            modifier = Modifier.padding(bottom = Spacing.s),
                            titles =
                                listOf(
                                    SearchSection.Hashtags.toReadableName(),
                                    SearchSection.Posts.toReadableName(),
                                    SearchSection.Users.toReadableName(),
                                ),
                            currentSection = uiState.section.toInt(),
                            onSectionSelected = {
                                val section = it.toSearchSection()
                                model.reduce(
                                    SearchMviModel.Intent.ChangeSection(section),
                                )
                            },
                        )
                    }

                    if (uiState.initial) {
                        items(20) {
                            val modifier = Modifier.fillMaxWidth()
                            when (uiState.section) {
                                SearchSection.Hashtags -> {
                                    GenericPlaceholder(modifier)
                                    Spacer(modifier = Modifier.height(Spacing.interItem))
                                }

                                SearchSection.Posts -> {
                                    TimelineItemPlaceholder(modifier)
                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = Spacing.s),
                                    )
                                }

                                SearchSection.Users -> {
                                    UserItemPlaceholder(modifier)
                                    Spacer(modifier = Modifier.height(Spacing.interItem))
                                }
                            }
                        }
                    }

                    if (!uiState.initial && !uiState.refreshing && !uiState.loading && uiState.items.isEmpty()) {
                        item {
                            Text(
                                modifier = Modifier.fillMaxWidth().padding(top = Spacing.m),
                                text = LocalStrings.current.messageSearchInitialEmpty,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                    }

                    itemsIndexed(
                        items = uiState.items,
                        key = { _, e ->
                            when (e) {
                                is ExploreItemModel.Entry -> e.entry.safeKey
                                else -> e.id
                            }
                        },
                    ) { idx, item ->
                        when (item) {
                            is ExploreItemModel.Entry -> {
                                TimelineItem(
                                    entry = item.entry,
                                    blurNsfw = uiState.blurNsfw,
                                    onClick = { e ->
                                        detailOpener.openEntryDetail(e.id)
                                    },
                                    onOpenUrl = { url ->
                                        openUrl(url)
                                    },
                                    onOpenUser = {
                                        detailOpener.openUserDetail(it.id)
                                    },
                                    onOpenImage = { imageUrl ->
                                        detailOpener.openImageDetail(imageUrl)
                                    },
                                    onReblog = { e ->
                                        model.reduce(SearchMviModel.Intent.ToggleReblog(e))
                                    },
                                    onBookmark = { e ->
                                        model.reduce(SearchMviModel.Intent.ToggleBookmark(e))
                                    },
                                    onFavorite = { e ->
                                        model.reduce(SearchMviModel.Intent.ToggleFavorite(e))
                                    },
                                    onReply = { e ->
                                        detailOpener.openComposer(
                                            inReplyToId = e.id,
                                            inReplyToHandle = e.creator?.handle,
                                            inReplyToUsername =
                                                e.creator?.let {
                                                    it.displayName ?: it.username
                                                },
                                        )
                                    },
                                    options =
                                        buildList {
                                            if (!item.entry.url.isNullOrBlank()) {
                                                this += OptionId.Share.toOption()
                                                this += OptionId.CopyUrl.toOption()
                                            }
                                            val currentUserId = uiState.currentUserId
                                            val creatorId =
                                                item.entry.reblog
                                                    ?.creator
                                                    ?.id
                                                    ?: item.entry.creator?.id
                                            if (currentUserId == creatorId) {
                                                this += OptionId.Edit.toOption()
                                                this += OptionId.Delete.toOption()
                                                if (item.entry.reblog == null) {
                                                    if (item.entry.pinned) {
                                                        this += OptionId.Unpin.toOption()
                                                    } else {
                                                        this += OptionId.Pin.toOption()
                                                    }
                                                }
                                            } else if (currentUserId != null) {
                                                this += OptionId.Mute.toOption()
                                                this += OptionId.Block.toOption()
                                            }
                                        },
                                    onOptionSelected = { optionId ->
                                        when (optionId) {
                                            OptionId.Share -> {
                                                val urlString = item.entry.url.orEmpty()
                                                shareHelper.share(urlString)
                                            }

                                            OptionId.CopyUrl -> {
                                                val urlString = item.entry.url.orEmpty()
                                                clipboardManager.setText(AnnotatedString(urlString))
                                                scope.launch {
                                                    snackbarHostState.showSnackbar(copyToClipboardSuccess)
                                                }
                                            }

                                            OptionId.Edit -> {
                                                (
                                                    item.entry.reblog
                                                        ?: item.entry
                                                ).also { entryToEdit ->
                                                    detailOpener.openComposer(
                                                        inReplyToId = entryToEdit.inReplyTo?.id,
                                                        inReplyToHandle = entryToEdit.inReplyTo?.creator?.handle,
                                                        inReplyToUsername = entryToEdit.inReplyTo?.creator?.username,
                                                        editedPostId = entryToEdit.id,
                                                    )
                                                }
                                            }

                                            OptionId.Delete -> confirmDeleteEntryId = item.entry.id
                                            OptionId.Mute -> confirmMuteEntry = item.entry
                                            OptionId.Block -> confirmBlockEntry = item.entry
                                            OptionId.Pin, OptionId.Unpin ->
                                                model.reduce(
                                                    SearchMviModel.Intent.TogglePin(item.entry),
                                                )
                                            else -> Unit
                                        }
                                    },
                                )
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = Spacing.s),
                                )
                            }

                            is ExploreItemModel.HashTag -> {
                                FollowHashtagItem(
                                    hashtag = item.hashtag,
                                    onOpen = {
                                        detailOpener.openHashtag(it)
                                    },
                                    onToggleFollow = { newFollow ->
                                        if (newFollow) {
                                            model.reduce(
                                                SearchMviModel.Intent.ToggleTagFollow(
                                                    item.hashtag.name,
                                                    newFollow,
                                                ),
                                            )
                                        } else {
                                            confirmUnfollowHashtagName = item.hashtag.name
                                        }
                                    },
                                )
                                Spacer(modifier = Modifier.height(Spacing.interItem))
                            }

                            is ExploreItemModel.User -> {
                                UserItem(
                                    user = item.user,
                                    onClick = {
                                        detailOpener.openUserDetail(item.user.id)
                                    },
                                    onRelationshipClicked = { nextAction ->
                                        when (nextAction) {
                                            RelationshipStatusNextAction.AcceptRequest -> {
                                                model.reduce(
                                                    SearchMviModel.Intent.AcceptFollowRequest(
                                                        item.user.id,
                                                    ),
                                                )
                                            }

                                            RelationshipStatusNextAction.ConfirmUnfollow -> {
                                                confirmUnfollowDialogUserId = item.user.id
                                            }

                                            RelationshipStatusNextAction.ConfirmDeleteFollowRequest -> {
                                                confirmDeleteFollowRequestDialogUserId =
                                                    item.user.id
                                            }

                                            RelationshipStatusNextAction.Follow -> {
                                                model.reduce(SearchMviModel.Intent.Follow(item.user.id))
                                            }

                                            RelationshipStatusNextAction.Unfollow -> {
                                                model.reduce(SearchMviModel.Intent.Unfollow(item.user.id))
                                            }
                                        }
                                    },
                                )
                                Spacer(modifier = Modifier.height(Spacing.interItem))
                            }

                            else -> Unit
                        }

                        val canFetchMore =
                            !uiState.initial && !uiState.loading && uiState.canFetchMore
                        if (idx == uiState.items.lastIndex - 5 && canFetchMore) {
                            model.reduce(SearchMviModel.Intent.LoadNextPage)
                        }
                    }

                    item {
                        if (uiState.loading && !uiState.refreshing && uiState.canFetchMore) {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center,
                            ) {
                                ListLoadingIndicator()
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(Spacing.xxxl))
                    }
                }

                PullRefreshIndicator(
                    refreshing = uiState.refreshing,
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter),
                    backgroundColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground,
                )
            }
        }

        if (confirmUnfollowDialogUserId != null) {
            AlertDialog(
                onDismissRequest = {
                    confirmUnfollowDialogUserId = null
                },
                title = {
                    Text(
                        text = LocalStrings.current.actionUnfollow,
                        style = MaterialTheme.typography.titleMedium,
                    )
                },
                text = {
                    Text(text = LocalStrings.current.messageAreYouSure)
                },
                dismissButton = {
                    Button(
                        onClick = {
                            confirmUnfollowDialogUserId = null
                        },
                    ) {
                        Text(text = LocalStrings.current.buttonCancel)
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val userId = confirmUnfollowDialogUserId ?: ""
                            confirmUnfollowDialogUserId = null
                            if (userId.isNotEmpty()) {
                                model.reduce(SearchMviModel.Intent.Unfollow(userId))
                            }
                        },
                    ) {
                        Text(text = LocalStrings.current.buttonConfirm)
                    }
                },
            )
        }

        if (confirmDeleteFollowRequestDialogUserId != null) {
            AlertDialog(
                onDismissRequest = {
                    confirmUnfollowDialogUserId = null
                },
                title = {
                    Text(
                        text = LocalStrings.current.actionDeleteFollowRequest,
                        style = MaterialTheme.typography.titleMedium,
                    )
                },
                text = {
                    Text(text = LocalStrings.current.messageAreYouSure)
                },
                dismissButton = {
                    Button(
                        onClick = {
                            confirmUnfollowDialogUserId = null
                        },
                    ) {
                        Text(text = LocalStrings.current.buttonCancel)
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val userId = confirmUnfollowDialogUserId ?: ""
                            confirmUnfollowDialogUserId = null
                            if (userId.isNotEmpty()) {
                                model.reduce(SearchMviModel.Intent.Unfollow(userId))
                            }
                        },
                    ) {
                        Text(text = LocalStrings.current.buttonConfirm)
                    }
                },
            )
        }

        if (confirmUnfollowHashtagName != null) {
            AlertDialog(
                onDismissRequest = {
                    confirmUnfollowHashtagName = null
                },
                title = {
                    Text(
                        text = LocalStrings.current.actionUnfollow,
                        style = MaterialTheme.typography.titleMedium,
                    )
                },
                text = {
                    Text(text = LocalStrings.current.messageAreYouSure)
                },
                dismissButton = {
                    Button(
                        onClick = {
                            confirmUnfollowHashtagName = null
                        },
                    ) {
                        Text(text = LocalStrings.current.buttonCancel)
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val oldtag = confirmUnfollowHashtagName
                            confirmUnfollowHashtagName = null
                            if (oldtag != null) {
                                model.reduce(
                                    SearchMviModel.Intent.ToggleTagFollow(
                                        name = oldtag,
                                        newValue = false,
                                    ),
                                )
                            }
                        },
                    ) {
                        Text(text = LocalStrings.current.buttonConfirm)
                    }
                },
            )
        }

        if (confirmDeleteEntryId != null) {
            AlertDialog(
                onDismissRequest = {
                    confirmDeleteEntryId = null
                },
                title = {
                    Text(
                        text = LocalStrings.current.actionDelete,
                        style = MaterialTheme.typography.titleMedium,
                    )
                },
                text = {
                    Text(text = LocalStrings.current.messageAreYouSure)
                },
                dismissButton = {
                    Button(
                        onClick = {
                            confirmDeleteEntryId = null
                        },
                    ) {
                        Text(text = LocalStrings.current.buttonCancel)
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val entryId = confirmDeleteEntryId ?: ""
                            confirmDeleteEntryId = null
                            if (entryId.isNotEmpty()) {
                                model.reduce(SearchMviModel.Intent.DeleteEntry(entryId))
                            }
                        },
                    ) {
                        Text(text = LocalStrings.current.buttonConfirm)
                    }
                },
            )
        }

        if (confirmMuteEntry != null) {
            val creator = confirmMuteEntry?.reblog?.creator ?: confirmMuteEntry?.creator
            AlertDialog(
                onDismissRequest = {
                    confirmMuteEntry = null
                },
                title = {
                    Text(
                        text =
                            buildString {
                                append(LocalStrings.current.actionMute)
                                val handle = creator?.handle ?: ""
                                if (handle.isNotEmpty()) {
                                    append(" @$handle")
                                }
                            },
                        style = MaterialTheme.typography.titleMedium,
                    )
                },
                text = {
                    Text(text = LocalStrings.current.messageAreYouSure)
                },
                dismissButton = {
                    Button(
                        onClick = {
                            confirmMuteEntry = null
                        },
                    ) {
                        Text(text = LocalStrings.current.buttonCancel)
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val entryId = confirmMuteEntry?.id
                            val creatorId = creator?.id
                            confirmMuteEntry = null
                            if (entryId != null && creatorId != null) {
                                model.reduce(
                                    SearchMviModel.Intent.MuteUser(
                                        userId = creatorId,
                                        entryId = entryId,
                                    ),
                                )
                            }
                        },
                    ) {
                        Text(text = LocalStrings.current.buttonConfirm)
                    }
                },
            )
        }

        if (confirmBlockEntry != null) {
            val creator = confirmBlockEntry?.reblog?.creator ?: confirmBlockEntry?.creator
            AlertDialog(
                onDismissRequest = {
                    confirmBlockEntry = null
                },
                title = {
                    Text(
                        text =
                            buildString {
                                append(LocalStrings.current.actionBlock)
                                val handle = creator?.handle ?: ""
                                if (handle.isNotEmpty()) {
                                    append(" @$handle")
                                }
                            },
                        style = MaterialTheme.typography.titleMedium,
                    )
                },
                text = {
                    Text(text = LocalStrings.current.messageAreYouSure)
                },
                dismissButton = {
                    Button(
                        onClick = {
                            confirmBlockEntry = null
                        },
                    ) {
                        Text(text = LocalStrings.current.buttonCancel)
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val entryId = confirmBlockEntry?.id
                            val creatorId = creator?.id
                            confirmBlockEntry = null
                            if (entryId != null && creatorId != null) {
                                model.reduce(
                                    SearchMviModel.Intent.BlockUser(
                                        userId = creatorId,
                                        entryId = entryId,
                                    ),
                                )
                            }
                        },
                    ) {
                        Text(text = LocalStrings.current.buttonConfirm)
                    }
                },
            )
        }
    }
}
