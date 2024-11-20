package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.CornerSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomImage
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.EmojiModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsertEmojiBottomSheet(
    emojis: List<EmojiModel>,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    onInsert: ((EmojiModel) -> Unit)? = null,
    onClose: (() -> Unit)? = null,
) {
    val groupedEmojis = emojis.groupBy { it.category.orEmpty() }
    val categories = groupedEmojis.keys.sorted()

    ModalBottomSheet(
        contentWindowInsets = { WindowInsets.navigationBars },
        sheetState = sheetState,
        onDismissRequest = {
            onClose?.invoke()
        },
    ) {
        Column {
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = LocalStrings.current.insertEmojiTitle,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )

            Spacer(modifier = Modifier.height(Spacing.s))

            LazyVerticalStaggeredGrid(
                modifier = Modifier.heightIn(max = 400.dp),
                columns = StaggeredGridCells.Fixed(count = 6),
            ) {
                for (category in categories) {
                    if (category.isNotBlank()) {
                        item(span = StaggeredGridItemSpan.FullLine) {
                            Text(
                                text = category,
                            )
                        }
                    }
                    val groupEmojis = groupedEmojis[category].orEmpty()
                    items(groupEmojis) { emoji ->
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(CornerSize.l))
                                    .clickable {
                                        onInsert?.invoke(emoji)
                                    },
                        ) {
                            CustomImage(
                                modifier =
                                    Modifier
                                        .padding(Spacing.m)
                                        .widthIn(min = IconSize.m),
                                url = emoji.url,
                                contentScale = ContentScale.FillWidth,
                            )
                        }
                    }
                }
            }
        }
    }
}
