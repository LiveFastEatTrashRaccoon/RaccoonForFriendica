package com.livefast.eattrash.raccoonforfriendica.core.utils.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.yield
import kotlin.math.roundToLong

@Composable
fun getAnimatedDots(
    char: String = ".",
    durationMillis: Long = 2500,
): String {
    val maxStep = 4
    val interval = (durationMillis / maxStep.toFloat()).roundToLong()
    var step by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(interval)
            step = (step + 1) % maxStep
            yield()
        }
    }
    return char.repeat(step)
}
