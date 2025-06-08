package com.livefast.eattrash.raccoonforfriendica.feature.manageblocks.data

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel

sealed interface ManageBlocksItem {
    data class User(val user: UserModel) : ManageBlocksItem

    data class StopWord(val word: String) : ManageBlocksItem
}

internal val ManageBlocksItem.safeKey: String
    get() =
        when (this) {
            is ManageBlocksItem.User -> user.id
            is ManageBlocksItem.StopWord -> word
        }
