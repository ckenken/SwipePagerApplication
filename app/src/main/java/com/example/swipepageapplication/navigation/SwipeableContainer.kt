package com.example.swipepageapplication.navigation

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.abs

@Composable
fun SwipeableContainer(
    onSwipe: (SwipeDirection) -> Unit,
    swipeThreshold: Float = 100f,
    content: @Composable () -> Unit
) {
    var totalDrag by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { totalDrag = Offset.Zero },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        totalDrag += dragAmount
                    },
                    onDragEnd = {
                        val (dx, dy) = totalDrag
                        if (maxOf(abs(dx), abs(dy)) > swipeThreshold) {
                            if (abs(dx) > abs(dy)) {
                                onSwipe(if (dx > 0) SwipeDirection.Left else SwipeDirection.Right)
                            } else {
                                onSwipe(if (dy > 0) SwipeDirection.Up else SwipeDirection.Down)
                            }
                        }
                    }
                )
            }
    ) {
        content()
    }
}
