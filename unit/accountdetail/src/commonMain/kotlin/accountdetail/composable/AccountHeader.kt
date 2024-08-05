package accountdetail.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.ancillaryTextAlpha
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomImage
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.PlaceholderImage
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AccountModel

@Composable
fun AccountHeader(
    account: AccountModel,
    modifier: Modifier = Modifier,
) {
    val banner = account.header.orEmpty()
    val avatar = account.avatar.orEmpty()
    val avatarSize = 100.dp
    val fullColor = MaterialTheme.colorScheme.onBackground
    val ancillaryColor = MaterialTheme.colorScheme.onBackground.copy(ancillaryTextAlpha)

    Column(
        modifier = modifier,
    ) {
        Box {
            CustomImage(
                modifier =
                    Modifier
                        .padding(bottom = avatarSize * 0.8f)
                        .aspectRatio(2f),
                url = banner,
                contentScale = ContentScale.FillBounds,
            )

            Row(
                modifier =
                    Modifier
                        .align(Alignment.BottomStart)
                        .offset(
                            x = Spacing.m,
                            y = -avatarSize * 0.1f,
                        ),
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
                        title = account.displayName ?: account.handle ?: "?",
                    )
                }
            }
        }

        Column(
            modifier = Modifier.padding(horizontal = Spacing.m),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            Text(
                text = account.displayName ?: account.username ?: "",
                style = MaterialTheme.typography.titleMedium,
                color = fullColor,
            )
            Text(
                text = account.handle ?: account.username ?: "",
                style = MaterialTheme.typography.titleSmall,
                color = ancillaryColor,
            )
        }
    }
}
