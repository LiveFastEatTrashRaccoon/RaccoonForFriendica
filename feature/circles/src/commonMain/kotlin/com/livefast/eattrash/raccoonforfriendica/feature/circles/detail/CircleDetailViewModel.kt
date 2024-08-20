package com.livefast.eattrash.raccoonforfriendica.feature.circles.detail

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import kotlinx.coroutines.launch

class CircleDetailViewModel(
    private val id: String,
) : DefaultMviModel<CircleDetailMviModel.Intent, CircleDetailMviModel.State, CircleDetailMviModel.Effect>(
        initialState = CircleDetailMviModel.State(),
    ),
    CircleDetailMviModel {
    init {
        screenModelScope.launch {
        }
    }
}
