package com.livefast.eattrash.raccoonforfriendica.feature.profile

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarState
import androidx.compose.runtime.compositionLocalOf

internal interface ProfileTopAppBarStateWrapper {
    @OptIn(ExperimentalMaterial3Api::class)
    val topAppBarState: TopAppBarState
}

internal val LocalProfileTopAppBarStateWrapper =
    compositionLocalOf<ProfileTopAppBarStateWrapper> {
        object : ProfileTopAppBarStateWrapper {
            @OptIn(ExperimentalMaterial3Api::class)
            override val topAppBarState: TopAppBarState
                get() = throw IllegalStateException("No TopAppBarState found in composition locals")
        }
    }
