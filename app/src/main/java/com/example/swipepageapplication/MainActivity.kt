package com.example.swipepageapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf
import com.example.swipepageapplication.navigation.Page
import com.example.swipepageapplication.navigation.SpatialNavHost
import com.example.swipepageapplication.ui.theme.SwipePageApplicationTheme

class MainActivity : ComponentActivity() {
    private val deepLinkTarget = mutableStateOf<Page?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val initialPage = parseDeepLink(intent) ?: Page.Home
        setContent {
            SwipePageApplicationTheme {
                SpatialNavHost(
                    initialPage = initialPage,
                    externalNavigation = deepLinkTarget,
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        parseDeepLink(intent)?.let { deepLinkTarget.value = it }
    }

    private fun parseDeepLink(intent: Intent?): Page? =
        intent?.data?.lastPathSegment?.let { Page.fromRoute(it) }
}
