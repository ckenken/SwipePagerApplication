package com.example.swipepageapplication.navigation

import androidx.compose.ui.graphics.Color

enum class SwipeDirection { Up, Down, Left, Right }

enum class Page(val route: String, val displayName: String, val color: Color) {
    Home("home", "Home", Color(0xFF2196F3)),
    B("b", "Page B", Color(0xFF4CAF50)),
    C("c", "Page C", Color(0xFFFF9800)),
    D("d", "Page D", Color(0xFFF44336)),
    E("e", "Page E", Color(0xFF9C27B0)),
    F("f", "Page F", Color(0xFF009688));

    companion object {
        fun fromRoute(route: String): Page? = entries.find { it.route == route }
    }
}

data class PageNeighbors(
    val up: Page? = null,
    val down: Page? = null,
    val left: Page? = null,
    val right: Page? = null,
)

val spatialGraph: Map<Page, PageNeighbors> = mapOf(
    Page.Home to PageNeighbors(up = Page.B, down = Page.C, left = Page.E, right = Page.F),
    Page.B    to PageNeighbors(down = Page.Home),
    Page.C    to PageNeighbors(up = Page.Home, down = Page.D),
    Page.D    to PageNeighbors(up = Page.C),
    Page.E    to PageNeighbors(right = Page.Home),
    Page.F    to PageNeighbors(left = Page.Home),
)

fun PageNeighbors.getNeighbor(direction: SwipeDirection): Page? = when (direction) {
    SwipeDirection.Up -> up
    SwipeDirection.Down -> down
    SwipeDirection.Left -> left
    SwipeDirection.Right -> right
}
