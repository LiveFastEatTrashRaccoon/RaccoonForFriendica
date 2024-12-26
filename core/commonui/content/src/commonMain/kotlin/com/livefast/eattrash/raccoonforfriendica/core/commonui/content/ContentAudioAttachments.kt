package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.CornerSize
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.AudioPlayer
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AttachmentModel

@Composable
fun ContentAudioAttachments(
    attachments: List<AttachmentModel>,
    modifier: Modifier = Modifier,
    cornerSize: Dp = CornerSize.xl,
) {
    AudioPlayer(
        modifier = modifier.clip(RoundedCornerShape(cornerSize)),
        urls = attachments.map { it.url },
        titles = attachments.map { it.description.orEmpty() },
    )
}
