package com.roboticswala.hub.ui.screens.admin.tabs

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.activity.compose.BackHandler
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import com.roboticswala.hub.data.models.StudentDirectoryItem
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.roboticswala.hub.ui.components.RoboticsOutlinedButton
import com.roboticswala.hub.ui.theme.CircuitWarning
import com.roboticswala.hub.ui.theme.CyberCyan
import com.roboticswala.hub.ui.theme.CyberCyanGlow
import com.roboticswala.hub.ui.theme.DarkSurface
import com.roboticswala.hub.ui.theme.DarkSurfaceBorder
import com.roboticswala.hub.ui.theme.ElectricBlue
import com.roboticswala.hub.ui.theme.LightSurface
import com.roboticswala.hub.ui.theme.LightSurfaceBorder
import com.roboticswala.hub.ui.theme.TextPrimaryDark
import com.roboticswala.hub.ui.theme.TextPrimaryLight
import com.roboticswala.hub.ui.theme.TextSecondaryDark
import com.roboticswala.hub.ui.theme.TextSecondaryLight

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.Person
import coil.compose.AsyncImage
import com.roboticswala.hub.data.models.UserProfile

import androidx.compose.runtime.LaunchedEffect

@Composable
fun AdminMoreScreen(
    onLogout: () -> Unit,
    adminProfile: UserProfile? = null,
    adminsList: List<UserProfile> = emptyList(),
    studentsList: List<StudentDirectoryItem> = emptyList(),
    onPromoteToAdmin: (userId: String, position: String) -> Unit = { _, _ -> },
    onUpdateAdminPosition: (userId: String, position: String) -> Unit = { _, _ -> },
    onDemoteAdmin: (userId: String) -> Unit = {},
    onPurgeLegacyRajguru: () -> Unit = {},
    onUpdateProfile: (fullName: String, phone: String, college: String, branch: String, year: String, adminId: String, emergencyContact: String) -> Unit = { _, _, _, _, _, _, _ -> },
    onUpdatePhoto: (photoBase64: String) -> Unit = {},
    onNavigateToChat: () -> Unit = {},
    unreadChatCount: Int = 0,
    initialSubScreen: String? = null,
    onSubScreenCleared: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isDark = com.roboticswala.hub.ui.theme.LocalThemeIsDark.current
    val scrollState = rememberScrollState()
    var showingProfile by remember { mutableStateOf(false) }
    var showingProjects by remember { mutableStateOf(false) }
    var showingTasks by remember { mutableStateOf(false) }
    var showingAchievements by remember { mutableStateOf(false) }
    var showingNotices by remember { mutableStateOf(false) }
    var showingEvents by remember { mutableStateOf(false) }
    var showingEquipment by remember { mutableStateOf(false) }
    var showingEquipmentRequests by remember { mutableStateOf(false) }
    var showingBudget by remember { mutableStateOf(false) }
    var showingAnalytics by remember { mutableStateOf(false) }
    var showingGateLog by remember { mutableStateOf(false) }
    var showingLabConfig by remember { mutableStateOf(false) }
    var showingAdminManagement by remember { mutableStateOf(false) }

    val dismissSubScreen: () -> Unit = {
        showingProfile = false
        showingProjects = false
        showingTasks = false
        showingAchievements = false
        showingNotices = false
        showingEvents = false
        showingEquipment = false
        showingEquipmentRequests = false
        showingBudget = false
        showingAnalytics = false
        showingGateLog = false
        showingLabConfig = false
        showingAdminManagement = false
        onSubScreenCleared()
    }

    val isAnySubScreenActive = showingProfile || showingProjects || showingTasks ||
            showingAchievements || showingNotices || showingEvents || showingEquipment ||
            showingEquipmentRequests || showingBudget || showingAnalytics || showingAdminManagement

    BackHandler(enabled = isAnySubScreenActive) {
        if (showingEquipmentRequests && showingEquipment) {
            showingEquipmentRequests = false
        } else {
            dismissSubScreen()
        }
    }

    LaunchedEffect(initialSubScreen) {
        when (initialSubScreen) {
            "projects" -> showingProjects = true
            "equipment" -> showingEquipment = true
            "tasks" -> showingTasks = true
            "achievements" -> showingAchievements = true
            "notices" -> showingNotices = true
            "events" -> showingEvents = true
            "budget" -> showingBudget = true
            "analytics" -> showingAnalytics = true
            "equipment_requests" -> showingEquipmentRequests = true
            "profile" -> showingProfile = true
        }
    }

    if (showingProfile) {
        Column(modifier = modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = dismissSubScreen) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Settings",
                        tint = if (isDark) CyberCyan else ElectricBlue
                    )
                }
                Text(
                    text = "Back to Admin Options",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = if (isDark) CyberCyan else ElectricBlue
                )
            }
            AdminProfileScreen(
                adminProfile = adminProfile,
                onUpdateProfile = onUpdateProfile,
                onUpdatePhoto = onUpdatePhoto,
                onLogout = onLogout
            )
        }
        return
    }

    if (showingAnalytics) {
        Column(modifier = modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = dismissSubScreen) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Settings",
                        tint = if (isDark) CyberCyan else ElectricBlue
                    )
                }
                Text(
                    text = "Back to Admin Options",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = if (isDark) CyberCyan else ElectricBlue
                )
            }
            AdminAnalyticsScreen()
        }
        return
    }

    if (showingBudget) {
        Column(modifier = modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = dismissSubScreen) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Settings",
                        tint = if (isDark) CyberCyan else ElectricBlue
                    )
                }
                Text(
                    text = "Back to Admin Options",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = if (isDark) CyberCyan else ElectricBlue
                )
            }
            AdminBudgetScreen()
        }
        return
    }

    if (showingEquipmentRequests) {
        AdminEquipmentRequestsScreen(
            onNavigateBack = {
                showingEquipmentRequests = false
                if (!showingEquipment) {
                    onSubScreenCleared()
                }
            },
            modifier = modifier
        )
        return
    }

    if (showingEquipment) {
        Column(modifier = modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = dismissSubScreen) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Settings",
                        tint = if (isDark) CyberCyan else ElectricBlue
                    )
                }
                Text(
                    text = "Back to Admin Options",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = if (isDark) CyberCyan else ElectricBlue
                )
            }
            AdminEquipmentScreen(
                onNavigateToRequests = { showingEquipmentRequests = true }
            )
        }
        return
    }

    if (showingNotices) {
        Column(modifier = modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = dismissSubScreen) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Settings",
                        tint = if (isDark) CyberCyan else ElectricBlue
                    )
                }
                Text(
                    text = "Back to Admin Options",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = if (isDark) CyberCyan else ElectricBlue
                )
            }
            AdminNoticesScreen()
        }
        return
    }

    if (showingEvents) {
        Column(modifier = modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = dismissSubScreen) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Settings",
                        tint = if (isDark) CyberCyan else ElectricBlue
                    )
                }
                Text(
                    text = "Back to Admin Options",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = if (isDark) CyberCyan else ElectricBlue
                )
            }
            AdminEventsScreen()
        }
        return
    }

    if (showingProjects) {
        Column(modifier = modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = dismissSubScreen) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Settings",
                        tint = if (isDark) CyberCyan else ElectricBlue
                    )
                }
                Text(
                    text = "Back to Admin Options",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = if (isDark) CyberCyan else ElectricBlue
                )
            }
            AdminProjectsScreen()
        }
        return
    }

    if (showingTasks) {
        Column(modifier = modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = dismissSubScreen) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Settings",
                        tint = if (isDark) CyberCyan else ElectricBlue
                    )
                }
                Text(
                    text = "Back to Admin Options",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = if (isDark) CyberCyan else ElectricBlue
                )
            }
            AdminTasksScreen()
        }
        return
    }

    if (showingAchievements) {
        Column(modifier = modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = dismissSubScreen) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Settings",
                        tint = if (isDark) CyberCyan else ElectricBlue
                    )
                }
                Text(
                    text = "Back to Admin Options",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = if (isDark) CyberCyan else ElectricBlue
                )
            }
            AdminAchievementsScreen()
        }
        return
    }

    val adminPhotoUrl = adminProfile?.photoUrl.orEmpty()
    val adminPhotoBitmap = remember(adminPhotoUrl) {
        if (adminPhotoUrl.isNotBlank() && !adminPhotoUrl.startsWith("http")) {
            try {
                val clean = if (adminPhotoUrl.contains(",")) adminPhotoUrl.substringAfter(",") else adminPhotoUrl
                val bytes = Base64.decode(clean, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            } catch (e: Exception) {
                null
            }
        } else null
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        // Section Header
        Text(
            text = "Admin Control Center",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.3).sp
            ),
            color = if (isDark) TextPrimaryDark else TextPrimaryLight
        )
        Text(
            text = "Facility management, approvals, inventory & system controls",
            style = MaterialTheme.typography.bodySmall,
            color = if (isDark) TextSecondaryDark else TextSecondaryLight
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 👑 HERO: ADMIN PROFILE & ID DOSSIER CARD (Compact & Sleek)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showingProfile = true }
                .border(
                    width = 1.dp,
                    color = if (isDark) CyberCyan.copy(alpha = 0.45f) else ElectricBlue.copy(alpha = 0.35f),
                    shape = RoundedCornerShape(18.dp)
                ),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isDark) DarkSurface else LightSurface
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(
                            if (isDark) CyberCyan.copy(alpha = 0.25f) else ElectricBlue.copy(alpha = 0.18f)
                        )
                        .border(
                            width = 2.dp,
                            brush = Brush.linearGradient(
                                colors = if (isDark) listOf(CyberCyan, CyberCyanGlow, ElectricBlue)
                                else listOf(ElectricBlue, CyberCyan, ElectricBlue)
                            ),
                            shape = RoundedCornerShape(15.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (adminPhotoBitmap != null) {
                        Image(
                            bitmap = adminPhotoBitmap.asImageBitmap(),
                            contentDescription = "Admin Profile Photo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else if (adminPhotoUrl.startsWith("http")) {
                        AsyncImage(
                            model = adminPhotoUrl,
                            contentDescription = "Admin Profile Photo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Admin Profile",
                            tint = if (isDark) CyberCyan else ElectricBlue,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = adminProfile?.fullName ?: "Admin In-Charge",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (isDark) TextPrimaryDark else TextPrimaryLight
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    if (isDark) CyberCyan.copy(alpha = 0.2f) else ElectricBlue.copy(alpha = 0.15f)
                                )
                                .padding(horizontal = 5.dp, vertical = 1.5.dp)
                        ) {
                            Text(
                                text = "ADMIN",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold
                                ),
                                color = if (isDark) CyberCyan else ElectricBlue
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "ID: ${adminProfile?.displayAdminId ?: "RWH-ADM-2026-001"} • Dossier & Permissions",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = if (isDark) TextSecondaryDark else TextSecondaryLight
                    )
                }

                Icon(
                    imageVector = Icons.Filled.Tune,
                    contentDescription = "Open Profile",
                    tint = if (isDark) CyberCyan else ElectricBlue,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Quick Grid Title
        Text(
            text = "MANAGEMENT MODULES",
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            color = if (isDark) CyberCyan else ElectricBlue
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 2-COLUMN COMPACT GRID
        // Row 1: Achievements & Tasks
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AdminGridTile(
                modifier = Modifier.weight(1f),
                title = "Achievements",
                subtitle = "Approve certs & awards",
                icon = Icons.Filled.EmojiEvents,
                badge = "Certificates",
                isDark = isDark,
                onClick = { showingAchievements = true }
            )
            AdminGridTile(
                modifier = Modifier.weight(1f),
                title = "Weekly Tasks",
                subtitle = "Milestones & reviews",
                icon = Icons.Filled.Assignment,
                badge = "Assignments",
                isDark = isDark,
                onClick = { showingTasks = true }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Row 2: Projects & Inventory
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AdminGridTile(
                modifier = Modifier.weight(1f),
                title = "Projects Hub",
                subtitle = "Registry & mentors",
                icon = Icons.Filled.PrecisionManufacturing,
                badge = "Robotics Lab",
                isDark = isDark,
                onClick = { showingProjects = true }
            )
            AdminGridTile(
                modifier = Modifier.weight(1f),
                title = "Inventory",
                subtitle = "Sensors & stock alerts",
                icon = Icons.Filled.Inventory2,
                badge = "Equipment",
                isDark = isDark,
                onClick = { showingEquipment = true }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Row 3: Notices & Events
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AdminGridTile(
                modifier = Modifier.weight(1f),
                title = "Notice Board",
                subtitle = "Broadcast lab alerts",
                icon = Icons.Filled.Campaign,
                badge = "Circulars",
                isDark = isDark,
                onClick = { showingNotices = true }
            )
            AdminGridTile(
                modifier = Modifier.weight(1f),
                title = "Events & Meets",
                subtitle = "Hackathons & workshops",
                icon = Icons.Filled.Event,
                badge = "Schedules",
                isDark = isDark,
                onClick = { showingEvents = true }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Row 4: Budget & Analytics
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AdminGridTile(
                modifier = Modifier.weight(1f),
                title = "Budget & Grants",
                subtitle = "Expense audits & claims",
                icon = Icons.Filled.Inventory2,
                badge = "Finances",
                isDark = isDark,
                onClick = { showingBudget = true }
            )
            AdminGridTile(
                modifier = Modifier.weight(1f),
                title = "Hub Analytics",
                subtitle = "Trends & export reports",
                icon = Icons.Filled.PrecisionManufacturing,
                badge = "Reports",
                isDark = isDark,
                onClick = { showingAnalytics = true }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Row 5: Security & Preferences
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AdminGridTile(
                modifier = Modifier.weight(1f),
                title = "Gate Access",
                subtitle = "Biometric & RFID log",
                icon = Icons.Filled.Security,
                badge = "Coming Soon",
                isDark = isDark,
                onClick = { showingGateLog = true }
            )
            AdminGridTile(
                modifier = Modifier.weight(1f),
                title = "Lab Config",
                subtitle = "Operating hours & bays",
                icon = Icons.Filled.Tune,
                badge = "Coming Soon",
                isDark = isDark,
                onClick = { showingLabConfig = true }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Row 6: Community & Admin Staff Management
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AdminGridTile(
                modifier = Modifier.weight(1f),
                title = "Community Chat",
                subtitle = "Real-time lab discussion",
                icon = Icons.AutoMirrored.Filled.Chat,
                badge = if (unreadChatCount > 0) "$unreadChatCount NEW" else "Live Room",
                isDark = isDark,
                onClick = onNavigateToChat
            )
            AdminGridTile(
                modifier = Modifier.weight(1f),
                title = "Manage Admins",
                subtitle = "Roles & staff positions",
                icon = Icons.Filled.SupervisorAccount,
                badge = "${adminsList.size} Staff",
                isDark = isDark,
                onClick = { showingAdminManagement = true }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        RoboticsOutlinedButton(
            text = "Log Out of Admin Hub",
            onClick = onLogout,
            leadingIcon = Icons.Filled.Logout
        )

        Spacer(modifier = Modifier.height(36.dp))
    }

    // Admin & Staff Role Management Dialog
    if (showingAdminManagement) {
        AdminStaffManagementDialog(
            adminsList = adminsList,
            studentsList = studentsList,
            onPromoteToAdmin = onPromoteToAdmin,
            onUpdateAdminPosition = onUpdateAdminPosition,
            onDemoteAdmin = onDemoteAdmin,
            onPurgeLegacyRajguru = onPurgeLegacyRajguru,
            onDismiss = { showingAdminManagement = false },
            isDark = isDark
        )
    }

    // Gate Access & RFID Biometric Dialog
    if (showingGateLog) {
        AlertDialog(
            onDismissRequest = { showingGateLog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        imageVector = Icons.Filled.Security,
                        contentDescription = null,
                        tint = if (isDark) CyberCyan else ElectricBlue,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = "Gate Access & Turnstile",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (isDark) TextPrimaryDark else TextPrimaryLight
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(CircuitWarning.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "NEXT RELEASE",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                            color = CircuitWarning
                        )
                    }
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Hardware Integration in Progress",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = if (isDark) CyberCyan else ElectricBlue
                    )
                    Text(
                        text = "Automatic RFID turnstile barrier sync, biometric student gate attendance logs, and hardware NFC door controllers will be enabled in the upcoming release.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isDark) TextSecondaryDark else TextSecondaryLight
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showingGateLog = false }) {
                    Text(
                        text = "Close",
                        color = if (isDark) CyberCyan else ElectricBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            containerColor = if (isDark) DarkSurface else LightSurface
        )
    }

    // Lab Preferences & Hours Config Dialog
    if (showingLabConfig) {
        AlertDialog(
            onDismissRequest = { showingLabConfig = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        imageVector = Icons.Filled.Tune,
                        contentDescription = null,
                        tint = if (isDark) CyberCyan else ElectricBlue,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = "Lab Operating Configuration",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (isDark) TextPrimaryDark else TextPrimaryLight
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(CircuitWarning.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "NEXT RELEASE",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                            color = CircuitWarning
                        )
                    }
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "IoT & Hardware Automation",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = if (isDark) CyberCyan else ElectricBlue
                    )
                    Text(
                        text = "Lab bay operating hours, machine quotas, 3D printer auto-shutdown rules, and smart switch IoT preferences will be configurable in the upcoming release.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isDark) TextSecondaryDark else TextSecondaryLight
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showingLabConfig = false }) {
                    Text(
                        text = "Close",
                        color = if (isDark) CyberCyan else ElectricBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            containerColor = if (isDark) DarkSurface else LightSurface
        )
    }
}

@Composable
fun AdminStaffManagementDialog(
    adminsList: List<UserProfile>,
    studentsList: List<StudentDirectoryItem>,
    onPromoteToAdmin: (userId: String, position: String) -> Unit,
    onUpdateAdminPosition: (userId: String, position: String) -> Unit,
    onDemoteAdmin: (userId: String) -> Unit,
    onPurgeLegacyRajguru: () -> Unit,
    onDismiss: () -> Unit,
    isDark: Boolean
) {
    var selectedStudentId by remember { mutableStateOf("") }
    var selectedStudentName by remember { mutableStateOf("") }
    var newPositionText by remember { mutableStateOf("") }
    var editingAdminUid by remember { mutableStateOf<String?>(null) }
    var editPositionText by remember { mutableStateOf("") }

    val presetPositions = listOf("Lab Director", "Hardware Mentor", "Faculty In-Charge", "Inventory In-Charge", "Teaching Assistant")

    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.85f))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 680.dp)
                    .border(
                        1.dp,
                        if (isDark) CyberCyan.copy(alpha = 0.35f) else ElectricBlue.copy(alpha = 0.3f),
                        RoundedCornerShape(20.dp)
                    ),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = if (isDark) DarkSurface else LightSurface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (isDark) CyberCyan.copy(alpha = 0.15f) else ElectricBlue.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.SupervisorAccount,
                                    contentDescription = null,
                                    tint = if (isDark) CyberCyan else ElectricBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Admin & Staff Roles",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (isDark) TextPrimaryDark else TextPrimaryLight
                                )
                                Text(
                                    text = "Assign positions & permissions",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = if (isDark) TextSecondaryDark else TextSecondaryLight
                                )
                            }
                        }

                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Filled.Logout,
                                contentDescription = "Close",
                                tint = if (isDark) TextSecondaryDark else TextSecondaryLight
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Legacy Clean Purge Banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isDark) DarkSurfaceElevated else LightSurfaceBorder.copy(alpha = 0.35f))
                            .border(1.dp, CircuitWarning.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Account Ownership & Cleanup",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = CircuitWarning
                                )
                                Text(
                                    text = "You are the Main Super Admin. Tap to purge any legacy test accounts.",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                    color = if (isDark) TextSecondaryDark else TextSecondaryLight
                                )
                            }
                            TextButton(onClick = onPurgeLegacyRajguru) {
                                Text(
                                    text = "Clean Legacy",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = CircuitWarning
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Section: Active Admins List
                    Text(
                        text = "ACTIVE ADMINS (${adminsList.size})",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp,
                            fontSize = 10.sp
                        ),
                        color = if (isDark) CyberCyan else ElectricBlue
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        adminsList.forEach { admin ->
                            val isCurrentMain = admin.isMainAdmin
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(
                                        1.dp,
                                        if (isCurrentMain) CircuitWarning.copy(alpha = 0.6f)
                                        else if (isDark) DarkSurfaceBorder else LightSurfaceBorder,
                                        RoundedCornerShape(12.dp)
                                    ),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isCurrentMain) {
                                        CircuitWarning.copy(alpha = 0.08f)
                                    } else if (isDark) DarkSurfaceElevated else LightSurface
                                )
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = admin.fullName.ifBlank { "Administrator" },
                                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                                    color = if (isDark) TextPrimaryDark else TextPrimaryLight
                                                )
                                                if (isCurrentMain) {
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(4.dp))
                                                            .background(CircuitWarning.copy(alpha = 0.25f))
                                                            .padding(horizontal = 5.dp, vertical = 1.dp)
                                                    ) {
                                                        Text(
                                                            text = "MAIN SUPER ADMIN",
                                                            style = MaterialTheme.typography.labelSmall.copy(
                                                                fontSize = 8.sp,
                                                                fontWeight = FontWeight.ExtraBold
                                                            ),
                                                            color = CircuitWarning
                                                        )
                                                    }
                                                }
                                            }
                                            Text(
                                                text = admin.email.ifBlank { admin.displayId },
                                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp),
                                                color = if (isDark) TextSecondaryDark else TextSecondaryLight
                                            )
                                        }

                                        if (!isCurrentMain) {
                                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                IconButton(
                                                    onClick = {
                                                        editingAdminUid = admin.uid
                                                        editPositionText = admin.adminPosition
                                                    },
                                                    modifier = Modifier.size(28.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Filled.Edit,
                                                        contentDescription = "Edit Position",
                                                        tint = if (isDark) CyberCyan else ElectricBlue,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                                IconButton(
                                                    onClick = { onDemoteAdmin(admin.uid) },
                                                    modifier = Modifier.size(28.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Filled.Delete,
                                                        contentDescription = "Remove Admin",
                                                        tint = com.roboticswala.hub.ui.theme.CircuitError,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    // Position Badge
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (isDark) CyberCyan.copy(alpha = 0.12f) else ElectricBlue.copy(alpha = 0.1f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "Position: " + (if (admin.adminPosition.isNotBlank()) admin.adminPosition else if (isCurrentMain) "Main Super Admin / Lab Director" else "Staff Admin"),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 9.5.sp,
                                                fontWeight = FontWeight.SemiBold
                                            ),
                                            color = if (isDark) CyberCyan else ElectricBlue
                                        )
                                    }

                                    // Inline position edit field if selected
                                    if (editingAdminUid == admin.uid) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            OutlinedTextField(
                                                value = editPositionText,
                                                onValueChange = { editPositionText = it },
                                                placeholder = { Text("e.g. Hardware Mentor", fontSize = 11.sp) },
                                                modifier = Modifier.weight(1f),
                                                textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                                singleLine = true
                                            )
                                            TextButton(
                                                onClick = {
                                                    onUpdateAdminPosition(admin.uid, editPositionText.trim())
                                                    editingAdminUid = null
                                                }
                                            ) {
                                                Text("Save", fontWeight = FontWeight.Bold, color = if (isDark) CyberCyan else ElectricBlue)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Section: Promote Student to Admin
                    Text(
                        text = "ADD / PROMOTE NEW ADMIN",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp,
                            fontSize = 10.sp
                        ),
                        color = if (isDark) CyberCyan else ElectricBlue
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, if (isDark) DarkSurfaceBorder else LightSurfaceBorder, RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = if (isDark) DarkSurfaceElevated else LightSurface)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "1. Select Student Candidate:",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (isDark) TextPrimaryDark else TextPrimaryLight
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            // Candidate items
                            if (studentsList.isEmpty()) {
                                Text(
                                    text = "No students currently registered in directory.",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = if (isDark) TextSecondaryDark else TextSecondaryLight
                                )
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    studentsList.take(6).forEach { stu ->
                                        val isChosen = selectedStudentId == stu.id
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(
                                                    if (isChosen) (if (isDark) CyberCyan.copy(alpha = 0.2f) else ElectricBlue.copy(alpha = 0.15f))
                                                    else Color.Transparent
                                                )
                                                .border(
                                                    1.dp,
                                                    if (isChosen) (if (isDark) CyberCyan else ElectricBlue) else Color.Transparent,
                                                    RoundedCornerShape(8.dp)
                                                )
                                                .clickable {
                                                    selectedStudentId = stu.id
                                                    selectedStudentName = stu.name
                                                }
                                                .padding(horizontal = 8.dp, vertical = 6.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "${stu.name} (${stu.studentId})",
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        fontWeight = if (isChosen) FontWeight.Bold else FontWeight.Normal,
                                                        fontSize = 11.sp
                                                    ),
                                                    color = if (isChosen) (if (isDark) CyberCyan else ElectricBlue) else (if (isDark) TextPrimaryDark else TextPrimaryLight)
                                                )
                                                if (isChosen) {
                                                    Icon(
                                                        imageVector = Icons.Filled.Star,
                                                        contentDescription = null,
                                                        tint = if (isDark) CyberCyan else ElectricBlue,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "2. Set Admin Position / Lab Role:",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (isDark) TextPrimaryDark else TextPrimaryLight
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            // Preset Position Chips
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                presetPositions.take(3).forEach { pos ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(
                                                if (newPositionText == pos) (if (isDark) CyberCyan.copy(alpha = 0.25f) else ElectricBlue.copy(alpha = 0.2f))
                                                else (if (isDark) DarkSurface else LightSurfaceBorder)
                                            )
                                            .clickable { newPositionText = pos }
                                            .padding(horizontal = 6.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = pos,
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                            color = if (newPositionText == pos) (if (isDark) CyberCyan else ElectricBlue) else (if (isDark) TextSecondaryDark else TextSecondaryLight)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = newPositionText,
                                onValueChange = { newPositionText = it },
                                placeholder = { Text("e.g. Hardware Mentor or Lab In-Charge", fontSize = 11.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(42.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (selectedStudentId.isNotBlank()) Brush.horizontalGradient(listOf(ElectricBlue, CyberCyan))
                                        else Brush.horizontalGradient(listOf(Color.Gray.copy(alpha = 0.5f), Color.Gray.copy(alpha = 0.5f)))
                                    )
                                    .clickable(enabled = selectedStudentId.isNotBlank()) {
                                        onPromoteToAdmin(selectedStudentId, newPositionText.ifBlank { "Staff Admin" })
                                        selectedStudentId = ""
                                        selectedStudentName = ""
                                        newPositionText = ""
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (selectedStudentId.isNotBlank()) "Promote to Admin ($selectedStudentName)" else "Select Candidate to Promote",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Close Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isDark) DarkSurfaceElevated else LightSurfaceBorder)
                            .clickable(onClick = onDismiss),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Done & Return",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = if (isDark) TextPrimaryDark else TextPrimaryLight
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminGridTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    badge: String,
    isDark: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .border(
                width = 1.dp,
                color = if (isDark) DarkSurfaceBorder else LightSurfaceBorder,
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) DarkSurface else LightSurface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isDark) CyberCyan.copy(alpha = 0.22f) else ElectricBlue.copy(alpha = 0.16f)
                        )
                        .border(
                            width = 1.5.dp,
                            color = if (isDark) CyberCyan.copy(alpha = 0.85f) else ElectricBlue.copy(alpha = 0.7f),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = if (isDark) CyberCyan else ElectricBlue,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (isDark) DarkSurfaceBorder.copy(alpha = 0.6f) else LightSurfaceBorder.copy(alpha = 0.8f)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = if (isDark) TextSecondaryDark else TextSecondaryLight
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp
                ),
                color = if (isDark) TextPrimaryDark else TextPrimaryLight,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 10.5.sp,
                    lineHeight = 13.sp
                ),
                color = if (isDark) TextSecondaryDark else TextSecondaryLight,
                maxLines = 1
            )
        }
    }
}
