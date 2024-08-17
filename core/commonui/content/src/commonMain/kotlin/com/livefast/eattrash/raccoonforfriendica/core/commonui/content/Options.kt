package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

sealed interface OptionId {
    data object Edit : OptionId

    data object Delete : OptionId

    data object Share : OptionId
}

data class Option(
    val id: OptionId,
    val label: String,
)
