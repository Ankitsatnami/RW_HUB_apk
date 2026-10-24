package com.roboticswala.hub.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.roboticswala.hub.data.models.AchievementBannerItem
import com.roboticswala.hub.ui.theme.CircuitSuccess
import com.roboticswala.hub.ui.theme.CyberCyan
import com.roboticswala.hub.ui.theme.DarkSurfaceElevated
import com.roboticswala.hub.ui.theme.ElectricBlue
import com.roboticswala.hub.ui.theme.LightSurfaceElevated
import com.roboticswala.hub.ui.theme.TextPrimaryDark
import com.roboticswala.hub.ui.theme.TextPrimaryLight
import kotlinx.coroutines.delay

@Composable
fun AchievementBannerCarousel(
    banners: List<AchievementBannerItem>,
    isDark: Boolean,
    onNavigateToAchievements: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (banners.isEmpty()) return

    var currentIndex by remember { mutableStateOf(0) }

    // Auto-advance sliding timer every 3.5 seconds
    LaunchedEffect(banners.size) {
        if (banners.size <= 1) return@LaunchedEffect
        while (true) {
            delay(3500)
            currentIndex = (currentIndex + 1) % banners.size
        }
    }

    val currentBanner = banners[currentIndex % banners.size]

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "🏆 Lab Achievements",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (isDark) TextPrimaryDark else TextPrimaryLight
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(CircuitSuccess.copy(alpha = 0.2f))
                        .border(1.dp, CircuitSuccess.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "LIVE SHOWCASE",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                        color = CircuitSuccess
                    )
                }
            }
            Text(
                text = "View All ➔",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = if (isDark) CyberCyan else ElectricBlue,
                modifier = Modifier.clickable { onNavigateToAchievements() }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Sliding Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(18.dp))
                .clickable { onNavigateToAchievements() },
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = if (isDark) DarkSurfaceElevated else LightSurfaceElevated),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, if (isDark) CyberCyan.copy(alpha = 0.5f) else ElectricBlue.copy(alpha = 0.4f))
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = currentBanner.imageUrl,
                    contentDescription = currentBanner.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Dark gradient overlay for cyber look
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.2f),
                                    Color.Black.copy(alpha = 0.65f),
                                    Color.Black.copy(alpha = 0.95f)
                                )
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (currentBanner.badge.isNotBlank()) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CyberCyan.copy(alpha = 0.25f))
                                    .border(1.dp, CyberCyan, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = currentBanner.badge,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }
                        }

                        if (currentBanner.category.isNotBlank()) {
                            Text(
                                text = currentBanner.category,
                                style = MaterialTheme.typography.labelSmall,
                                color = CyberCyan
                            )
                        }
                    }

                    Column {
                        Text(
                            text = currentBanner.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (currentBanner.team.isNotBlank()) {
                                Text(
                                    text = "👥 " + currentBanner.team,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CyberCyan
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            if (currentBanner.date.isNotBlank()) {
                                Text(
                                    text = "• 📅 " + currentBanner.date,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }

                // Slide Indicators (dots)
                if (banners.size > 1) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        banners.indices.forEach { index ->
                            Box(
                                modifier = Modifier
                                    .height(6.dp)
                                    .width(if (index == currentIndex) 18.dp else 6.dp)
                                    .clip(CircleShape)
                                    .background(if (index == currentIndex) CyberCyan else Color.White.copy(alpha = 0.4f))
                                    .clickable { currentIndex = index }
                            )
                        }
                    }
                }
            }
        }
    }
}
