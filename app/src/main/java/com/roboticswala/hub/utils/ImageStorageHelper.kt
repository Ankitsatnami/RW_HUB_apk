package com.roboticswala.hub.utils

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import androidx.core.content.FileProvider
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object ImageStorageHelper {

    /**
     * Compresses and resizes an image ByteArray to optimize storage and transfer.
     */
    fun compressImage(
        bytes: ByteArray,
        maxDimension: Int = 1024,
        quality: Int = 82
    ): ByteArray {
        return try {
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)

            var sampleSize = 1
            var w = options.outWidth
            var h = options.outHeight

            while (w > maxDimension || h > maxDimension) {
                w /= 2
                h /= 2
                sampleSize *= 2
            }

            val decodeOptions = BitmapFactory.Options().apply {
                inSampleSize = sampleSize.coerceAtLeast(1)
            }
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, decodeOptions) ?: return bytes

            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            val result = outputStream.toByteArray()
            bitmap.recycle()
            if (result.isNotEmpty()) result else bytes
        } catch (_: Exception) {
            bytes
        }
    }

    /**
     * Uploads file bytes to Firebase Storage. If Storage is restricted by rules or fails,
     * automatically converts to a Base64 Data URI so it NEVER fails and displays seamlessly in Coil.
     */
    suspend fun uploadToStorageWithBase64Fallback(
        storage: FirebaseStorage,
        folder: String,
        fileName: String,
        fileBytes: ByteArray,
        mimeType: String = "image/jpeg"
    ): String = withContext(Dispatchers.IO) {
        // Keep original high-resolution bytes for Firebase Storage (only optimize if > 8MB)
        val storageBytes = if (mimeType.startsWith("image/") && fileBytes.size > 8 * 1024 * 1024) {
            compressImage(fileBytes, maxDimension = 2560, quality = 94)
        } else {
            fileBytes
        }

        // Try Firebase Storage first with full original clarity
        try {
            val safeName = "${System.currentTimeMillis()}_${fileName.replace("[^a-zA-Z0-9._-]".toRegex(), "_")}"
            val ref = storage.reference.child("$folder/$safeName")
            val metadata = StorageMetadata.Builder()
                .setContentType(mimeType)
                .build()

            ref.putBytes(storageBytes, metadata).await()
            val downloadUrl = ref.downloadUrl.await().toString()
            if (downloadUrl.isNotBlank()) {
                return@withContext downloadUrl
            }
        } catch (_: Exception) {
            // Storage upload failed (permissions/offline), proceed to Base64 fallback
        }

        // Base64 Data URI fallback (used ONLY if Storage upload fails)
        val fallbackBytes = if (mimeType.startsWith("image/")) {
            compressImage(fileBytes, maxDimension = 1200, quality = 86)
        } else {
            fileBytes
        }
        val base64Data = Base64.encodeToString(fallbackBytes, Base64.NO_WRAP)
        "data:$mimeType;base64,$base64Data"
    }

    /**
     * Saves a file (from either HTTPS URL or Base64 Data URI) directly to the device Downloads folder.
     */
    suspend fun saveToDownloads(
        context: Context,
        fileName: String,
        fileUrlOrBase64: String,
        mimeType: String = "*/*"
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val rawBytes: ByteArray = when {
                fileUrlOrBase64.startsWith("data:") -> {
                    val base64Part = fileUrlOrBase64.substringAfter("base64,")
                    Base64.decode(base64Part, Base64.DEFAULT)
                }
                fileUrlOrBase64.startsWith("http") -> {
                    val url = java.net.URL(fileUrlOrBase64)
                    val connection = url.openConnection()
                    connection.connectTimeout = 10000
                    connection.readTimeout = 15000
                    connection.getInputStream().use { it.readBytes() }
                }
                else -> {
                    return@withContext Result.failure(Exception("Invalid file URL or data"))
                }
            }

            val sanitizedName = fileName.ifBlank { "RW_HUB_File_${System.currentTimeMillis()}" }
            val resolvedMime = when {
                sanitizedName.endsWith(".pdf", ignoreCase = true) -> "application/pdf"
                sanitizedName.endsWith(".jpg", ignoreCase = true) || sanitizedName.endsWith(".jpeg", ignoreCase = true) -> "image/jpeg"
                sanitizedName.endsWith(".png", ignoreCase = true) -> "image/png"
                sanitizedName.endsWith(".zip", ignoreCase = true) -> "application/zip"
                mimeType.isNotBlank() && mimeType != "*/*" -> mimeType
                else -> "application/octet-stream"
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, sanitizedName)
                    put(MediaStore.MediaColumns.MIME_TYPE, resolvedMime)
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val uri: Uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                    ?: throw Exception("Could not insert MediaStore record")

                context.contentResolver.openOutputStream(uri)?.use { out ->
                    out.write(rawBytes)
                }
                Result.success("Saved to Downloads/$sanitizedName")
            } else {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (!downloadsDir.exists()) downloadsDir.mkdirs()
                val targetFile = File(downloadsDir, sanitizedName)
                FileOutputStream(targetFile).use { out ->
                    out.write(rawBytes)
                }
                Result.success(targetFile.absolutePath)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Opens a file directly in an external viewer or browser.
     */
    fun openFile(context: Context, fileName: String, fileUrlOrBase64: String) {
        try {
            if (fileUrlOrBase64.startsWith("http")) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(fileUrlOrBase64)).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            } else if (fileUrlOrBase64.startsWith("data:")) {
                // Save temporarily to cache and launch FileProvider intent
                val base64Part = fileUrlOrBase64.substringAfter("base64,")
                val bytes = Base64.decode(base64Part, Base64.DEFAULT)
                val cacheDir = File(context.cacheDir, "attachments").apply { if (!exists()) mkdirs() }
                val tempFile = File(cacheDir, fileName.ifBlank { "attachment_${System.currentTimeMillis()}" })
                FileOutputStream(tempFile).use { it.write(bytes) }

                val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", tempFile)
                val mime = when {
                    fileName.endsWith(".pdf", ignoreCase = true) -> "application/pdf"
                    fileName.endsWith(".png", ignoreCase = true) -> "image/png"
                    fileName.endsWith(".jpg", ignoreCase = true) || fileName.endsWith(".jpeg", ignoreCase = true) -> "image/jpeg"
                    else -> "*/*"
                }
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, mime)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
                }
                context.startActivity(Intent.createChooser(intent, "Open with").apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK })
            }
        } catch (_: Exception) {
            // Fallback: If intent fails, attempt opening via browser or intent without type
            try {
                if (fileUrlOrBase64.startsWith("http")) {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(fileUrlOrBase64)).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK })
                }
            } catch (_: Exception) {}
        }
    }
}
