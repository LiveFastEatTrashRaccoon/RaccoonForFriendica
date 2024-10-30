package com.livefast.eattrash.raccoonforfriendica.feature.composer.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.AlbumImageItem
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.OptionId
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.toOption
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AttachmentModel

@Composable
internal fun AttachmentsGrid(
    attachments: List<AttachmentModel>,
    autoloadImages: Boolean = true,
    modifier: Modifier = Modifier,
    onDelete: ((AttachmentModel) -> Unit)? = null,
    onEditDescription: ((AttachmentModel) -> Unit)? = null,
) {
    val columns =
        listOf(
            attachments.filterIndexed { i, _ -> (i % 2) == 0 },
            attachments.filterIndexed { i, _ -> (i % 2) == 1 },
        )

    Text(
        modifier =
            Modifier.padding(
                top = Spacing.m,
                start = 10.dp,
                end = 10.dp,
            ),
        text = LocalStrings.current.createPostAttachmentsSection,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onBackground,
    )

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Spacing.s),
    ) {
        for (column in columns) {
            AttachmentsColumn(
                modifier = Modifier.weight(0.5f),
                autoloadImages = autoloadImages,
                attachments = column,
                onEditDescription = onEditDescription,
                onDelete = onDelete,
            )
        }
    }
}

@Composable
private fun AttachmentsColumn(
    attachments: List<AttachmentModel>,
    modifier: Modifier = Modifier,
    autoloadImages: Boolean = true,
    onDelete: ((AttachmentModel) -> Unit)? = null,
    onEditDescription: ((AttachmentModel) -> Unit)? = null,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Spacing.s),
    ) {
        for (attachment in attachments) {
            AlbumImageItem(
                attachment = attachment,
                autoload = autoloadImages,
                options =
                    buildList {
                        this += OptionId.Edit.toOption()
                        this += OptionId.Delete.toOption()
                    },
                onOptionSelected = { optionId ->
                    when (optionId) {
                        OptionId.Edit -> onEditDescription?.invoke(attachment)
                        OptionId.Delete -> onDelete?.invoke(attachment)
                        else -> Unit
                    }
                },
            )
        }
    }
}
