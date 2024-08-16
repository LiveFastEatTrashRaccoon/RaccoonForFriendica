package com.livefast.eattrash.raccoonforfriendica.feature.composer.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomImage
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.ListLoadingIndicator
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.PlaceholderImage
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.SearchField
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MentionDialog(
    query: String = "",
    users: List<UserModel> = emptyList(),
    loading: Boolean = false,
    canFetchMore: Boolean = false,
    onLoadMoreUsers: (() -> Unit)? = null,
    onSearchChanged: ((String) -> Unit)? = null,
    onClose: ((UserModel?) -> Unit)? = null,
) {
    BasicAlertDialog(
        onDismissRequest = {
            onClose?.invoke(null)
        },
    ) {
        Column(
            modifier =
                Modifier
                    .background(color = MaterialTheme.colorScheme.surface)
                    .padding(Spacing.s),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
        ) {
            Text(
                text = LocalStrings.current.selectUserDialogTitle,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.height(Spacing.s))

            SearchField(
                hint = LocalStrings.current.selectUserSearchPlaceholder,
                value = query,
                onValueChange = {
                    onSearchChanged?.invoke(it)
                },
                onClear = {
                    onSearchChanged?.invoke((""))
                },
            )
            Spacer(modifier = Modifier.height(Spacing.xs))

            LazyColumn(
                modifier = Modifier.fillMaxWidth().height(200.dp),
            ) {
                if (!loading && users.isEmpty()) {
                    item {
                        Text(
                            modifier = Modifier.fillMaxWidth().padding(top = Spacing.s),
                            text = LocalStrings.current.messageEmptyList,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                }

                itemsIndexed(users) { idx, user ->
                    UserResultItem(
                        user = user,
                        onClick = {
                            onClose?.invoke(user)
                        },
                    )

                    if (idx == users.lastIndex - 5 && !loading && canFetchMore) {
                        onLoadMoreUsers?.invoke()
                    }
                }

                item {
                    if (loading && canFetchMore) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center,
                        ) {
                            ListLoadingIndicator()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UserResultItem(
    user: UserModel,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val avatar = user.avatar.orEmpty()
    val avatarSize = IconSize.m
    val fullColor = MaterialTheme.colorScheme.onBackground

    Row(
        modifier =
            modifier
                .clickable {
                    onClick?.invoke()
                }.padding(Spacing.s),
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

        Text(
            modifier = Modifier.weight(1f),
            text = user.displayName ?: user.username ?: "",
            style = MaterialTheme.typography.titleMedium,
            color = fullColor,
        )
    }
}
