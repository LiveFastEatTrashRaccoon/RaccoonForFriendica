package com.livefast.eattrash.raccoonforfriendica.feature.circles.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.getScreenModel
import org.koin.core.parameter.parametersOf

class CircleDetailScreen(
    val id: String,
) : Screen {
    override val key: ScreenKey
        get() = super.key + id

    @Composable
    override fun Content() {
        val model = getScreenModel<CircleDetailMviModel>(parameters = { parametersOf(id) })
        val uiState by model.uiState.collectAsState()
    }
}
