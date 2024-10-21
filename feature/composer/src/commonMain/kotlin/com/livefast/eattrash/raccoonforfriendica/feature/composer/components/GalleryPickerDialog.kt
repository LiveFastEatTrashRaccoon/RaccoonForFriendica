package com.livefast.eattrash.raccoonforfriendica.feature.composer.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.CornerSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomDropDown
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.ListLoadingIndicator
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.AlbumImageItem
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.utils.isNearTheEnd
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AttachmentModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.MediaAlbumModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryPickerDialog(
    sheetScope: CoroutineScope = rememberCoroutineScope(),
    sheetState: SheetState = rememberModalBottomSheetState(),
    currentAlbum: String? = null,
    albums: List<MediaAlbumModel> = emptyList(),
    canFetchMore: Boolean = false,
    loading: Boolean = false,
    onInitialLoad: (() -> Unit)? = null,
    onLoadMorePhotos: (() -> Unit)? = null,
    onAlbumChanged: ((String) -> Unit)? = null,
    photos: List<AttachmentModel> = emptyList(),
    onClose: ((List<AttachmentModel>?) -> Unit)? = null,
) {
    val lazyGridState = rememberLazyStaggeredGridState()
    val fullColor = MaterialTheme.colorScheme.onBackground
    var optionsOffset by remember { mutableStateOf(Offset.Zero) }
    var optionsMenuOpen by remember { mutableStateOf(false) }
    val selection = mutableStateListOf<AttachmentModel>()

    LaunchedEffect(Unit) {
        if (currentAlbum == null) {
            onInitialLoad?.invoke()
        }
    }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = {
            onClose?.invoke(null)
        },
        content = {
            Column(
                modifier = Modifier.padding(bottom = Spacing.xl),
            ) {
                Box {
                    Text(
                        modifier = Modifier.fillMaxWidth().align(Alignment.Center),
                        textAlign = TextAlign.Center,
                        text = LocalStrings.current.pickFromGalleryDialogTitle,
                        style = MaterialTheme.typography.titleMedium,
                        color = fullColor,
                    )
                    Button(
                        modifier =
                            Modifier
                                .padding(end = Spacing.s)
                                .align(Alignment.CenterEnd),
                        onClick = {
                            sheetScope
                                .launch {
                                    sheetState.hide()
                                }.invokeOnCompletion {
                                    if (selection.isNotEmpty()) {
                                        onClose?.invoke(selection)
                                    } else {
                                        onClose?.invoke(null)
                                    }
                                }
                        },
                    ) {
                        Text(
                            text =
                                buildString {
                                    append(LocalStrings.current.buttonConfirm)
                                    if (selection.isNotEmpty()) {
                                        append(" (")
                                        append(selection.size)
                                        append(")")
                                    }
                                },
                        )
                    }
                }
                Spacer(modifier = Modifier.height(Spacing.s))

                LazyVerticalStaggeredGrid(
                    modifier =
                        Modifier
                            .padding(horizontal = Spacing.s)
                            .heightIn(min = 500.dp),
                    state = lazyGridState,
                    columns = StaggeredGridCells.Fixed(count = 2),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.s),
                    verticalItemSpacing = Spacing.s,
                ) {
                    item(span = StaggeredGridItemSpan.FullLine) {
                        Row(
                            modifier =
                                Modifier
                                    .border(
                                        width = Dp.Hairline,
                                        color = MaterialTheme.colorScheme.onBackground,
                                        shape = RoundedCornerShape(CornerSize.xl),
                                    ).clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                    ) {
                                        optionsMenuOpen = !optionsMenuOpen
                                    },
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                modifier =
                                    Modifier
                                        .weight(1f)
                                        .padding(start = Spacing.m),
                                text = currentAlbum.orEmpty(),
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Box {
                                IconButton(
                                    modifier =
                                        Modifier.onGloballyPositioned {
                                            optionsOffset = it.positionInParent()
                                        },
                                    onClick = {
                                        optionsMenuOpen = !optionsMenuOpen
                                    },
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = null,
                                    )
                                }

                                CustomDropDown(
                                    expanded = optionsMenuOpen,
                                    onDismiss = {
                                        optionsMenuOpen = false
                                    },
                                    offset =
                                        with(LocalDensity.current) {
                                            DpOffset(
                                                x = optionsOffset.x.toDp(),
                                                y = optionsOffset.y.toDp(),
                                            )
                                        },
                                ) {
                                    albums.forEach { album ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(album.name)
                                            },
                                            onClick = {
                                                optionsMenuOpen = false
                                                onAlbumChanged?.invoke(album.name)
                                            },
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (!loading && photos.isEmpty()) {
                        item(span = StaggeredGridItemSpan.FullLine) {
                            Text(
                                modifier = Modifier.fillMaxWidth().padding(top = Spacing.m),
                                text = LocalStrings.current.messageEmptyAlbum,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                    }

                    itemsIndexed(
                        items = photos,
                        key = { _, e -> e.id },
                    ) { idx, attachment ->
                        val selectionIndex = selection.indexOf(attachment)
                        Box {
                            AlbumImageItem(
                                attachment = attachment,
                                onClick = {
                                    if (selectionIndex >= 0) {
                                        selection -= attachment
                                    } else {
                                        selection += attachment
                                    }
                                },
                            )
                            if (selectionIndex >= 0) {
                                Box(
                                    modifier =
                                        Modifier
                                            .padding(
                                                top = Spacing.s,
                                                end = Spacing.s,
                                            ).align(Alignment.TopEnd)
                                            .size(IconSize.s)
                                            .background(
                                                color = MaterialTheme.colorScheme.primaryContainer,
                                                shape = CircleShape,
                                            ),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = "${selectionIndex + 1}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    )
                                }
                            }
                        }

                        val isNearTheEnd = idx.isNearTheEnd(photos)
                        if (isNearTheEnd && !loading && canFetchMore) {
                            onLoadMorePhotos?.invoke()
                        }
                    }

                    item(span = StaggeredGridItemSpan.FullLine) {
                        if (loading && canFetchMore) {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center,
                            ) {
                                ListLoadingIndicator()
                            }
                        }
                    }

                    item(span = StaggeredGridItemSpan.FullLine) {
                        Spacer(modifier = Modifier.height(Spacing.xl))
                    }
                }
            }
        },
    )
}
