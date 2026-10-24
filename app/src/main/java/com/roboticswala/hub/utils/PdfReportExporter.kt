package com.roboticswala.hub.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.roboticswala.hub.data.models.AdminHubAnalytics
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfReportExporter {

    fun exportReportToStorage(
        context: Context,
        reportTitle: String,
        analytics: AdminHubAnalytics?
    ): Result<String> {
        return try {
            val document = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // Standard A4 (72 dpi)
            val page = document.startPage(pageInfo)
            val canvas = page.canvas

            val paint = Paint()
            val dateFormat = SimpleDateFormat("dd MMMM yyyy, hh:mm a", Locale.getDefault())
            val dateStr = dateFormat.format(Date())

            // Header Background Bar
            paint.color = Color.rgb(11, 14, 20)
            canvas.drawRect(0f, 0f, 595f, 100f, paint)

            // Accent Line
            paint.color = Color.rgb(0, 212, 255)
            canvas.drawRect(0f, 96f, 595f, 100f, paint)

            // Title
            paint.color = Color.WHITE
            paint.textSize = 18f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("ROBOTICS WALA HUB (RW HUB)", 36f, 42f, paint)

            paint.textSize = 11f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            paint.color = Color.rgb(142, 158, 181)
            canvas.drawText("Official Laboratory Analytics & Performance Audit Report", 36f, 62f, paint)
            canvas.drawText("Generated on: $dateStr", 36f, 80f, paint)

            // Report Header
            var yPos = 140f
            paint.color = Color.rgb(0, 64, 193)
            paint.textSize = 16f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText(reportTitle, 36f, yPos, paint)

            yPos += 24f
            paint.color = Color.DKGRAY
            paint.textSize = 10f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
            canvas.drawText("Verified system report compiled from Cloud Firestore live telemetry.", 36f, yPos, paint)

            yPos += 30f
            paint.color = Color.LTGRAY
            canvas.drawLine(36f, yPos, 559f, yPos, paint)
            yPos += 24f

            // Data section
            val safeAnalytics = analytics ?: AdminHubAnalytics()
            val metrics = when {
                reportTitle.contains("Attendance", ignoreCase = true) -> listOf(
                    "Today's Check-Ins" to "${safeAnalytics.todayCheckIns}",
                    "Today's Present Students" to "${safeAnalytics.todayPresentStudents}",
                    "Average Attendance Rate" to "${String.format(Locale.US, "%.1f", safeAnalytics.averageAttendancePercentage)}%",
                    "Total Recorded Lab Hours" to "${String.format(Locale.US, "%.1f", safeAnalytics.totalLabHours)} hrs",
                    "Average Working Hours / Student" to "${String.format(Locale.US, "%.1f", safeAnalytics.averageWorkingHoursPerStudent)} hrs",
                    "Total Registered Students" to "${safeAnalytics.totalStudents}"
                )
                reportTitle.contains("Project", ignoreCase = true) -> listOf(
                    "Total Lab Projects" to "${safeAnalytics.totalProjects}",
                    "Active Research Projects" to "${safeAnalytics.activeProjects}",
                    "Completed Deliverables" to "${safeAnalytics.completedProjects}",
                    "Planning Stage Projects" to "${safeAnalytics.planningProjects}",
                    "Average Project Progress" to "${String.format(Locale.US, "%.1f", safeAnalytics.averageProjectProgress)}%",
                    "Total Active Tasks" to "${safeAnalytics.totalTasks}",
                    "Task Completion Rate" to "${String.format(Locale.US, "%.1f", safeAnalytics.taskCompletionRate)}%"
                )
                reportTitle.contains("Inventory", ignoreCase = true) || reportTitle.contains("Equipment", ignoreCase = true) -> listOf(
                    "Total Equipment Types" to "${safeAnalytics.totalEquipmentItems}",
                    "Total Hardware Units in Stock" to "${safeAnalytics.totalEquipmentStock}",
                    "Currently Issued Hardware Units" to "${safeAnalytics.issuedEquipmentCount}",
                    "Low Stock Alerts" to "${safeAnalytics.lowStockItemsCount}",
                    "Out of Stock Alerts" to "${safeAnalytics.outOfStockItemsCount}",
                    "Overdue Component Returns" to "${safeAnalytics.overdueReturnsCount}"
                )
                reportTitle.contains("Budget", ignoreCase = true) || reportTitle.contains("Expense", ignoreCase = true) -> listOf(
                    "Total Approved Lab Grant Budget" to "₹${String.format(Locale.US, "%.2f", safeAnalytics.totalApprovedBudget)}",
                    "Total Approved Project Expenses" to "₹${String.format(Locale.US, "%.2f", safeAnalytics.totalApprovedExpenses)}",
                    "Pending Expense Claims" to "₹${String.format(Locale.US, "%.2f", safeAnalytics.totalPendingExpenses)}",
                    "Remaining Available Lab Balance" to "₹${String.format(Locale.US, "%.2f", safeAnalytics.totalRemainingBudget)}",
                    "Budget Utilization Rate" to "${String.format(Locale.US, "%.1f", safeAnalytics.budgetUtilizationPercentage)}%",
                    "Total Funded Research Projects" to "${safeAnalytics.totalProjects}"
                )
                else -> listOf(
                    "Total Registered Students" to "${safeAnalytics.totalStudents}",
                    "Active Lab Researchers" to "${safeAnalytics.activeStudents}",
                    "Approved Student Profiles" to "${safeAnalytics.approvedStudents}",
                    "Total Research Projects" to "${safeAnalytics.totalProjects}",
                    "Active Projects" to "${safeAnalytics.activeProjects}",
                    "Average Progress" to "${String.format(Locale.US, "%.1f", safeAnalytics.averageProjectProgress)}%",
                    "Total Hardware Items" to "${safeAnalytics.totalEquipmentItems}",
                    "Equipment Stock Total" to "${safeAnalytics.totalEquipmentStock}",
                    "Issued Equipment" to "${safeAnalytics.issuedEquipmentCount}",
                    "Approved Budget" to "₹${String.format(Locale.US, "%.2f", safeAnalytics.totalApprovedBudget)}",
                    "Approved Expenses" to "₹${String.format(Locale.US, "%.2f", safeAnalytics.totalApprovedExpenses)}",
                    "Total Lab Achievements" to "${safeAnalytics.totalAchievements}",
                    "Approved Achievements" to "${safeAnalytics.approvedAchievements}"
                )
            }

            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            for ((key, value) in metrics) {
                paint.color = Color.rgb(245, 248, 252)
                canvas.drawRect(36f, yPos - 16f, 559f, yPos + 10f, paint)

                paint.color = Color.rgb(20, 30, 45)
                paint.textSize = 11f
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                canvas.drawText(key, 48f, yPos, paint)

                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                paint.color = Color.rgb(0, 102, 255)
                canvas.drawText(value, 380f, yPos, paint)

                yPos += 30f
            }

            // Footer
            yPos = 790f
            paint.color = Color.LTGRAY
            canvas.drawLine(36f, yPos, 559f, yPos, paint)
            yPos += 18f
            paint.color = Color.GRAY
            paint.textSize = 9f
            canvas.drawText("Confidential • RW HUB Robotics Lab Management System • Verified PDF Export", 36f, yPos, paint)

            document.finishPage(page)

            val safeTitle = reportTitle.replace("[^a-zA-Z0-9]".toRegex(), "_").take(30)
            val fileName = "RW_HUB_${safeTitle}_${System.currentTimeMillis()}.pdf"

            val savedLocation = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val uri: Uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                    ?: throw Exception("Failed to insert MediaStore download entry")
                context.contentResolver.openOutputStream(uri)?.use { out ->
                    document.writeTo(out)
                }
                "Downloads/$fileName"
            } else {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (!downloadsDir.exists()) downloadsDir.mkdirs()
                val file = File(downloadsDir, fileName)
                FileOutputStream(file).use { out ->
                    document.writeTo(out)
                }
                file.absolutePath
            }

            document.close()
            Result.success(savedLocation)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
