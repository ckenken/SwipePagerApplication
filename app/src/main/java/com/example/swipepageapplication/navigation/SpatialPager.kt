package com.example.swipepageapplication.navigation

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.abs

private enum class DragAxis { Horizontal, Vertical }

@Composable
fun SpatialPager(
    currentPage: Page,
    onPageChanged: (Page) -> Unit,
    modifier: Modifier = Modifier,
    pageContent: @Composable (Page) -> Unit,
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var dragAxis by remember { mutableStateOf<DragAxis?>(null) }
    var animJob by remember { mutableStateOf<Job?>(null) }
    val scope = rememberCoroutineScope()

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .clipToBounds()
            .pointerInput(currentPage) {
                val w = size.width.toFloat()
                val h = size.height.toFloat()
                val nbrs = spatialGraph[currentPage]

                detectDragGestures(
                    onDragStart = {
                        animJob?.cancel()
                        offsetX = 0f
                        offsetY = 0f
                        dragAxis = null
                    },
                    onDrag = { change, amount ->
                        change.consume()
                        if (dragAxis == null) {
                            dragAxis = if (abs(amount.x) >= abs(amount.y))
                                DragAxis.Horizontal else DragAxis.Vertical
                        }
                        when (dragAxis) {
                            DragAxis.Horizontal -> {
                                val next = offsetX + amount.x
                                offsetX = when {
                                    next > 0 && nbrs?.left == null -> 0f
                                    next < 0 && nbrs?.right == null -> 0f
                                    else -> next.coerceIn(-w, w)
                                }
                            }
                            DragAxis.Vertical -> {
                                val next = offsetY + amount.y
                                offsetY = when {
                                    next > 0 && nbrs?.up == null -> 0f
                                    next < 0 && nbrs?.down == null -> 0f
                                    else -> next.coerceIn(-h, h)
                                }
                            }
                            null -> {}
                        }
                    },
                    onDragEnd = {
                        val snapThreshold = 0.3f
                        val target = when (dragAxis) {
                            DragAxis.Horizontal ->
                                if (offsetX > 0) nbrs?.left else nbrs?.right
                            DragAxis.Vertical ->
                                if (offsetY > 0) nbrs?.up else nbrs?.down
                            null -> null
                        }

                        animJob = scope.launch {
                            val spec = spring<Float>(stiffness = 500f)

                            when (dragAxis) {
                                DragAxis.Horizontal -> {
                                    val snap = abs(offsetX) > w * snapThreshold && target != null
                                    val end = if (snap) (if (offsetX > 0) w else -w) else 0f
                                    animate(offsetX, end, animationSpec = spec) { v, _ -> offsetX = v }
                                    if (snap && target != null) {
                                        offsetX = 0f
                                        onPageChanged(target)
                                    }
                                }
                                DragAxis.Vertical -> {
                                    val snap = abs(offsetY) > h * snapThreshold && target != null
                                    val end = if (snap) (if (offsetY > 0) h else -h) else 0f
                                    animate(offsetY, end, animationSpec = spec) { v, _ -> offsetY = v }
                                    if (snap && target != null) {
                                        offsetY = 0f
                                        onPageChanged(target)
                                    }
                                }
                                null -> {}
                            }
                            dragAxis = null
                        }
                    },
                )
            }
    ) {
        val wPx = constraints.maxWidth.toFloat()
        val hPx = constraints.maxHeight.toFloat()
        val nbrs = spatialGraph[currentPage]

        // Determine which neighbor is being revealed
        val targetPage = when {
            offsetX > 0 -> nbrs?.left
            offsetX < 0 -> nbrs?.right
            offsetY > 0 -> nbrs?.up
            offsetY < 0 -> nbrs?.down
            else -> null
        }

        // Render target page first (lower z-order, slides in from edge)
        targetPage?.let { target ->
            val tx = when {
                offsetX > 0 -> offsetX - wPx
                offsetX < 0 -> offsetX + wPx
                else -> 0f
            }
            val ty = when {
                offsetY > 0 -> offsetY - hPx
                offsetY < 0 -> offsetY + hPx
                else -> 0f
            }
            Box(
                Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        translationX = tx
                        translationY = ty
                    }
            ) {
                pageContent(target)
            }
        }

        // Render current page on top (moves with finger)
        Box(
            Modifier
                .fillMaxSize()
                .graphicsLayer {
                    translationX = offsetX
                    translationY = offsetY
                }
        ) {
            pageContent(currentPage)
        }
    }
}
