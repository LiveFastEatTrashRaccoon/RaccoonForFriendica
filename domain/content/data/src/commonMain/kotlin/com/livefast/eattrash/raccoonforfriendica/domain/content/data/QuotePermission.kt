package com.livefast.eattrash.raccoonforfriendica.domain.content.data

sealed interface QuotePermission {
    data object AutomaticallyApprove : QuotePermission
    data object ManualApprove : QuotePermission
    data object OnlyFollowers : QuotePermission
    data object Unauthorized : QuotePermission
}
