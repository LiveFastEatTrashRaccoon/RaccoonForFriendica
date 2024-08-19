package com.livefast.eattrash.raccoonforfriendica.feature.profile.myaccount

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Dimensions
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.ListLoadingIndicator
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.SectionSelector
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.OptionId
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TimelineItem
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TimelineItemPlaceholder
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserFields
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserHeader
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserHeaderPlaceholder
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserSection
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.toAccountSection
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.toInt
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.toOption
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.toReadableName
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.BottomNavigationSection
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getDetailOpener
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getNavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.prettifyDate
import com.livefast.eattrash.raccoonforfriendica.core.utils.di.getShareHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.FieldModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.safeKey
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.di.getOpenUrlUseCase
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MyAccountScreen : Screen {
    @OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
    @Composable
    override fun Content() {
        val model = getScreenModel<MyAccountMviModel>()
        val uiState by model.uiState.collectAsState()
        val navigationCoordinator = remember { getNavigationCoordinator() }
        val uriHandler = LocalUriHandler.current
        val openUrl = remember { getOpenUrlUseCase(uriHandler) }
        val detailOpener = remember { getDetailOpener() }
        val lazyListState = rememberLazyListState()
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        val shareHelper = remember { getShareHelper() }
        val genericError = LocalStrings.current.messageGenericError
        val copyToClipboardSuccess = LocalStrings.current.messageTextCopiedToClipboard
        val clipboardManager = LocalClipboardManager.current
        var confirmDeleteEntryId by remember { mutableStateOf<String?>(null) }

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
                }
            }
        }

        LaunchedEffect(model) {
            model.effects
                .onEach { event ->
                    when (event) {
                        MyAccountMviModel.Effect.BackToTop -> goBackToTop()
                        MyAccountMviModel.Effect.Failure ->
                            snackbarHostState.showSnackbar(
                                message = genericError,
                            )
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

        val pullRefreshState =
            rememberPullRefreshState(
                refreshing = uiState.refreshing,
                onRefresh = {
                    model.reduce(MyAccountMviModel.Intent.Refresh)
                },
            )
        Box(
            modifier = Modifier.pullRefresh(pullRefreshState),
        ) {
            LazyColumn(
                state = lazyListState,
            ) {
                if (uiState.user != null) {
                    item {
                        UserHeader(
                            user = uiState.user,
                            onOpenUrl = { url ->
                                openUrl(url)
                            },
                            onOpenFollowers = {
                                uiState.user?.id?.also { userId ->
                                    detailOpener.openFollowers(userId)
                                }
                            },
                            onOpenFollowing = {
                                uiState.user?.id?.also { userId ->
                                    detailOpener.openFollowing(userId)
                                }
                            },
                        )
                    }
                } else {
                    item {
                        UserHeaderPlaceholder(modifier = Modifier.fillMaxWidth())
                    }
                }

                item {
                    UserFields(
                        modifier =
                            Modifier.padding(
                                top = Spacing.m,
                                bottom = Spacing.s,
                            ),
                        fields =
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
                            },
                        onOpenUrl = { url ->
                            openUrl(url)
                        },
                    )
                }

                stickyHeader {
                    SectionSelector(
                        modifier = Modifier.padding(bottom = Spacing.s),
                        titles =
                            listOf(
                                UserSection.Posts.toReadableName(),
                                UserSection.All.toReadableName(),
                                UserSection.Pinned.toReadableName(),
                                UserSection.Media.toReadableName(),
                            ),
                        scrollable = true,
                        currentSection = uiState.section.toInt(),
                        onSectionSelected = {
                            val section = it.toAccountSection()
                            model.reduce(
                                MyAccountMviModel.Intent.ChangeSection(section),
                            )
                        },
                    )
                }

                if (uiState.initial) {
                    items(5) {
                        TimelineItemPlaceholder(modifier = Modifier.fillMaxWidth())
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = Spacing.s),
                        )
                    }
                }

                itemsIndexed(
                    items = uiState.entries,
                    key = { _, e -> e.safeKey },
                ) { idx, entry ->
                    TimelineItem(
                        entry = entry,
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
                            model.reduce(MyAccountMviModel.Intent.ToggleReblog(e))
                        },
                        onBookmark = { e ->
                            model.reduce(MyAccountMviModel.Intent.ToggleBookmark(e))
                        },
                        onFavorite = { e ->
                            model.reduce(MyAccountMviModel.Intent.ToggleFavorite(e))
                        },
                        onReply = { e ->
                            detailOpener.openComposer(
                                inReplyToId = e.id,
                                inReplyToHandle = e.creator?.handle,
                                inReplyToUsername = e.creator?.let { it.displayName ?: it.username },
                            )
                        },
                        options =
                            buildList {
                                if (!entry.url.isNullOrBlank()) {
                                    this += OptionId.Share.toOption()
                                    this += OptionId.CopyUrl.toOption()
                                }
                                if (entry.reblog == null || entry.creator?.group == true) {
                                    this += OptionId.Edit.toOption()
                                    this += OptionId.Delete.toOption()
                                }
                                if (entry.reblog == null) {
                                    if (entry.pinned) {
                                        this += OptionId.Unpin.toOption()
                                    } else {
                                        this += OptionId.Pin.toOption()
                                    }
                                }
                            },
                        onOptionSelected = { optionId ->
                            when (optionId) {
                                OptionId.Edit -> {
                                    if (entry.creator?.group == true) {
                                        // edit the original post reblogged by the group
                                        detailOpener.openComposer(
                                            groupHandle = entry.creator?.handle,
                                            groupUsername = entry.creator?.username,
                                            editedPostId = entry.reblog?.id,
                                        )
                                    } else {
                                        detailOpener.openComposer(
                                            inReplyToId = entry.inReplyTo?.id,
                                            inReplyToHandle = entry.inReplyTo?.creator?.handle,
                                            inReplyToUsername = entry.inReplyTo?.creator?.username,
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
                                else -> Unit
                            }
                        },
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = Spacing.s),
                    )

                    val canFetchMore =
                        !uiState.initial && !uiState.loading && uiState.canFetchMore
                    if (idx == uiState.entries.lastIndex - 5 && canFetchMore) {
                        model.reduce(MyAccountMviModel.Intent.LoadNextPage)
                    }
                }
                if (!uiState.initial && !uiState.refreshing && !uiState.loading && uiState.entries.isEmpty()) {
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

            PullRefreshIndicator(
                refreshing = uiState.refreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                backgroundColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground,
            )

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
                                model.reduce(MyAccountMviModel.Intent.DeleteEntry(entryId))
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
