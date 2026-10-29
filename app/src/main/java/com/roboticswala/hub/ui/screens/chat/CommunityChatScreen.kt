package com.roboticswala.hub.ui.screens.chat

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.AddLink
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.roboticswala.hub.data.models.ChatMessage
import com.roboticswala.hub.ui.components.RoboticsBackground
import com.roboticswala.hub.ui.theme.CircuitError
import com.roboticswala.hub.ui.theme.CircuitSuccess
import com.roboticswala.hub.ui.theme.CircuitWarning
import com.roboticswala.hub.ui.theme.CyberCyan
import com.roboticswala.hub.ui.theme.CyberCyanGlow
import com.roboticswala.hub.ui.theme.DarkBackground
import com.roboticswala.hub.ui.theme.DarkSurface
import com.roboticswala.hub.ui.theme.DarkSurfaceBorder
import com.roboticswala.hub.ui.theme.DarkSurfaceElevated
import com.roboticswala.hub.ui.theme.ElectricBlue
import com.roboticswala.hub.ui.theme.LightBackground
import com.roboticswala.hub.ui.theme.LightSurface
import com.roboticswala.hub.ui.theme.LightSurfaceBorder
import com.roboticswala.hub.ui.theme.TextPrimaryDark
import com.roboticswala.hub.ui.theme.TextPrimaryLight
import com.roboticswala.hub.ui.theme.TextSecondaryDark
import com.roboticswala.hub.ui.theme.TextSecondaryLight
import com.roboticswala.hub.ui.theme.LocalThemeIsDark
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityChatScreen(
    onNavigateBack: () -> Unit,
    viewModel: ChatViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isDark = LocalThemeIsDark.current
    val listState = rememberLazyListState()
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    var previewImageAttachment by remember { mutableStateOf<Pair<String, String>?>(null) }

    // Mark as read on entry and on new message
    LaunchedEffect(Unit) {
        viewModel.markAsRead(context)
    }

    // Auto-scroll to bottom on new message or typing
    val totalChatItems = uiState.messages.size + if (uiState.typingUsers.isNotEmpty()) 1 else 0
    LaunchedEffect(totalChatItems) {
        if (totalChatItems > 0) {
            viewModel.markAsRead(context)
            listState.animateScrollToItem(totalChatItems - 1)
        }
    }

    if (previewImageAttachment != null) {
        val (imgUrl, imgName) = previewImageAttachment!!
        ImageViewerDialog(
            imageUrl = imgUrl,
            imageName = imgName,
            onDismiss = { previewImageAttachment = null },
            onSaveToDevice = {
                scope.launch {
                    val res = com.roboticswala.hub.utils.ImageStorageHelper.saveToDownloads(
                        context = context,
                        fileName = imgName.ifBlank { "chat_image_${System.currentTimeMillis()}.jpg" },
                        fileUrlOrBase64 = imgUrl,
                        mimeType = "image/jpeg"
                    )
                    if (res.isSuccess) {
                        Toast.makeText(context, "Image saved to Downloads folder!", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "Could not save image: ${res.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            onOpenExternal = {
                com.roboticswala.hub.utils.ImageStorageHelper.openFile(
                    context = context,
                    fileName = imgName.ifBlank { "chat_image.jpg" },
                    fileUrlOrBase64 = imgUrl
                )
            },
            isDark = isDark
        )
    }

    RoboticsBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = if (isDark) CyberCyan else ElectricBlue
                            )
                        }
                    },
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(CyberCyan.copy(alpha = 0.3f), ElectricBlue.copy(alpha = 0.3f))
                                        )
                                    )
                                    .border(1.5.dp, CyberCyan.copy(alpha = 0.6f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Groups,
                                    contentDescription = "Group",
                                    tint = if (isDark) CyberCyan else ElectricBlue,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "RW HUB Discussion",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = (-0.2).sp
                                    ),
                                    color = if (isDark) TextPrimaryDark else TextPrimaryLight
                                )
                                val typingText = when {
                                    uiState.typingUsers.isNotEmpty() -> {
                                        if (uiState.typingUsers.size == 1) "${uiState.typingUsers.first()} is typing..."
                                        else "${uiState.typingUsers.take(2).joinToString(" & ")} are typing..."
                                    }
                                    uiState.inputText.isNotBlank() -> "Typing..."
                                    else -> null
                                }
                                if (typingText != null) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(CyberCyanGlow)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = typingText,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold
                                            ),
                                            color = CyberCyanGlow
                                        )
                                    }
                                } else {
                                    Text(
                                        text = "Lab Community Room • Real-time",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = if (isDark) CyberCyan else ElectricBlue
                                    )
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = if (isDark) DarkBackground.copy(alpha = 0.95f) else LightBackground.copy(alpha = 0.95f)
                    )
                )
            },
            bottomBar = {
                ChatInputBar(
                    text = uiState.inputText,
                    onTextChanged = viewModel::onInputTextChanged,
                    onSend = viewModel::sendMessage,
                    isSending = uiState.isSending,
                    isDark = isDark,
                    typingUsers = uiState.typingUsers,
                    pendingAttachmentBytes = uiState.pendingAttachmentBytes,
                    pendingAttachmentName = uiState.pendingAttachmentName,
                    pendingAttachmentType = uiState.pendingAttachmentType,
                    onClearAttachment = viewModel::clearPendingAttachment,
                    onAttachFile = { bytes, name, type ->
                        viewModel.setPendingAttachment(bytes, name, type)
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (uiState.isLoading && uiState.messages.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = if (isDark) CyberCyan else ElectricBlue
                        )
                    }
                } else if (uiState.messages.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(CyberCyan.copy(alpha = 0.2f), ElectricBlue.copy(alpha = 0.25f))
                                        )
                                    )
                                    .border(1.5.dp, CyberCyan.copy(alpha = 0.6f), RoundedCornerShape(18.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Groups,
                                    contentDescription = "Chat",
                                    tint = if (isDark) CyberCyan else ElectricBlue,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Text(
                                text = "Welcome to Lab Discussion",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (isDark) TextPrimaryDark else TextPrimaryLight
                            )
                            Text(
                                text = "Ask hardware questions, discuss projects, or share robotics updates with students and instructors.",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isDark) TextSecondaryDark else TextSecondaryLight,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.messages, key = { it.id.ifBlank { "${it.clientTimeMillis}_${it.senderId}" } }) { msg ->
                            val isOwn = msg.senderId == uiState.currentUserId
                            val canDelete = isOwn || uiState.currentUserRole == "ADMIN"
                            ChatBubble(
                                message = msg,
                                isOwn = isOwn,
                                canDelete = canDelete,
                                onDelete = { viewModel.deleteMessage(msg.id) },
                                onPreviewImage = { url, name ->
                                    previewImageAttachment = Pair(url, name)
                                },
                                onOpenFile = { url, name ->
                                    scope.launch {
                                        Toast.makeText(context, "Saving & opening $name...", Toast.LENGTH_SHORT).show()
                                        com.roboticswala.hub.utils.ImageStorageHelper.saveToDownloads(context, name, url)
                                        com.roboticswala.hub.utils.ImageStorageHelper.openFile(context, name, url)
                                    }
                                },
                                isDark = isDark
                            )
                        }

                        if (uiState.typingUsers.isNotEmpty()) {
                            item(key = "typing_bubble") {
                                TypingBubble(typingUsers = uiState.typingUsers, isDark = isDark)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(
    message: ChatMessage,
    isOwn: Boolean,
    canDelete: Boolean,
    onDelete: () -> Unit,
    onPreviewImage: (url: String, name: String) -> Unit = { _, _ -> },
    onOpenFile: (url: String, name: String) -> Unit = { _, _ -> },
    isDark: Boolean
) {
    val timeFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val formattedTime = remember(message.timestamp, message.clientTimeMillis) {
        val date = message.timestamp ?: Date(message.clientTimeMillis)
        timeFormat.format(date)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isOwn) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier.widthIn(max = 310.dp),
            horizontalAlignment = if (isOwn) Alignment.End else Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .clip(
                        if (isOwn) {
                            RoundedCornerShape(topStart = 16.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
                        } else {
                            RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
                        }
                    )
                    .background(
                        if (isOwn) {
                            Brush.linearGradient(
                                if (isDark) listOf(ElectricBlue, CyberCyan.copy(alpha = 0.9f))
                                else listOf(ElectricBlue, CyberCyan)
                            )
                        } else {
                            Brush.linearGradient(
                                if (isDark) listOf(DarkSurfaceElevated, DarkSurface)
                                else listOf(LightSurface, LightSurface)
                            )
                        }
                    )
                    .border(
                        width = 1.dp,
                        color = if (isOwn) CyberCyanGlow.copy(alpha = 0.4f)
                        else if (isDark) DarkSurfaceBorder else LightSurfaceBorder,
                        shape = if (isOwn) {
                            RoundedCornerShape(topStart = 16.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
                        } else {
                            RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
                        }
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Column {
                    // Header: sender name + role tag (for others' messages)
                    if (!isOwn) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 3.dp)
                        ) {
                            Text(
                                text = message.senderName.ifBlank { "Robotics Member" },
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                color = if (message.senderRole == "ADMIN") CircuitWarning
                                else if (isDark) CyberCyan else ElectricBlue
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        if (message.senderRole == "ADMIN") CircuitWarning.copy(alpha = 0.2f)
                                        else CyberCyan.copy(alpha = 0.15f)
                                    )
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = if (message.senderRole == "ADMIN") "ADMIN" else "STUDENT",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    ),
                                    color = if (message.senderRole == "ADMIN") CircuitWarning
                                    else if (isDark) CyberCyan else ElectricBlue
                                )
                            }
                        }
                    }

                    // Attachment render (if present)
                    if (message.attachmentUrl.isNotBlank()) {
                        if (message.attachmentType == "IMAGE") {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        onPreviewImage(
                                            message.attachmentUrl,
                                            message.attachmentName.ifBlank { "image_${System.currentTimeMillis()}.jpg" }
                                        )
                                    }
                            ) {
                                AsyncImage(
                                    model = message.attachmentUrl,
                                    contentDescription = message.attachmentName.ifBlank { "Image" },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 120.dp, max = 220.dp)
                                        .clip(RoundedCornerShape(10.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        } else {
                            // Document / File Card
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (isOwn) Color.White.copy(alpha = 0.18f)
                                        else if (isDark) DarkBackground.copy(alpha = 0.6f)
                                        else LightBackground.copy(alpha = 0.7f)
                                    )
                                    .border(
                                        1.dp,
                                        if (isOwn) Color.White.copy(alpha = 0.35f)
                                        else CyberCyan.copy(alpha = 0.4f),
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable {
                                        onOpenFile(
                                            message.attachmentUrl,
                                            message.attachmentName.ifBlank { "document" }
                                        )
                                    }
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(if (isOwn) Color.White.copy(alpha = 0.25f) else CyberCyan.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (message.attachmentType == "DOCUMENT") Icons.Filled.Description else Icons.Filled.InsertDriveFile,
                                        contentDescription = null,
                                        tint = if (isOwn) Color.White else CyberCyan,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = message.attachmentName.ifBlank { "Document" },
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp),
                                        color = if (isOwn) Color.White else if (isDark) TextPrimaryDark else TextPrimaryLight,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "Tap to view & download",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                        color = if (isOwn) Color.White.copy(alpha = 0.8f) else CyberCyan
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Filled.Download,
                                    contentDescription = "Open",
                                    tint = if (isOwn) Color.White.copy(alpha = 0.85f) else CyberCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    // Message text
                    if (message.message.isNotBlank() &&
                        !(message.attachmentUrl.isNotBlank() && (message.message == "📷 Photo" || message.message == "📄 ${message.attachmentName}"))) {
                        Text(
                            text = message.message,
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.5.sp),
                            color = if (isOwn) Color.White
                            else if (isDark) TextPrimaryDark else TextPrimaryLight
                        )

                        if (message.message.contains("http://") || message.message.contains("https://")) {
                            val url = message.message.split("\\s+".toRegex()).firstOrNull { it.startsWith("http://") || it.startsWith("https://") }
                            if (url != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isOwn) Color.White.copy(alpha = 0.22f) else CyberCyan.copy(alpha = 0.15f))
                                        .clickable {
                                            try {
                                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                                context.startActivity(intent)
                                            } catch (_: Exception) {}
                                        }
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.OpenInNew,
                                        contentDescription = "Open Link",
                                        tint = if (isOwn) Color.White else CyberCyan,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = if (url.contains("drive.google.com")) "Open in Google Drive ➔" else "Open Link ➔",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                                        color = if (isOwn) Color.White else CyberCyan
                                    )
                                }
                            }
                        }
                    }

                    // Footer: time + optional delete button
                    Row(
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(top = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formattedTime,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = if (isOwn) Color.White.copy(alpha = 0.75f)
                            else if (isDark) TextSecondaryDark else TextSecondaryLight
                        )
                        if (canDelete && message.id.isNotBlank()) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Filled.DeleteOutline,
                                contentDescription = "Delete message",
                                tint = if (isOwn) Color.White.copy(alpha = 0.75f) else CircuitError.copy(alpha = 0.7f),
                                modifier = Modifier
                                    .size(13.dp)
                                    .clickable(onClick = onDelete)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ImageViewerDialog(
    imageUrl: String,
    imageName: String,
    onDismiss: () -> Unit,
    onSaveToDevice: () -> Unit,
    onOpenExternal: () -> Unit,
    isDark: Boolean
) {
    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.92f))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isDark) DarkSurface else LightSurface)
                    .border(
                        1.dp,
                        if (isDark) CyberCyan.copy(alpha = 0.4f) else ElectricBlue.copy(alpha = 0.3f),
                        RoundedCornerShape(20.dp)
                    )
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = imageName.ifBlank { "Lab Image" },
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (isDark) TextPrimaryDark else TextPrimaryLight,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Robotics Lab Shared Media",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isDark) CyberCyan else ElectricBlue
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close",
                            tint = if (isDark) TextSecondaryDark else TextSecondaryLight
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Image Container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 200.dp, max = 380.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.Black.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = imageName,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 380.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Brush.horizontalGradient(listOf(ElectricBlue, CyberCyan)))
                            .clickable(onClick = onSaveToDevice),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Download,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Save to Downloads",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .height(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isDark) DarkSurfaceElevated else LightSurfaceBorder)
                            .border(1.dp, CyberCyan.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .clickable(onClick = onOpenExternal)
                            .padding(horizontal = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.OpenInNew,
                            contentDescription = "Open External",
                            tint = if (isDark) CyberCyan else ElectricBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TypingBubble(
    typingUsers: List<String>,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "typing_dots")
    val dot1Alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot1"
    )
    val dot2Alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot2"
    )
    val dot3Alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot3"
    )

    val label = if (typingUsers.size == 1) "${typingUsers.first()} is typing"
    else "${typingUsers.take(2).joinToString(" & ")} are typing"

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 4.dp))
                .background(if (isDark) DarkSurfaceElevated else LightSurface)
                .border(
                    1.dp,
                    if (isDark) CyberCyan.copy(alpha = 0.4f) else ElectricBlue.copy(alpha = 0.3f),
                    RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 4.dp)
                )
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = if (isDark) CyberCyan else ElectricBlue
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(CyberCyan.copy(alpha = dot1Alpha))
                    )
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(CyberCyan.copy(alpha = dot2Alpha))
                    )
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(CyberCyan.copy(alpha = dot3Alpha))
                    )
                }
            }
        }
    }
}

@Composable
fun ChatInputBar(
    text: String,
    onTextChanged: (String) -> Unit,
    onSend: () -> Unit,
    isSending: Boolean,
    isDark: Boolean,
    typingUsers: List<String> = emptyList(),
    pendingAttachmentBytes: ByteArray? = null,
    pendingAttachmentName: String = "",
    pendingAttachmentType: String = "",
    onClearAttachment: () -> Unit = {},
    onAttachFile: (ByteArray, String, String) -> Unit = { _, _, _ -> }
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var showAttachmentDialog by remember { mutableStateOf(false) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, stream)
            val bytes = stream.toByteArray()
            val fileName = "IMG_${System.currentTimeMillis()}.jpg"
            onAttachFile(bytes, fileName, "IMAGE")
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            try {
                val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                if (bytes != null) {
                    if (bytes.size > 15 * 1024 * 1024) {
                        Toast.makeText(context, "Image exceeds 15 MB limit", Toast.LENGTH_SHORT).show()
                    } else {
                        val name = getFileNameFromUri(context, uri) ?: "IMG_${System.currentTimeMillis()}.jpg"
                        onAttachFile(bytes, name, "IMAGE")
                    }
                }
            } catch (_: Exception) {
                Toast.makeText(context, "Could not open image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val documentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            try {
                val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                if (bytes != null) {
                    if (bytes.size > 15 * 1024 * 1024) {
                        Toast.makeText(context, "File exceeds 15 MB limit", Toast.LENGTH_SHORT).show()
                    } else {
                        val name = getFileNameFromUri(context, uri) ?: "DOC_${System.currentTimeMillis()}"
                        val mime = context.contentResolver.getType(uri).orEmpty().lowercase()
                        val type = when {
                            mime.startsWith("image/") -> "IMAGE"
                            mime.contains("pdf") || name.endsWith(".pdf", ignoreCase = true) -> "DOCUMENT"
                            else -> "FILE"
                        }
                        onAttachFile(bytes, name, type)
                    }
                }
            } catch (_: Exception) {
                Toast.makeText(context, "Could not open file", Toast.LENGTH_SHORT).show()
            }
        }
    }

    if (showAttachmentDialog) {
        AlertDialog(
            onDismissRequest = { showAttachmentDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.AttachFile,
                        contentDescription = null,
                        tint = CyberCyan
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Share with Community",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (isDark) TextPrimaryDark else TextPrimaryLight
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AttachmentOptionRow(
                        icon = Icons.Filled.CloudQueue,
                        title = "Robotics Hub Google Drive",
                        subtitle = "Open shared high-res storage folder",
                        iconColor = CyberCyan,
                        isDark = isDark,
                        onClick = {
                            showAttachmentDialog = false
                            try {
                                val driveUrl = "https://drive.google.com/drive/folders/1tBxC2BmaPW9Xy-35PMbBgwbuuIFAt0JY?usp=sharing"
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(driveUrl))
                                context.startActivity(intent)
                            } catch (_: Exception) {
                                Toast.makeText(context, "Could not open Google Drive", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                    AttachmentOptionRow(
                        icon = Icons.Filled.AddLink,
                        title = "Share Google Drive Link",
                        subtitle = "Insert shared folder link into chat",
                        iconColor = ElectricBlue,
                        isDark = isDark,
                        onClick = {
                            showAttachmentDialog = false
                            val driveUrl = "https://drive.google.com/drive/folders/1tBxC2BmaPW9Xy-35PMbBgwbuuIFAt0JY?usp=sharing"
                            onInputTextChange(if (text.isBlank()) "📁 Shared Robotics Drive: $driveUrl" else "$text\n📁 Drive Link: $driveUrl")
                        }
                    )
                    AttachmentOptionRow(
                        icon = Icons.Filled.PhotoCamera,
                        title = "Live Camera",
                        subtitle = "Capture and share photo",
                        iconColor = CyberCyan,
                        isDark = isDark,
                        onClick = {
                            showAttachmentDialog = false
                            cameraLauncher.launch(null)
                        }
                    )
                    AttachmentOptionRow(
                        icon = Icons.Filled.Image,
                        title = "Gallery / Photos",
                        subtitle = "Select diagrams, photos from gallery (High-Res)",
                        iconColor = ElectricBlue,
                        isDark = isDark,
                        onClick = {
                            showAttachmentDialog = false
                            galleryLauncher.launch("image/*")
                        }
                    )
                    AttachmentOptionRow(
                        icon = Icons.Filled.Description,
                        title = "Documents & PDFs",
                        subtitle = "Share PDF manuals, datasheets, reports",
                        iconColor = CircuitSuccess,
                        isDark = isDark,
                        onClick = {
                            showAttachmentDialog = false
                            documentLauncher.launch("*/*")
                        }
                    )
                    AttachmentOptionRow(
                        icon = Icons.Filled.FolderZip,
                        title = "Files & Archives",
                        subtitle = "Share code, zip, or project files",
                        iconColor = CircuitWarning,
                        isDark = isDark,
                        onClick = {
                            showAttachmentDialog = false
                            documentLauncher.launch("*/*")
                        }
                    )
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showAttachmentDialog = false }) {
                    Text(
                        text = "Cancel",
                        color = if (isDark) TextSecondaryDark else TextSecondaryLight
                    )
                }
            },
            containerColor = if (isDark) DarkSurfaceElevated else LightSurface,
            shape = RoundedCornerShape(20.dp)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .imePadding()
            .background(if (isDark) DarkSurface.copy(alpha = 0.98f) else LightSurface.copy(alpha = 0.98f))
            .border(1.dp, if (isDark) DarkSurfaceBorder else LightSurfaceBorder)
    ) {
        val isTyping = typingUsers.isNotEmpty() || text.isNotBlank()
        if (isTyping) {
            val typingText = when {
                typingUsers.isNotEmpty() -> {
                    if (typingUsers.size == 1) "${typingUsers.first()} is typing..."
                    else "${typingUsers.take(2).joinToString(" & ")} are typing..."
                }
                else -> "You are typing..."
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "✍️ $typingText",
                    fontSize = 11.sp,
                    color = CyberCyan,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Pending attachment preview
        if (pendingAttachmentBytes != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isDark) DarkSurfaceElevated else LightSurfaceBorder.copy(alpha = 0.45f))
                    .border(1.dp, CyberCyan.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val icon = when (pendingAttachmentType) {
                    "IMAGE" -> Icons.Filled.Image
                    "DOCUMENT" -> Icons.Filled.Description
                    else -> Icons.Filled.InsertDriveFile
                }
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(CyberCyan.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = CyberCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = pendingAttachmentName.ifBlank { "Attached file" },
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = if (isDark) TextPrimaryDark else TextPrimaryLight,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${(pendingAttachmentBytes.size / 1024).coerceAtLeast(1)} KB • Ready to send",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = CyberCyan
                    )
                }
                IconButton(
                    onClick = onClearAttachment,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Remove attachment",
                        tint = CircuitError,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp)
        ) {
            // Attachment Button
            IconButton(
                onClick = { showAttachmentDialog = true },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isDark) DarkSurfaceElevated else LightSurfaceBorder.copy(alpha = 0.35f))
            ) {
                Icon(
                    imageVector = Icons.Filled.AttachFile,
                    contentDescription = "Attach file or photo",
                    tint = if (isDark) CyberCyan else ElectricBlue,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            OutlinedTextField(
                value = text,
                onValueChange = onTextChanged,
                placeholder = {
                    Text(
                        text = if (pendingAttachmentBytes != null) "Add caption (optional)..." else "Type a message...",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                        color = if (isDark) TextSecondaryDark else TextSecondaryLight
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = if (isDark) DarkSurfaceElevated else LightSurfaceBorder.copy(alpha = 0.3f),
                    unfocusedContainerColor = if (isDark) DarkSurfaceElevated else LightSurfaceBorder.copy(alpha = 0.3f),
                    focusedBorderColor = if (isDark) CyberCyan else ElectricBlue,
                    unfocusedBorderColor = if (isDark) DarkSurfaceBorder else LightSurfaceBorder,
                    focusedTextColor = if (isDark) TextPrimaryDark else TextPrimaryLight,
                    unfocusedTextColor = if (isDark) TextPrimaryDark else TextPrimaryLight
                ),
                maxLines = 4,
                singleLine = false
            )

            Spacer(modifier = Modifier.width(8.dp))

            val canSend = (text.isNotBlank() || pendingAttachmentBytes != null) && !isSending
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        if (canSend) Brush.linearGradient(listOf(CyberCyan, ElectricBlue))
                        else Brush.linearGradient(listOf(Color.Gray.copy(alpha = 0.4f), Color.Gray.copy(alpha = 0.4f)))
                    )
                    .clickable(enabled = canSend, onClick = onSend),
                contentAlignment = Alignment.Center
            ) {
                if (isSending) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AttachmentOptionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    iconColor: Color,
    isDark: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isDark) DarkSurface.copy(alpha = 0.8f) else LightSurfaceBorder.copy(alpha = 0.35f))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(iconColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconColor,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = if (isDark) TextPrimaryDark else TextPrimaryLight
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = if (isDark) TextSecondaryDark else TextSecondaryLight
            )
        }
    }
}

private fun getFileNameFromUri(context: Context, uri: Uri): String? {
    var result: String? = null
    if (uri.scheme == "content") {
        try {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (index >= 0) {
                        result = cursor.getString(index)
                    }
                }
            }
        } catch (_: Exception) {}
    }
    if (result.isNullOrBlank()) {
        result = uri.path
        val cut = result?.lastIndexOf('/') ?: -1
        if (cut != -1) {
            result = result?.substring(cut + 1)
        }
    }
    return result
}

