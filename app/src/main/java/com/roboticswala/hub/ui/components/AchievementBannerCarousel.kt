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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.shadow
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
import com.roboticswala.hub.ui.theme.CyberCyanGlow
import com.roboticswala.hub.ui.theme.DarkSurfaceElevated
import com.roboticswala.hub.ui.theme.ElectricBlue
import com.roboticswala.hub.ui.theme.LightSurfaceElevated
import com.roboticswala.hub.ui.theme.LocalThemeIsDark
import com.roboticswala.hub.ui.theme.TextPrimaryDark
import com.roboticswala.hub.ui.theme.TextPrimaryLight
import com.roboticswala.hub.ui.theme.TextSecondaryDark
import com.roboticswala.hub.ui.theme.TextSecondaryLight
import kotlinx.coroutines.delay

@Composable
fun AchievementBannerCarousel(
    banners: List<AchievementBannerItem>,
    isDark: Boolean = LocalThemeIsDark.current,
    isAdmin: Boolean = false,
    onNavigateToAchievements: () -> Unit,
    onEditBanner: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val activeBanners = if (banners.isNotEmpty()) banners else AchievementBannerItem.DEFAULT_BANNERS

    var currentIndex by remember { mutableStateOf(0) }
    var previewBanner by remember { mutableStateOf<AchievementBannerItem?>(null) }

    // Auto-advance sliding timer every 3.5 seconds
    LaunchedEffect(activeBanners.size) {
        if (activeBanners.size <= 1) return@LaunchedEffect
        while (true) {
            delay(3500)
            currentIndex = (currentIndex + 1) % activeBanners.size
        }
    }

    val currentBanner = activeBanners[currentIndex % activeBanners.size]

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(CyberCyan.copy(alpha = 0.25f), ElectricBlue.copy(alpha = 0.2f))
                            )
                        )
                        .border(1.dp, CyberCyan.copy(alpha = 0.6f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.EmojiEvents,
                        contentDescription = "Achievements",
                        tint = if (isDark) CyberCyan else ElectricBlue,
                        modifier = Modifier.size(17.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Lab Achievements",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.2).sp
                    ),
                    color = if (isDark) TextPrimaryDark else TextPrimaryLight
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(CircuitSuccess.copy(alpha = 0.15f))
                        .border(1.dp, CircuitSuccess.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "LIVE SHOWCASE",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.ExtraBold),
                        color = CircuitSuccess
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                // Strictly Admin-only edit & upload action
                if (isAdmin && onEditBanner != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isDark) CyberCyan.copy(alpha = 0.18f) else ElectricBlue.copy(alpha = 0.12f))
                            .border(1.dp, if (isDark) CyberCyan.copy(alpha = 0.5f) else ElectricBlue.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .clickable { onEditBanner() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Edit / Upload",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                            color = if (isDark) CyberCyan else ElectricBlue
                        )
                    }
                }
                Text(
                    text = if (isAdmin) "Manage ➔" else "View Details ➔",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (isDark) CyberCyan else ElectricBlue,
                    modifier = Modifier.clickable {
                        if (isAdmin) onNavigateToAchievements() else previewBanner = currentBanner
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Jar App Inspired High-Tech Sliding Card with Soft Ambient Shadow
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(175.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(20.dp),
                    ambientColor = if (isDark) CyberCyan.copy(alpha = 0.25f) else ElectricBlue.copy(alpha = 0.15f),
                    spotColor = Color.Black.copy(alpha = 0.35f)
                )
                .clickable {
                    if (isAdmin) onNavigateToAchievements() else previewBanner = currentBanner
                },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = if (isDark) DarkSurfaceElevated else LightSurfaceElevated),
            border = androidx.compose.foundation.BorderStroke(
                1.5.dp,
                if (isDark) CyberCyan.copy(alpha = 0.5f) else ElectricBlue.copy(alpha = 0.4f)
            )
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                if (currentBanner.imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = currentBanner.imageUrl,
                        contentDescription = currentBanner.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // Fallback Futuristic Background
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        if (isDark) Color(0xFF0F172A) else Color(0xFF1E293B),
                                        if (isDark) Color(0xFF032644) else Color(0xFF0F3B66)
                                    )
                                )
                            )
                    )
                }

                // Dark gradient overlay for cyber contrast
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.25f),
                                    Color.Black.copy(alpha = 0.65f),
                                    Color.Black.copy(alpha = 0.95f)
                                )
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
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
                                    .border(1.dp, CyberCyanGlow, RoundedCornerShape(8.dp))
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
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = CyberCyan
                            )
                        }
                    }

                    Column {
                        Text(
                            text = currentBanner.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.2).sp
                            ),
                            color = Color.White,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (currentBanner.team.isNotBlank()) {
                                Icon(
                                    imageVector = Icons.Filled.Group,
                                    contentDescription = null,
                                    tint = CyberCyan,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = currentBanner.team,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CyberCyan
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                            }
                            if (currentBanner.date.isNotBlank()) {
                                Icon(
                                    imageVector = Icons.Filled.CalendarToday,
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.7f),
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = currentBanner.date,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.8f)
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
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        banners.indices.forEach { index ->
                            Box(
                                modifier = Modifier
                                    .height(5.dp)
                                    .width(if (index == currentIndex) 18.dp else 5.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(
                                        if (index == currentIndex) CyberCyan
                                        else Color.White.copy(alpha = 0.35f)
                                    )
                            )
                        }
                    }
                }
            }
        }

        // Read-only Showcase Dialog for students / viewers
        if (previewBanner != null) {
            val item = previewBanner!!
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { previewBanner = null },
                confirmButton = {
                    androidx.compose.material3.TextButton(onClick = { previewBanner = null }) {
                        Text("Close", fontWeight = FontWeight.Bold, color = if (isDark) CyberCyan else ElectricBlue)
                    }
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(imageVector = Icons.Filled.EmojiEvents, contentDescription = null, tint = if (isDark) CyberCyan else ElectricBlue)
                        Text(text = item.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    }
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (item.imageUrl.isNotBlank()) {
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                            ) {
                                AsyncImage(
                                    model = item.imageUrl,
                                    contentDescription = item.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }

                        if (item.badge.isNotBlank()) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(CircuitSuccess.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "★ ${item.badge}",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = CircuitSuccess
                                )
                            }
                        }

                        if (item.team.isNotBlank()) {
                            Text(
                                text = "Team / Project: ${item.team}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                        }

                        if (item.category.isNotBlank()) {
                            Text(
                                text = "Category: ${item.category}",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isDark) TextSecondaryDark else TextSecondaryLight
                            )
                        }

                        if (item.description.isNotBlank()) {
                            Text(
                                text = item.description,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            )
        }
    }
}

