package com.livefast.eattrash.raccoonforfriendica.feature.report.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.CornerSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.ancillaryTextAlpha
import com.livefast.eattrash.raccoonforfriendica.core.htmlparse.parseHtml
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.RuleModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectViolatedRulesDialog(
    initialSelection: List<String> = emptyList(),
    rules: List<RuleModel> = emptyList(),
    onClose: ((List<String>?) -> Unit)? = null,
) {
    val selectedIds =
        remember {
            mutableStateListOf<String>().apply {
                addAll(initialSelection)
            }
        }

    BasicAlertDialog(
        modifier = Modifier.clip(RoundedCornerShape(CornerSize.xxl)),
        onDismissRequest = {
            onClose?.invoke(null)
        },
    ) {
        Column(
            modifier =
                Modifier
                    .background(color = MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp))
                    .padding(Spacing.m),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
        ) {
            Text(
                text = LocalStrings.current.nodeInfoSectionRules,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.height(Spacing.s))
            LazyColumn(
                modifier = Modifier.fillMaxWidth().height(400.dp),
            ) {
                items(rules) { item ->
                    val selected = selectedIds.contains(item.id)
                    RuleItem(
                        selected = selected,
                        rule = item,
                        onSelected = {
                            val ruleId = item.id
                            if (selectedIds.contains(ruleId)) {
                                selectedIds.remove(ruleId)
                            } else {
                                selectedIds.add(item.id)
                            }
                        },
                    )
                }
            }
            Spacer(modifier = Modifier.height(Spacing.xs))
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.s),
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = {
                        onClose?.invoke(null)
                    },
                ) {
                    Text(text = LocalStrings.current.buttonCancel)
                }
                Button(
                    onClick = {
                        onClose?.invoke(selectedIds)
                    },
                ) {
                    Text(text = LocalStrings.current.buttonConfirm)
                }
            }
        }
    }
}

@Composable
private fun RuleItem(
    rule: RuleModel,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onSelected: (() -> Unit)? = null,
) {
    val fullColor = MaterialTheme.colorScheme.onBackground

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Spacing.s),
        verticalAlignment = Alignment.Top,
    ) {
        val annotatedContent =
            rule.text.parseHtml(
                linkColor = MaterialTheme.colorScheme.primary,
                quoteColor = MaterialTheme.colorScheme.onBackground.copy(ancillaryTextAlpha),
            )
        Text(
            modifier = Modifier.weight(1f),
            text = annotatedContent,
            style = MaterialTheme.typography.bodySmall,
            color = fullColor,
        )

        Checkbox(
            modifier = Modifier.size(IconSize.s),
            checked = selected,
            onCheckedChange = {
                onSelected?.invoke()
            },
        )
    }
}
