package com.livefast.eattrash.raccoonforfriendica.feaure.search

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.kodein.rememberScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Dimensions
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.toWindowInsets
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.ListLoadingIndicator
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.SearchField
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.SectionSelector
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.ConfirmMuteUserBottomSheet
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.CustomConfirmDialog
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.EntryDetailDialog
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.GenericPlaceholder
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.HashtagItem
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.Option
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.OptionId
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.PollVoteErrorDialog
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TimelineDivider
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TimelineItem
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TimelineItemPlaceholder
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserItem
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserItemPlaceholder
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.toOption
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getDetailOpener
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getNavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.utils.compose.getAnimatedDots
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.getDurationFromDateToNow
import com.livefast.eattrash.raccoonforfriendica.core.utils.di.getShareHelper
import com.livefast.eattrash.raccoonforfriendica.core.utils.isNearTheEnd
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.ExploreItemModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.RelationshipStatusNextAction
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.isOldEntry
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.original
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.safeKey
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.di.getEntryActionRepository
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.openExternally
import com.livefast.eattrash.raccoonforfriendica.feaure.search.data.SearchSection
import com.livefast.eattrash.raccoonforfriendica.feaure.search.data.toReadableName
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.time.Duration

class SearchScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
    @Composable
    override fun Content() {
        val model: SearchMviModel = rememberScreenModel()
        val uiState by model.uiState.collectAsState()
        val navigationCoordinator = remember { getNavigationCoordinator() }
        val topAppBarState = rememberTopAppBarState()
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
        val uriHandler = LocalUriHandler.current
        val detailOpener = remember { getDetailOpener() }
        val lazyListState = rememberLazyListState()
        val scope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }
        val shareHelper = remember { getShareHelper() }
        val actionRepository = remember { getEntryActionRepository() }
        val searchFieldFocusRequester = remember { FocusRequester() }
        val copyToClipboardSuccess = LocalStrings.current.messageTextCopiedToClipboard
        val clipboardManager = LocalClipboardManager.current
        var confirmUnfollowDialogUserId by remember { mutableStateOf<String?>(null) }
        var confirmDeleteFollowRequestDialogUserId by remember { mutableStateOf<String?>(null) }
        var confirmDeleteEntryId by remember { mutableStateOf<String?>(null) }
        var confirmMuteEntry by remember { mutableStateOf<TimelineEntryModel?>(null) }
        var confirmBlockEntry by remember { mutableStateOf<TimelineEntryModel?>(null) }
        var confirmReblogEntry by remember { mutableStateOf<TimelineEntryModel?>(null) }
        var pollErrorDialogOpened by remember { mutableStateOf(false) }
        var seeDetailsEntry by remember { mutableStateOf<TimelineEntryModel?>(null) }

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
                        SearchMviModel.Effect.PollVoteFailure -> pollErrorDialogOpened = true
                        is SearchMviModel.Effect.TriggerCopy -> {
                            clipboardManager.setText(AnnotatedString(event.text))
                            snackbarHostState.showSnackbar(copyToClipboardSuccess)
                        }
                    }
                }.launchIn(this)
        }
        LaunchedEffect(Unit) {
            searchFieldFocusRequester.requestFocus()
        }

        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                TopAppBar(
                    modifier = Modifier.clickable { scope.launch { goBackToTop() } },
                    windowInsets = topAppBarState.toWindowInsets(),
                    scrollBehavior = scrollBehavior,
                    title = {
                        SearchField(
                            modifier = Modifier.focusRequester(searchFieldFocusRequester),
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
                            IconButton(
                                onClick = {
                                    navigationCoordinator.pop()
                                },
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                    contentDescription = LocalStrings.current.actionGoBack,
                                )
                            }
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
            PullToRefreshBox(
                modifier =
                    Modifier
                        .padding(padding)
                        .fillMaxWidth()
                        .then(
                            if (uiState.hideNavigationBarWhileScrolling) {
                                Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
                            } else {
                                Modifier
                            },
                        ),
                isRefreshing = uiState.refreshing,
                onRefresh = {
                    model.reduce(SearchMviModel.Intent.Refresh)
                },
            ) {
                LazyColumn(
                    state = lazyListState,
                ) {
                    stickyHeader {
                        val titles =
                            listOf(
                                SearchSection.Posts,
                                SearchSection.Users,
                                SearchSection.Hashtags,
                            )
                        SectionSelector(
                            modifier =
                                Modifier
                                    .background(MaterialTheme.colorScheme.background)
                                    .padding(
                                        top = Dimensions.maxTopBarInset * topAppBarState.collapsedFraction,
                                        bottom = Spacing.s,
                                    ),
                            titles = titles.map { it.toReadableName() },
                            currentSection = titles.indexOf(uiState.section),
                            onSectionSelected = {
                                model.reduce(
                                    SearchMviModel.Intent.ChangeSection(titles[it]),
                                )
                            },
                        )
                    }

                    if (uiState.initial) {
                        val placeholderCount = 20
                        items(placeholderCount) { idx ->
                            val modifier = Modifier.fillMaxWidth()
                            when (uiState.section) {
                                SearchSection.Hashtags -> {
                                    GenericPlaceholder(modifier = modifier)
                                    Spacer(modifier = Modifier.height(Spacing.interItem))
                                }

                                SearchSection.Posts -> {
                                    TimelineItemPlaceholder(modifier = modifier)
                                    if (idx < placeholderCount - 1) {
                                        TimelineDivider(layout = uiState.layout)
                                    }
                                }

                                SearchSection.Users -> {
                                    UserItemPlaceholder(modifier = modifier)
                                    Spacer(modifier = Modifier.height(Spacing.interItem))
                                }
                            }
                        }
                    }

                    if (!uiState.initial && !uiState.refreshing && !uiState.loading && !uiState.earlyLoading && uiState.items.isEmpty()) {
                        item {
                            if (uiState.query.isEmpty()) {
                                val animatingPart = getAnimatedDots()
                                Text(
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(
                                                top = Spacing.m,
                                                start = Spacing.s,
                                            ),
                                    text =
                                        buildString {
                                            append("🔦")
                                            append(" ")
                                            append(LocalStrings.current.messageSearchInitialEmpty)
                                            append(animatingPart)
                                        },
                                    style = MaterialTheme.typography.bodyLarge,
                                )
                            } else {
                                Text(
                                    modifier = Modifier.fillMaxWidth().padding(top = Spacing.m),
                                    text = LocalStrings.current.messageEmptyList,
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodyLarge,
                                )
                            }
                        }
                    }

                    itemsIndexed(
                        items = uiState.items,
                        key = { _, e ->
                            when (e) {
                                is ExploreItemModel.Entry -> "search-${e.entry.safeKey}"
                                else -> "search-${e.id}"
                            }
                        },
                    ) { idx, item ->
                        when (item) {
                            is ExploreItemModel.Entry -> {
                                TimelineItem(
                                    entry = item.entry,
                                    layout = uiState.layout,
                                    blurNsfw = uiState.blurNsfw,
                                    autoloadImages = uiState.autoloadImages,
                                    maxBodyLines = uiState.maxBodyLines,
                                    onClick = { e ->
                                        detailOpener.openEntryDetail(e)
                                    },
                                    onOpenUrl = { url, allowOpenInternal ->
                                        if (allowOpenInternal) {
                                            uriHandler.openUri(url)
                                        } else {
                                            uriHandler.openExternally(url)
                                        }
                                    },
                                    onOpenUser = {
                                        detailOpener.openUserDetail(it)
                                    },
                                    onOpenImage = { urls, imageIdx, videoIndices ->
                                        detailOpener.openImageDetail(
                                            urls = urls,
                                            initialIndex = imageIdx,
                                            videoIndices = videoIndices,
                                        )
                                    },
                                    onReblog =
                                        { e: TimelineEntryModel ->
                                            val timeSinceCreation =
                                                e.created?.run {
                                                    getDurationFromDateToNow(this)
                                                } ?: Duration.ZERO
                                            when {
                                                !e.reblogged && timeSinceCreation.isOldEntry ->
                                                    confirmReblogEntry = e

                                                else ->
                                                    model.reduce(
                                                        SearchMviModel.Intent.ToggleReblog(e),
                                                    )
                                            }
                                        }.takeIf { actionRepository.canReblog(item.entry.original) },
                                    onBookmark =
                                        { e: TimelineEntryModel ->
                                            model.reduce(SearchMviModel.Intent.ToggleBookmark(e))
                                        }.takeIf { actionRepository.canBookmark(item.entry.original) },
                                    onFavorite =
                                        { e: TimelineEntryModel ->
                                            model.reduce(SearchMviModel.Intent.ToggleFavorite(e))
                                        }.takeIf { actionRepository.canFavorite(item.entry.original) },
                                    onDislike =
                                        { e: TimelineEntryModel ->
                                            model.reduce(SearchMviModel.Intent.ToggleDislike(e))
                                        }.takeIf { actionRepository.canDislike(item.entry.original) },
                                    onReply =
                                        { e: TimelineEntryModel ->
                                            detailOpener.openComposer(
                                                inReplyTo = e,
                                                inReplyToUser = e.creator,
                                            )
                                        }.takeIf { actionRepository.canReply(item.entry.original) },
                                    onPollVote =
                                        uiState.currentUserId?.let {
                                            { e, choices ->
                                                model.reduce(
                                                    SearchMviModel.Intent.SubmitPollVote(
                                                        entry = e,
                                                        choices = choices,
                                                    ),
                                                )
                                            }
                                        },
                                    onShowOriginal = {
                                        model.reduce(
                                            SearchMviModel.Intent.ToggleTranslation(
                                                item.entry.original,
                                            ),
                                        )
                                    },
                                    options =
                                        buildList {
                                            val entry = item.entry
                                            if (actionRepository.canShare(entry.original)) {
                                                this += OptionId.Share.toOption()
                                                this += OptionId.CopyUrl.toOption()
                                            }
                                            if (actionRepository.canEdit(entry.original)) {
                                                this += OptionId.Edit.toOption()
                                            }
                                            if (actionRepository.canDelete(entry.original)) {
                                                this += OptionId.Delete.toOption()
                                            }
                                            if (actionRepository.canTogglePin(entry)) {
                                                if (entry.pinned) {
                                                    this += OptionId.Unpin.toOption()
                                                } else {
                                                    this += OptionId.Pin.toOption()
                                                }
                                            }
                                            if (actionRepository.canMute(entry)) {
                                                this += OptionId.Mute.toOption()
                                            }
                                            if (actionRepository.canBlock(entry)) {
                                                this += OptionId.Block.toOption()
                                            }
                                            if (actionRepository.canReport(entry.original)) {
                                                this += OptionId.ReportUser.toOption()
                                                this += OptionId.ReportEntry.toOption()
                                            }
                                            if (actionRepository.canQuote(entry.original)) {
                                                this += OptionId.Quote.toOption()
                                            }
                                            this += OptionId.ViewDetails.toOption()
                                            this += OptionId.CopyToClipboard.toOption()
                                            val currentLang = uiState.lang.orEmpty()
                                            if (currentLang.isNotEmpty() && entry.lang != currentLang && !entry.isShowingTranslation) {
                                                this +=
                                                    Option(
                                                        id = OptionId.Translate,
                                                        label =
                                                            buildString {
                                                                append(
                                                                    LocalStrings.current.actionTranslateTo(
                                                                        currentLang,
                                                                    ),
                                                                )
                                                                append(" (")
                                                                append(LocalStrings.current.experimental)
                                                                append(")")
                                                            },
                                                    )
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
                                                        inReplyTo = entryToEdit.inReplyTo,
                                                        inReplyToUser = entryToEdit.inReplyTo?.creator,
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
                                            OptionId.ReportUser ->
                                                item.entry.original.creator?.also { userToReport ->
                                                    detailOpener.openCreateReport(user = userToReport)
                                                }
                                            OptionId.ReportEntry ->
                                                item.entry.original.also { entryToReport ->
                                                    entryToReport.creator?.also { userToReport ->
                                                        detailOpener.openCreateReport(
                                                            user = userToReport,
                                                            entry = entryToReport,
                                                        )
                                                    }
                                                }
                                            OptionId.ViewDetails -> seeDetailsEntry = item.entry.original
                                            OptionId.Quote -> {
                                                item.entry.original.also { entryToShare ->
                                                    detailOpener.openComposer(
                                                        urlToShare = entryToShare.url,
                                                    )
                                                }
                                            }
                                            OptionId.CopyToClipboard ->
                                                model.reduce(
                                                    SearchMviModel.Intent.CopyToClipboard(
                                                        item.entry.original,
                                                    ),
                                                )

                                            OptionId.Translate ->
                                                model.reduce(
                                                    SearchMviModel.Intent.ToggleTranslation(
                                                        item.entry.original,
                                                    ),
                                                )
                                            else -> Unit
                                        }
                                    },
                                )
                                if (idx < uiState.items.lastIndex) {
                                    TimelineDivider(layout = uiState.layout)
                                }
                            }

                            is ExploreItemModel.HashTag -> {
                                HashtagItem(
                                    hashtag = item.hashtag,
                                    onOpen = {
                                        detailOpener.openHashtag(it)
                                    },
                                )
                                Spacer(modifier = Modifier.height(Spacing.interItem))
                            }

                            is ExploreItemModel.User -> {
                                UserItem(
                                    user = item.user,
                                    autoloadImages = uiState.autoloadImages,
                                    onClick = {
                                        detailOpener.openUserDetail(item.user)
                                    },
                                    onRelationshipClicked = { nextAction ->
                                        when (nextAction) {
                                            RelationshipStatusNextAction.AcceptRequest -> {
                                                detailOpener.openFollowRequests()
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
                        val isNearTheEnd = idx.isNearTheEnd(uiState.items)
                        if (isNearTheEnd && canFetchMore) {
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
            }
        }

        if (confirmUnfollowDialogUserId != null) {
            CustomConfirmDialog(
                title = LocalStrings.current.actionUnfollow,
                onClose = { confirm ->
                    val userId = confirmUnfollowDialogUserId
                    confirmUnfollowDialogUserId = null
                    if (confirm && userId != null) {
                        model.reduce(SearchMviModel.Intent.Unfollow(userId))
                    }
                },
            )
        }

        if (confirmDeleteFollowRequestDialogUserId != null) {
            CustomConfirmDialog(
                title = LocalStrings.current.actionDeleteFollowRequest,
                onClose = { confirm ->
                    val userId = confirmUnfollowDialogUserId
                    confirmUnfollowDialogUserId = null
                    if (confirm && userId != null) {
                        model.reduce(SearchMviModel.Intent.Unfollow(userId))
                    }
                },
            )
        }

        if (confirmDeleteEntryId != null) {
            CustomConfirmDialog(
                title = LocalStrings.current.actionDelete,
                onClose = { confirm ->
                    val entryId = confirmDeleteEntryId
                    confirmDeleteEntryId = null
                    if (confirm && entryId != null) {
                        model.reduce(SearchMviModel.Intent.DeleteEntry(entryId))
                    }
                },
            )
        }

        if (confirmMuteEntry != null) {
            (confirmMuteEntry?.reblog?.creator ?: confirmMuteEntry?.creator)?.also { user ->
                ConfirmMuteUserBottomSheet(
                    userHandle = user.handle.orEmpty(),
                    onClose = { pair ->
                        val entryId = confirmMuteEntry?.id
                        confirmMuteEntry = null
                        if (pair != null) {
                            val (duration, disableNotifications) = pair
                            if (entryId != null) {
                                model.reduce(
                                    SearchMviModel.Intent.MuteUser(
                                        userId = user.id,
                                        entryId = entryId,
                                        duration = duration,
                                        disableNotifications = disableNotifications,
                                    ),
                                )
                            }
                        }
                    },
                )
            }
        }

        if (confirmBlockEntry != null) {
            val creator = confirmBlockEntry?.reblog?.creator ?: confirmBlockEntry?.creator
            CustomConfirmDialog(
                title =
                    buildString {
                        append(LocalStrings.current.actionBlock)
                        val handle = creator?.handle ?: ""
                        if (handle.isNotEmpty()) {
                            append(" @$handle")
                        }
                    },
                onClose = { confirm ->
                    val entryId = confirmBlockEntry?.id
                    val creatorId = creator?.id
                    confirmBlockEntry = null
                    if (confirm && entryId != null && creatorId != null) {
                        model.reduce(
                            SearchMviModel.Intent.BlockUser(
                                userId = creatorId,
                                entryId = entryId,
                            ),
                        )
                    }
                },
            )
        }

        if (pollErrorDialogOpened) {
            PollVoteErrorDialog(
                onDismissRequest = {
                    pollErrorDialogOpened = false
                },
            )
        }

        if (confirmReblogEntry != null) {
            CustomConfirmDialog(
                title = LocalStrings.current.buttonConfirm,
                body = LocalStrings.current.messageAreYouSureReblog,
                onClose = { confirm ->
                    val e = confirmReblogEntry
                    confirmReblogEntry = null
                    if (confirm && e != null) {
                        model.reduce(SearchMviModel.Intent.ToggleReblog(e))
                    }
                },
            )
        }

        seeDetailsEntry?.let { entry ->
            EntryDetailDialog(
                entry = entry,
                onClose = {
                    seeDetailsEntry = null
                },
            )
        }
    }
}
