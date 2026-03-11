package com.example.swipepageapplication.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val AccentColor = Color(0xFFF44336)
private val tags = listOf("Photography", "Nature", "Landscape", "HDR", "Golden Hour")

@Composable
fun DetailScreen() {
    var isFavorite by remember { mutableStateOf(false) }
    var userRating by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Hero Image Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(AccentColor),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Mountain Sunrise", color = Color.White.copy(alpha = 0.3f), fontSize = 36.sp, fontWeight = FontWeight.Bold)
                Text("Photo Preview", color = Color.White.copy(alpha = 0.5f), fontSize = 14.sp)
            }
        }

        // Content
        Column(modifier = Modifier.padding(20.dp)) {
            // Title & Actions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Mountain Sunrise at Dawn", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Alice Chen", fontSize = 14.sp, color = Color.Gray)
                        Text("  ·  March 2024", fontSize = 14.sp, color = Color.LightGray)
                    }
                }
                IconToggleButton(
                    checked = isFavorite,
                    onCheckedChange = { isFavorite = it }
                ) {
                    Icon(
                        if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) AccentColor else Color.Gray,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Rating
            Text("Your Rating", fontWeight = FontWeight.Medium, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                for (i in 1..5) {
                    IconToggleButton(
                        checked = i <= userRating,
                        onCheckedChange = { userRating = if (userRating == i) 0 else i },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = "Star $i",
                            tint = if (i <= userRating) Color(0xFFFFC107) else Color.LightGray,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
                if (userRating > 0) {
                    Text(
                        "$userRating / 5",
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 8.dp),
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Description
            Text(
                "A breathtaking capture of the first light breaking over the mountain range. " +
                    "The golden hues blend with the cool blues of the retreating night sky, " +
                    "creating a stunning contrast that highlights the rugged terrain below.",
                fontSize = 14.sp,
                color = Color.DarkGray,
                lineHeight = 22.sp,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tags
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                tags.take(3).forEach { tag ->
                    AssistChip(
                        onClick = { },
                        label = { Text(tag, fontSize = 11.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Divider()
            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ElevatedButton(
                    onClick = { },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.elevatedButtonColors(containerColor = AccentColor, contentColor = Color.White)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Share")
                }
                FilledTonalButton(
                    onClick = { },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Download")
                }
            }
        }
    }
}
