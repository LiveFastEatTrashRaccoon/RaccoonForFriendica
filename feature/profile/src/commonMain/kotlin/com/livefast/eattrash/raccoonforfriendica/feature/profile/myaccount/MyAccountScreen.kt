package com.livefast.eattrash.raccoonforfriendica.feature.profile.myaccount

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.kodein.rememberScreenModel
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.CornerSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Dimensions
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.ListLoadingIndicator
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.SectionSelector
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.CustomConfirmDialog
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.EntryDetailDialog
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.OptionId
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TimelineItem
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TimelineItemPlaceholder
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserFields
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserHeader
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserHeaderPlaceholder
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserSection
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.toOption
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.toReadableName
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.BottomNavigationSection
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getDetailOpener
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getNavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.prettifyDate
import com.livefast.eattrash.raccoonforfriendica.core.utils.di.getShareHelper
import com.livefast.eattrash.raccoonforfriendica.core.utils.isNearTheEnd
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.FieldModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.original
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.safeKey
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.di.getEntryActionRepository
import com.livefast.eattrash.raccoonforfriendica.feature.profile.LocalProfileTopAppBarStateWrapper
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

object MyAccountScreen : Tab {
    override val options: TabOptions
        @Composable get() =
            TabOptions(
                index = 1u,
                title = "",
            )

    @OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val model: MyAccountMviModel = rememberScreenModel()
        val uiState by model.uiState.collectAsState()
        val navigationCoordinator = remember { getNavigationCoordinator() }
        val connection = navigationCoordinator.getBottomBarScrollConnection()
        val uriHandler = LocalUriHandler.current
        val detailOpener = remember { getDetailOpener() }
        val lazyListState = rememberLazyListState()
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        val shareHelper = remember { getShareHelper() }
        val actionRepository = remember { getEntryActionRepository() }
        val genericError = LocalStrings.current.messageGenericError
        val copyToClipboardSuccess = LocalStrings.current.messageTextCopiedToClipboard
        val clipboardManager = LocalClipboardManager.current
        var confirmDeleteEntryId by remember { mutableStateOf<String?>(null) }
        var confirmReblogEntry by remember { mutableStateOf<TimelineEntryModel?>(null) }
        val topAppBarState = LocalProfileTopAppBarStateWrapper.current.topAppBarState
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
        val stickyHeaderTopOffset by animateDpAsState(
            if (lazyListState.firstVisibleItemIndex >= 2) {
                Dimensions.maxTopBarInset * topAppBarState.collapsedFraction
            } else {
                0.dp
            },
        )
        var seeDetailsEntry by remember { mutableStateOf<TimelineEntryModel?>(null) }

        suspend fun goBackToTop() {
            runCatching {
                if (lazyListState.firstVisibleItemIndex > 0) {
                    if (uiState.entries.isEmpty()) {
                        lazyListState.scrollToItem(1)
                    } else {
                        lazyListState.scrollToItem(2)
                    }
                } else {
                    lazyListState.scrollToItem(0)
                    topAppBarState.heightOffset = 0f
                    topAppBarState.contentOffset = 0f
                }
            }
        }

        LaunchedEffect(model) {
            model.effects
                .onEach { event ->
                    when (event) {
                        MyAccountMviModel.Effect.BackToTop -> goBackToTop()
                        MyAccountMviModel.Effect.Failure ->
                            snackbarHostState.showSnackbar(message = genericError)

                        is MyAccountMviModel.Effect.TriggerCopy -> {
                            clipboardManager.setText(AnnotatedString(event.text))
                            snackbarHostState.showSnackbar(copyToClipboardSuccess)
                        }
                    }
                }.launchIn(this)
        }
        LaunchedEffect(navigationCoordinator) {
            navigationCoordinator.onDoubleTabSelection
                .onEach { section ->
                    if (section == BottomNavigationSection.Profile) {
                        goBackToTop()
                    }
                }.launchIn(this)
        }

        PullToRefreshBox(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .then(
                        if (connection != null && uiState.hideNavigationBarWhileScrolling) {
                            Modifier.nestedScroll(connection)
                        } else {
                            Modifier
                        },
                    ).then(
                        if (connection != null && uiState.hideNavigationBarWhileScrolling) {
                            Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
                        } else {
                            Modifier
                        },
                    ),
            isRefreshing = uiState.refreshing,
            onRefresh = {
                model.reduce(MyAccountMviModel.Intent.Refresh)
            },
        ) {
            LazyColumn(
                state = lazyListState,
            ) {
                if (uiState.user != null) {
                    item {
                        UserHeader(
                            user = uiState.user,
                            autoloadImages = uiState.autoloadImages,
                            editButtonEnabled = true,
                            onOpenUrl = { url ->
                                uriHandler.openUri(url)
                            },
                            onOpenFollowers = {
                                uiState.user?.also { user ->
                                    detailOpener.openFollowers(
                                        user = user,
                                        enableExport = true,
                                    )
                                }
                            },
                            onOpenFollowing = {
                                uiState.user?.also { user ->
                                    detailOpener.openFollowing(
                                        user = user,
                                        enableExport = true,
                                    )
                                }
                            },
                            onEditClicked = {
                                detailOpener.openEditProfile()
                            },
                        )
                    }
                } else if (uiState.initial) {
                    item {
                        UserHeaderPlaceholder(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = Spacing.xs),
                        )
                    }
                } else {
                    item {
                        Text(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.s),
                            textAlign = TextAlign.Center,
                            text = LocalStrings.current.messageAuthIssue,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                        )

                        val annotatedString =
                            buildAnnotatedString {
                                val linkStyle =
                                    SpanStyle(
                                        color = MaterialTheme.colorScheme.primary,
                                        textDecoration = TextDecoration.Underline,
                                    )
                                withStyle(SpanStyle(color = MaterialTheme.colorScheme.onBackground)) {
                                    append(LocalStrings.current.messageAuthIssueHintsTitle)
                                    append("\n• ")
                                    pushLink(
                                        LinkAnnotation.Clickable(
                                            tag = "action-refresh",
                                            linkInteractionListener = {
                                                model.reduce(MyAccountMviModel.Intent.Refresh)
                                            },
                                        ),
                                    )
                                    withStyle(linkStyle) {
                                        append(LocalStrings.current.messageAuthIssueHint1)
                                    }
                                    pop()
                                    append("\n• ")
                                    pushLink(
                                        LinkAnnotation.Clickable(
                                            tag = "action-login",
                                            linkInteractionListener = {
                                                model.reduce(MyAccountMviModel.Intent.Logout)
                                            },
                                        ),
                                    )
                                    withStyle(linkStyle) {
                                        append(LocalStrings.current.messageAuthIssueHint2)
                                    }
                                    pop()
                                    append("\n• ")
                                    append(LocalStrings.current.messageAuthIssueHint3)
                                }
                            }
                        Box(
                            modifier =
                                Modifier
                                    .padding(
                                        vertical = Spacing.m,
                                        horizontal = Spacing.s,
                                    ).border(
                                        width = 1.dp,
                                        color = MaterialTheme.colorScheme.onBackground,
                                        shape = RoundedCornerShape(CornerSize.l),
                                    ).padding(
                                        vertical = Spacing.s,
                                        horizontal = Spacing.m,
                                    ),
                        ) {
                            Text(
                                modifier =
                                    Modifier.fillMaxWidth(),
                                text = annotatedString,
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                    }
                }

                item {
                    val fields =
                        buildList {
                            uiState.user?.created?.also { date ->
                                add(
                                    FieldModel(
                                        key = LocalStrings.current.accountAge,
                                        value = date.prettifyDate(),
                                    ),
                                )
                            }
                            addAll(uiState.user?.fields.orEmpty())
                        }
                    if (fields.isNotEmpty()) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = Spacing.s),
                        )
                        UserFields(
                            fields = fields,
                            autoloadImages = uiState.autoloadImages,
                            onOpenUrl = { url ->
                                uriHandler.openUri(url)
                            },
                        )
                    }
                }

                if (uiState.initial || uiState.entries.isNotEmpty()) {
                    stickyHeader {
                        val titles =
                            listOf(
                                UserSection.Posts,
                                UserSection.All,
                                UserSection.Pinned,
                                UserSection.Media,
                            )
                        SectionSelector(
                            modifier =
                                Modifier
                                    .background(MaterialTheme.colorScheme.background)
                                    .padding(
                                        top = stickyHeaderTopOffset,
                                        bottom = Spacing.s,
                                    ),
                            titles = titles.map { it.toReadableName() },
                            scrollable = true,
                            currentSection = titles.indexOf(uiState.section),
                            onSectionSelected = {
                                model.reduce(
                                    MyAccountMviModel.Intent.ChangeSection(titles[it]),
                                )
                            },
                        )
                    }
                }

                if (uiState.initial) {
                    val placeholderCount = 5
                    items(placeholderCount) { idx ->
                        TimelineItemPlaceholder(modifier = Modifier.fillMaxWidth())
                        if (idx < placeholderCount - 1) {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = Spacing.s),
                            )
                        }
                    }
                }

                itemsIndexed(
                    items = uiState.entries,
                    key = { _, e -> "my-account-${e.safeKey}" },
                ) { idx, entry ->
                    TimelineItem(
                        entry = entry,
                        layout = uiState.layout,
                        blurNsfw = uiState.blurNsfw,
                        autoloadImages = uiState.autoloadImages,
                        pollEnabled = false,
                        maxBodyLines = uiState.maxBodyLines,
                        onClick = { e ->
                            detailOpener.openEntryDetail(e)
                        },
                        onOpenUrl = { url ->
                            uriHandler.openUri(url)
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
                        onBookmark =
                            { e: TimelineEntryModel ->
                                model.reduce(MyAccountMviModel.Intent.ToggleBookmark(e))
                            }.takeIf { actionRepository.canBookmark(entry.original) },
                        onFavorite =
                            { e: TimelineEntryModel ->
                                model.reduce(MyAccountMviModel.Intent.ToggleFavorite(e))
                            }.takeIf { actionRepository.canFavorite(entry.original) },
                        onReply =
                            { e: TimelineEntryModel ->
                                detailOpener.openComposer(
                                    inReplyTo = e,
                                    inReplyToUser = e.creator,
                                )
                            }.takeIf { actionRepository.canReply(entry.original) },
                        options =
                            buildList {
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
                                if (actionRepository.canQuote(entry.original)) {
                                    this += OptionId.Quote.toOption()
                                }
                                this += OptionId.ViewDetails.toOption()
                                this += OptionId.CopyToClipboard.toOption()
                            },
                        onOptionSelected = { optionId ->
                            when (optionId) {
                                OptionId.Edit -> {
                                    if (entry.creator?.group == true) {
                                        // edit the original post reblogged by the group
                                        detailOpener.openComposer(
                                            inReplyTo = entry.inReplyTo,
                                            inReplyToUser = entry.creator,
                                            editedPostId = entry.reblog?.id,
                                            inGroup = true,
                                        )
                                    } else {
                                        detailOpener.openComposer(
                                            inReplyTo = entry.inReplyTo,
                                            inReplyToUser = entry.inReplyTo?.creator,
                                            editedPostId = entry.id,
                                        )
                                    }
                                }
                                OptionId.Delete -> confirmDeleteEntryId = entry.id
                                OptionId.Share -> {
                                    val urlString = entry.url.orEmpty()
                                    shareHelper.share(urlString)
                                }

                                OptionId.CopyUrl -> {
                                    val urlString = entry.url.orEmpty()
                                    clipboardManager.setText(AnnotatedString(urlString))
                                    scope.launch {
                                        snackbarHostState.showSnackbar(copyToClipboardSuccess)
                                    }
                                }
                                OptionId.Pin, OptionId.Unpin ->
                                    model.reduce(
                                        MyAccountMviModel.Intent.TogglePin(entry),
                                    )
                                OptionId.ViewDetails -> seeDetailsEntry = entry.original
                                OptionId.Quote -> {
                                    entry.original.also { entryToShare ->
                                        detailOpener.openComposer(
                                            urlToShare = entryToShare.url,
                                        )
                                    }
                                }
                                OptionId.CopyToClipboard ->
                                    model.reduce(MyAccountMviModel.Intent.CopyToClipboard(entry.original))
                                else -> Unit
                            }
                        },
                    )
                    if (idx < uiState.entries.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = Spacing.s),
                        )
                    }

                    val canFetchMore =
                        !uiState.initial && !uiState.loading && uiState.canFetchMore
                    val isNearTheEnd = idx.isNearTheEnd(uiState.entries)
                    if (isNearTheEnd && canFetchMore) {
                        model.reduce(MyAccountMviModel.Intent.LoadNextPage)
                    }
                }
                if (!uiState.initial && !uiState.refreshing && !uiState.loading && uiState.entries.isEmpty() && uiState.user != null) {
                    item {
                        Text(
                            modifier = Modifier.fillMaxWidth().padding(top = Spacing.m),
                            text = LocalStrings.current.messageEmptyList,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge,
                        )
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

            SnackbarHost(
                modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = Dimensions.floatingActionButtonBottomInset),
                hostState = snackbarHostState,
            ) { data ->
                Snackbar(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    snackbarData = data,
                )
            }
        }

        if (confirmDeleteEntryId != null) {
            CustomConfirmDialog(
                title = LocalStrings.current.actionDelete,
                onClose = { confirm ->
                    val entryId = confirmDeleteEntryId
                    confirmDeleteEntryId = null
                    if (confirm && entryId != null) {
                        model.reduce(MyAccountMviModel.Intent.DeleteEntry(entryId))
                    }
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
                        model.reduce(MyAccountMviModel.Intent.ToggleReblog(e))
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
