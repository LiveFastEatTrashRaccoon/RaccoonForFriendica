package accountdetail.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.ancillaryTextAlpha
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.ContentBody
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.FieldModel

@Composable
internal fun AccountFields(
    fields: List<FieldModel> = emptyList(),
    modifier: Modifier = Modifier,
    onOpenUrl: ((String) -> Unit)? = null,
) {
    val ancillaryColor = MaterialTheme.colorScheme.onBackground.copy(ancillaryTextAlpha)

    Column(
        modifier = modifier.padding(horizontal = Spacing.m),
    ) {
        for (field in fields) {
            Row {
                Text(
                    modifier = Modifier.weight(0.4f),
                    text = field.key.uppercase(),
                    color = ancillaryColor,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Light),
                )
                ContentBody(
                    modifier = Modifier.weight(1f),
                    content = field.value,
                    onOpenUrl = onOpenUrl,
                )
                if (field.verified) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = null,
                        tint = Color.Green,
                    )
                }
            }
        }
    }
}
