package com.livefast.eattrash.raccoonforfriendica.domain.content.data

import androidx.compose.runtime.Composable
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings

sealed interface QuotePolicy {
    data object Public : QuotePolicy
    data object Followers : QuotePolicy
    data object Nobody : QuotePolicy
}

@Composable
fun QuotePolicy.toReadableName(): String = when (this) {
    QuotePolicy.Followers -> LocalStrings.current.quotePolicyFollowers
    QuotePolicy.Nobody -> LocalStrings.current.quotePolicyNobody
    QuotePolicy.Public -> LocalStrings.current.quotePolicyPublic
}
