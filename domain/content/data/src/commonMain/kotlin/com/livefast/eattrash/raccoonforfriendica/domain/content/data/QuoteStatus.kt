package com.livefast.eattrash.raccoonforfriendica.domain.content.data

sealed interface QuoteStatus {
    data object Pending : QuoteStatus
    data object Accepted : QuoteStatus
    data object Rejected : QuoteStatus
    data object Deleted : QuoteStatus
    data object BlockedAccount : QuoteStatus
    data object BlockedDomain : QuoteStatus
    data object MutedAccount : QuoteStatus
    data object Unauthorized : QuoteStatus
}
