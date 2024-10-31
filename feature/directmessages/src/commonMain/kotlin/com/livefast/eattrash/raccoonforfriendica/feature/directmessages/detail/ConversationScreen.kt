package com.livefast.eattrash.raccoonforfriendica.feature.directmessages.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.CornerSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.toWindowInsets
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomImage
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.PlaceholderImage
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.TextWithCustomEmojis
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getNavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.utils.compose.safeImePadding
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.getDurationFromDateToNow
import com.livefast.eattrash.raccoonforfriendica.core.utils.isNearTheEnd
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.DirectMessageModel
import com.livefast.eattrash.raccoonforfriendica.feature.directmessages.components.MessageItem
import com.livefast.eattrash.raccoonforfriendica.feature.directmessages.components.MessageItemPlaceholder
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.core.parameter.parametersOf
import kotlin.math.abs

class ConversationScreen(
    private val otherUserId: String,
    private val parentUri: String,
) : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val model =
            getScreenModel<ConversationMviModel>(
                parameters = {
                    parametersOf(otherUserId, parentUri)
                },
            )
        val uiState by model.uiState.collectAsState()
        val navigationCoordinator = remember { getNavigationCoordinator() }
        val topAppBarState = rememberTopAppBarState()
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
        val snackbarHostState = remember { SnackbarHostState() }
        val lazyListState = rememberLazyListState()
        val scope = rememberCoroutineScope()
        val otherUserName = uiState.otherUser?.let { it.displayName ?: it.username } ?: ""
        val genericError = LocalStrings.current.messageGenericError
        val followRequiredMessage = LocalStrings.current.followRequiredMessage

        fun goBackToTop() {
            runCatching {
                scope.launch {
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
                        ConversationMviModel.Effect.BackToTop -> goBackToTop()
                        ConversationMviModel.Effect.Failure ->
                            snackbarHostState.showSnackbar(genericError)

                        ConversationMviModel.Effect.FollowUserRequired ->
                            snackbarHostState.showSnackbar(followRequiredMessage)
                    }
                }.launchIn(this)
        }

        Scaffold(
            modifier =
                Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .navigationBarsPadding()
                    .safeImePadding(),
            topBar = {
                TopAppBar(
                    modifier = Modifier.clickable { scope.launch { goBackToTop() } },
                    windowInsets = topAppBarState.toWindowInsets(),
                    scrollBehavior = scrollBehavior,
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.s),
                        ) {
                            val avatar = uiState.otherUser?.avatar.orEmpty()
                            val avatarSize = IconSize.l
                            if (avatar.isNotEmpty() && uiState.autoloadImages) {
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
                                    title = otherUserName,
                                )
                            }
                            TextWithCustomEmojis(
                                text = otherUserName,
                                emojis = uiState.otherUser?.emojis.orEmpty(),
                                autoloadImages = uiState.autoloadImages,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onBackground,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
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
            bottomBar = {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                top = Spacing.s,
                                end = Spacing.s,
                                start = Spacing.s,
                                bottom = Spacing.s,
                            ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xxs),
                ) {
                    OutlinedTextField(
                        modifier = Modifier.weight(1f),
                        keyboardOptions =
                            KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                            ),
                        value = uiState.newMessageValue,
                        textStyle = MaterialTheme.typography.bodyMedium,
                        maxLines = 3,
                        onValueChange = {
                            model.reduce(ConversationMviModel.Intent.SetNewMessageValue(it))
                        },
                    )
                    FilledIconButton(
                        onClick = {
                            model.reduce(ConversationMviModel.Intent.Submit)
                        },
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.Send,
                            contentDescription = null,
                        )
                    }
                }
            },
        ) { padding ->
            PullToRefreshBox(
                modifier =
                    Modifier
                        .padding(padding)
                        .fillMaxSize(),
                isRefreshing = uiState.refreshing,
                onRefresh = {
                    model.reduce(ConversationMviModel.Intent.Refresh)
                },
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = lazyListState,
                    reverseLayout = true,
                    verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
                ) {
                    if (uiState.initial) {
                        items(10) { idx ->
                            MessageItemPlaceholder(
                                isMyMessage = idx % 2 == 0,
                                height =
                                    when (idx % 3) {
                                        1 -> 80.dp
                                        2 -> 100.dp
                                        else -> 50.dp
                                    },
                                isLastOfSequence = false,
                                isFirstOfSequence = false,
                            )
                        }
                    }

                    if (!uiState.initial && !uiState.refreshing && !uiState.loading && uiState.items.isEmpty()) {
                        item {
                            if (otherUserName.isNotEmpty()) {
                                Box(
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(
                                                top = Spacing.m,
                                                start = Spacing.s,
                                                end = Spacing.s,
                                            ).border(
                                                width = Dp.Hairline,
                                                color = MaterialTheme.colorScheme.onBackground,
                                                shape = RoundedCornerShape(CornerSize.xl),
                                            ).background(
                                                color =
                                                    MaterialTheme.colorScheme.surfaceColorAtElevation(
                                                        5.dp,
                                                    ),
                                                shape = RoundedCornerShape(CornerSize.xl),
                                            ).padding(Spacing.m),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    TextWithCustomEmojis(
                                        text =
                                            buildString {
                                                append(LocalStrings.current.messageEmptyConversation)
                                                append(" ")
                                                append(otherUserName)
                                            },
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onBackground,
                                        emojis = uiState.otherUser?.emojis.orEmpty(),
                                        autoloadImages = uiState.autoloadImages,
                                    )
                                }
                            }
                        }
                    }

                    itemsIndexed(
                        items = uiState.items,
                        key = { _, e -> "conversation-${e.id}" },
                    ) { idx, message ->
                        val moreThan5MinutesFromPrevious =
                            isTimeBetweenMessagesAboveThreshold(
                                current = message,
                                other = uiState.items.getOrNull(idx - 1),
                            )
                        val moreThan5MinutesFromNext =
                            isTimeBetweenMessagesAboveThreshold(
                                current = message,
                                other = uiState.items.getOrNull(idx + 1),
                            )
                        // these checks look inverted because the LazyColumn has reverseLayout = true
                        val isFirst =
                            idx >= uiState.items.lastIndex ||
                                uiState.items[idx + 1].sender?.id != message.sender?.id ||
                                moreThan5MinutesFromNext
                        val isLast =
                            idx == 0 ||
                                uiState.items[idx - 1].sender?.id != message.sender?.id ||
                                moreThan5MinutesFromPrevious

                        MessageItem(
                            message = message,
                            isMyMessage = message.sender?.id == uiState.currentUser?.id,
                            isLastOfSequence = isLast,
                            isFirstOfSequence = isFirst,
                            withDate = isLast,
                        )

                        val canFetchMore =
                            !uiState.initial && !uiState.loading && uiState.canFetchMore
                        val isNearTheEnd = idx.isNearTheEnd(uiState.items)
                        if (isNearTheEnd && canFetchMore) {
                            model.reduce(ConversationMviModel.Intent.LoadNextPage)
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(Spacing.xl))
                    }
                }
            }
        }
    }
}

private fun isTimeBetweenMessagesAboveThreshold(
    current: DirectMessageModel,
    other: DirectMessageModel?,
    thresholdSeconds: Long = 300,
): Boolean {
    val ref1 =
        current.created
            ?.let { getDurationFromDateToNow(it) }
            ?.inWholeSeconds ?: return false
    val ref2 =
        other
            ?.created
            ?.let { getDurationFromDateToNow(it) }
            ?.inWholeSeconds ?: return false
    return abs(ref1 - ref2) > thresholdSeconds
}
