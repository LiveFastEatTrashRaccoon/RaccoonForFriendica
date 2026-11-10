package com.livefast.eattrash.raccoonforfriendica.core.navigation

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.SharedTransitionScope.OverlayClip
import androidx.compose.animation.SharedTransitionScope.PlaceholderSize
import androidx.compose.animation.SharedTransitionScope.ResizeMode
import androidx.compose.animation.SharedTransitionScope.SharedContentState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

internal object NoOpSharedTransitionScope : SharedTransitionScope {
    override val isTransitionActive: Boolean get() = false

    override fun Modifier.skipToLookaheadSize(enabled: () -> Boolean): Modifier = this

    override fun Modifier.skipToLookaheadPosition(enabled: () -> Boolean): Modifier = this

    override fun Modifier.renderInSharedTransitionScopeOverlay(
        zIndexInOverlay: Float,
        renderInOverlay: () -> Boolean,
    ): Modifier = this

    override fun Modifier.sharedElement(
        sharedContentState: SharedContentState,
        animatedVisibilityScope: AnimatedVisibilityScope,
        boundsTransform: BoundsTransform,
        placeholderSize: PlaceholderSize,
        renderInOverlayDuringTransition: Boolean,
        zIndexInOverlay: Float,
        clipInOverlayDuringTransition: OverlayClip,
    ): Modifier = this

    override fun Modifier.sharedBounds(
        sharedContentState: SharedContentState,
        animatedVisibilityScope: AnimatedVisibilityScope,
        enter: EnterTransition,
        exit: ExitTransition,
        boundsTransform: BoundsTransform,
        resizeMode: ResizeMode,
        placeholderSize: PlaceholderSize,
        renderInOverlayDuringTransition: Boolean,
        zIndexInOverlay: Float,
        clipInOverlayDuringTransition: OverlayClip,
    ): Modifier = this

    override fun Modifier.sharedElementWithCallerManagedVisibility(
        sharedContentState: SharedContentState,
        visible: Boolean,
        boundsTransform: BoundsTransform,
        placeholderSize: PlaceholderSize,
        renderInOverlayDuringTransition: Boolean,
        zIndexInOverlay: Float,
        clipInOverlayDuringTransition: OverlayClip,
    ): Modifier = this

    override fun OverlayClip(clipShape: Shape): OverlayClip = object : OverlayClip {
        override fun getClipPath(
            sharedContentState: SharedContentState,
            bounds: Rect,
            layoutDirection: LayoutDirection,
            density: Density,
        ): Path? = null
    }

    override fun LayoutCoordinates.toLookaheadCoordinates(): LayoutCoordinates = this

    override val Placeable.PlacementScope.lookaheadScopeCoordinates: LayoutCoordinates
        get() = error("lookaheadScopeCoordinates not supported in NoOpSharedTransitionScope")
}
