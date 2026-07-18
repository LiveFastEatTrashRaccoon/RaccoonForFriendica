package com.livefast.eattrash.raccoonforfriendica.domain.content.data

sealed interface QuotePolicy {
    data object Public : QuotePolicy
    data object Followers : QuotePolicy
    data object Nobody : QuotePolicy
}
