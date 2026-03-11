package com.example.swipepageapplication.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.example.swipepageapplication.screens.ChatScreen
import com.example.swipepageapplication.screens.DetailScreen
import com.example.swipepageapplication.screens.FeedScreen
import com.example.swipepageapplication.screens.HomeScreen
import com.example.swipepageapplication.screens.ProfileScreen
import com.example.swipepageapplication.screens.StatsScreen

@Composable
fun SpatialNavHost(navController: NavHostController = rememberNavController()) {
    var lastDirection by remember { mutableStateOf<SwipeDirection?>(null) }

    fun handleSwipe(currentPage: Page, direction: SwipeDirection) {
        val target = spatialGraph[currentPage]?.getNeighbor(direction) ?: return
        lastDirection = direction
        navController.navigate(target.route) {
            launchSingleTop = true
        }
    }

    NavHost(
        navController = navController,
        startDestination = Page.Home.route,
        enterTransition = {
            when (lastDirection) {
                SwipeDirection.Up -> slideInVertically { -it }
                SwipeDirection.Down -> slideInVertically { it }
                SwipeDirection.Left -> slideInHorizontally { -it }
                SwipeDirection.Right -> slideInHorizontally { it }
                null -> fadeIn()
            }
        },
        exitTransition = {
            when (lastDirection) {
                SwipeDirection.Up -> slideOutVertically { it }
                SwipeDirection.Down -> slideOutVertically { -it }
                SwipeDirection.Left -> slideOutHorizontally { it }
                SwipeDirection.Right -> slideOutHorizontally { -it }
                null -> fadeOut()
            }
        },
        popEnterTransition = { fadeIn() },
        popExitTransition = { fadeOut() },
    ) {
        composable(
            route = Page.Home.route,
            deepLinks = listOf(navDeepLink { uriPattern = "swipepage://app/${Page.Home.route}" }),
        ) {
            SwipeableContainer(onSwipe = { handleSwipe(Page.Home, it) }) {
                Box(Modifier.fillMaxSize()) {
                    HomeScreen()
                    NavigationHints(Page.Home)
                }
            }
        }

        composable(
            route = Page.B.route,
            deepLinks = listOf(navDeepLink { uriPattern = "swipepage://app/${Page.B.route}" }),
        ) {
            SwipeableContainer(onSwipe = { handleSwipe(Page.B, it) }) {
                Box(Modifier.fillMaxSize()) {
                    ProfileScreen()
                    NavigationHints(Page.B)
                }
            }
        }

        composable(
            route = Page.C.route,
            deepLinks = listOf(navDeepLink { uriPattern = "swipepage://app/${Page.C.route}" }),
        ) {
            SwipeableContainer(onSwipe = { handleSwipe(Page.C, it) }) {
                Box(Modifier.fillMaxSize()) {
                    FeedScreen()
                    NavigationHints(Page.C)
                }
            }
        }

        composable(
            route = Page.D.route,
            deepLinks = listOf(navDeepLink { uriPattern = "swipepage://app/${Page.D.route}" }),
        ) {
            SwipeableContainer(onSwipe = { handleSwipe(Page.D, it) }) {
                Box(Modifier.fillMaxSize()) {
                    DetailScreen()
                    NavigationHints(Page.D)
                }
            }
        }

        composable(
            route = Page.E.route,
            deepLinks = listOf(navDeepLink { uriPattern = "swipepage://app/${Page.E.route}" }),
        ) {
            SwipeableContainer(onSwipe = { handleSwipe(Page.E, it) }) {
                Box(Modifier.fillMaxSize()) {
                    ChatScreen()
                    NavigationHints(Page.E)
                }
            }
        }

        composable(
            route = Page.F.route,
            deepLinks = listOf(navDeepLink { uriPattern = "swipepage://app/${Page.F.route}" }),
        ) {
            SwipeableContainer(onSwipe = { handleSwipe(Page.F, it) }) {
                Box(Modifier.fillMaxSize()) {
                    StatsScreen()
                    NavigationHints(Page.F)
                }
            }
        }
    }
}

@Composable
private fun NavigationHints(page: Page) {
    val neighbors = spatialGraph[page] ?: return

    Box(modifier = Modifier.fillMaxSize()) {
        neighbors.up?.let {
            HintPill(
                text = "↑ ${it.displayName}",
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 6.dp)
            )
        }
        neighbors.down?.let {
            HintPill(
                text = "↓ ${it.displayName}",
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            )
        }
        neighbors.left?.let {
            HintPill(
                text = "← ${it.displayName}",
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 6.dp)
            )
        }
        neighbors.right?.let {
            HintPill(
                text = "${it.displayName} →",
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 6.dp)
            )
        }
    }
}

@Composable
private fun HintPill(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.4f), shape = RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        color = Color.White,
        fontSize = 11.sp
    )
}
