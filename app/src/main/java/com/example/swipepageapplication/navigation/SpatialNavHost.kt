package com.example.swipepageapplication.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.swipepageapplication.screens.ChatScreen
import com.example.swipepageapplication.screens.DetailScreen
import com.example.swipepageapplication.screens.FeedScreen
import com.example.swipepageapplication.screens.HomeScreen
import com.example.swipepageapplication.screens.ProfileScreen
import com.example.swipepageapplication.screens.StatsScreen

@Composable
fun SpatialNavHost(
    initialPage: Page = Page.Home,
    externalNavigation: State<Page?> = remember { mutableStateOf(null) },
) {
    var currentPage by remember { mutableStateOf(initialPage) }
    val backStack = remember { mutableStateListOf<Page>() }

    // Handle deep links arriving while the app is already running
    LaunchedEffect(externalNavigation.value) {
        externalNavigation.value?.let { page ->
            if (page != currentPage) {
                backStack.add(currentPage)
                currentPage = page
            }
        }
    }

    BackHandler(backStack.isNotEmpty()) {
        currentPage = backStack.removeAt(backStack.lastIndex)
    }

    SpatialPager(
        currentPage = currentPage,
        onPageChanged = { nextPage ->
            backStack.add(currentPage)
            currentPage = nextPage
        },
    ) { page ->
        Box(Modifier.fillMaxSize()) {
            when (page) {
                Page.Home -> HomeScreen()
                Page.B -> ProfileScreen()
                Page.C -> FeedScreen()
                Page.D -> DetailScreen()
                Page.E -> ChatScreen()
                Page.F -> StatsScreen()
            }
            NavigationHints(page)
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
