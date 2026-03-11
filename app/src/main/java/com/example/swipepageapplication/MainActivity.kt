package com.example.swipepageapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.swipepageapplication.navigation.SpatialNavHost
import com.example.swipepageapplication.ui.theme.SwipePageApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SwipePageApplicationTheme {
                SpatialNavHost()
            }
        }
    }
}
