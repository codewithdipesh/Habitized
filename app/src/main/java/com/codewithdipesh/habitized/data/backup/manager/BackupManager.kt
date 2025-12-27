package com.codewithdipesh.habitized.data.backup.manager

import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.codewithdipesh.habitized.DATABASE_VERSION
import com.codewithdipesh.habitized.data.backup.model.BackupData
import com.codewithdipesh.habitized.data.backup.model.BackupFileInfo
import com.codewithdipesh.habitized.data.backup.model.BackupMetadata
import com.codewithdipesh.habitized.data.backup.model.BackupPreferences
import com.codewithdipesh.habitized.data.backup.model.GoalBackup
import com.codewithdipesh.habitized.data.backup.model.HabitBackup
import com.codewithdipesh.habitized.data.backup.model.HabitProgressBackup
import com.codewithdipesh.habitized.data.backup.model.ImageProgressBackup
import com.codewithdipesh.habitized.data.backup.model.OneTimeTaskBackup
import com.codewithdipesh.habitized.data.backup.model.SubtaskBackup
import com.codewithdipesh.habitized.data.local.dao.GoalDao
import com.codewithdipesh.habitized.data.local.dao.HabitDao
import com.codewithdipesh.habitized.data.local.dao.HabitProgressDao
import com.codewithdipesh.habitized.data.local.dao.ImageProgressDao
import com.codewithdipesh.habitized.data.local.dao.OneTimeTaskDao
import com.codewithdipesh.habitized.data.local.dao.SubTaskDao
import com.codewithdipesh.habitized.data.sharedPref.HabitPreference
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

sealed class BackupResult {
    data class Success(val message: String, val fileName: String? = null) : BackupResult()
    data class Error(val message: String) : BackupResult()
}

@Singleton
class BackupManager @Inject constructor(
    private val context: Context,
    private val habitDao: HabitDao,
    private val habitProgressDao: HabitProgressDao,
    private val goalDao: GoalDao,
    private val subtaskDao: SubTaskDao,
    private val imageProgressDao: ImageProgressDao,
    private val oneTimeTaskDao: OneTimeTaskDao,
    private val imageBackupManager: ImageBackupManager,
    private val habitPreference: HabitPreference
) {
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    companion object {
        private const val BACKUP_FILE_PREFIX = "habitized_backup_"
        private const val BACKUP_FILE_EXTENSION = ".json"
    }

    suspend fun createBackup(backupType: String = "manual"): BackupResult = withContext(Dispatchers.IO) {
        try {
            // Collect all data from database
            val habits = habitDao.getAllHabits().map { HabitBackup.fromEntity(it) }
            val habitProgress = habitProgressDao.getAllProgress().map { HabitProgressBackup.fromEntity(it) }
            val goals = goalDao.getAllGoals().map { GoalBackup.fromEntity(it) }
            val subtasks = subtaskDao.getAllSubtasks().map { SubtaskBackup.fromEntity(it) }
            val imageProgress = imageProgressDao.getAllImageProgress().map { ImageProgressBackup.fromEntity(it) }
            val oneTimeTasks = oneTimeTaskDao.getAllTasks().map { OneTimeTaskBackup.fromEntity(it) }

            // Collect images
            val images = imageBackupManager.encodeAllImages(imageProgress)

            // Create metadata
            val timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            val versionName = packageInfo.versionName ?: "unknown"
            val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode.toInt()
            } else {
                @Suppress("DEPRECATION")
                packageInfo.versionCode
            }
            val metadata = BackupMetadata(
                appVersion = versionName,
                appVersionCode = versionCode,
                databaseVersion = DATABASE_VERSION,
                backupTimestamp = timestamp,
                backupType = backupType,
                deviceModel = Build.MODEL,
                androidVersion = Build.VERSION.SDK_INT
            )

            // Create preferences
            val preferences = BackupPreferences(
                theme = habitPreference.getTheme(),
                introShown = !habitPreference.getIntro(),
                autoBackupEnabled = habitPreference.isAutoBackupEnabled()
            )

            // Create backup data
            val backupData = BackupData(
                metadata = metadata,
                habits = habits,
                habitProgress = habitProgress,
                goals = goals,
                subtasks = subtasks,
                imageProgress = imageProgress,
                oneTimeTasks = oneTimeTasks,
                preferences = preferences,
                images = images
            )

            // Convert to JSON
            val jsonContent = gson.toJson(backupData)

            // Save to Downloads folder
            val fileName = generateFileName()
            val result = saveToDownloads(fileName, jsonContent)

            if (result) {
                // Update last backup date
                habitPreference.updateLastBackupDate(timestamp)
                BackupResult.Success("Backup created successfully", fileName)
            } else {
                BackupResult.Error("Failed to save backup file")
            }
        } catch (e: Exception) {
            BackupResult.Error("Backup failed: ${e.message}")
        }
    }

    suspend fun restoreBackup(uri: Uri): BackupResult = withContext(Dispatchers.IO) {
        try {
            // Read backup file
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val jsonContent = inputStream?.bufferedReader()?.use { it.readText() }
                ?: return@withContext BackupResult.Error("Could not read backup file")

            // Parse backup data
            val backupData = try {
                gson.fromJson(jsonContent, BackupData::class.java)
            } catch (e: Exception) {
                return@withContext BackupResult.Error("Invalid backup file format")
            }

            // Validate backup
            val validationResult = validateBackup(backupData)
            if (validationResult != null) {
                return@withContext BackupResult.Error(validationResult)
            }

            // Clear existing data
            clearAllData()

            // Restore in correct order for foreign key integrity
            // 1. Goals (no dependencies)
            backupData.goals.forEach { goal ->
                goalDao.insertGoal(goal.toEntity())
            }

            // 2. Habits (depends on goals)
            backupData.habits.forEach { habit ->
                habitDao.insertHabit(habit.toEntity())
            }

            // 3. HabitProgress (depends on habits)
            backupData.habitProgress.forEach { progress ->
                habitProgressDao.insertProgress(progress.toEntity())
            }

            // 4. Subtasks (depends on habitProgress)
            backupData.subtasks.forEach { subtask ->
                subtaskDao.insertSubtask(subtask.toEntity())
            }

            // 5. ImageProgress (depends on habits)
            backupData.imageProgress.forEach { imageProgress ->
                imageProgressDao.insert(imageProgress.toEntity())
            }

            // 6. OneTimeTasks (independent)
            backupData.oneTimeTasks.forEach { task ->
                oneTimeTaskDao.insertTask(task.toEntity())
            }

            // 7. Restore images
            imageBackupManager.restoreImages(backupData.images)

            // 8. Restore preferences
            habitPreference.updateTheme(backupData.preferences.theme)
            habitPreference.updateIntro(!backupData.preferences.introShown)
            habitPreference.setAutoBackupEnabled(backupData.preferences.autoBackupEnabled)

            BackupResult.Success("Restore completed successfully")
        } catch (e: Exception) {
            BackupResult.Error("Restore failed: ${e.message}")
        }
    }


    suspend fun listAvailableBackups(): List<BackupFileInfo> = withContext(Dispatchers.IO) {
        val backups = mutableListOf<BackupFileInfo>()

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Use MediaStore for Android 10+
                val projection = arrayOf(
                    MediaStore.Downloads._ID,
                    MediaStore.Downloads.DISPLAY_NAME,
                    MediaStore.Downloads.SIZE,
                    MediaStore.Downloads.DATE_MODIFIED
                )

                val selection = "${MediaStore.Downloads.DISPLAY_NAME} LIKE ?"
                val selectionArgs = arrayOf("$BACKUP_FILE_PREFIX%$BACKUP_FILE_EXTENSION")

                context.contentResolver.query(
                    MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    "${MediaStore.Downloads.DATE_MODIFIED} DESC"
                )?.use { cursor ->
                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Downloads._ID)
                    val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Downloads.DISPLAY_NAME)
                    val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Downloads.SIZE)
                    val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Downloads.DATE_MODIFIED)

                    while (cursor.moveToNext()) {
                        val id = cursor.getLong(idColumn)
                        val name = cursor.getString(nameColumn)
                        val size = cursor.getLong(sizeColumn)
                        val dateModified = cursor.getLong(dateColumn)

                        val uri = Uri.withAppendedPath(
                            MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                            id.toString()
                        )

                        val dateTime = java.text.SimpleDateFormat(
                            "MMM dd, yyyy HH:mm",
                            java.util.Locale.getDefault()
                        ).format(java.util.Date(dateModified * 1000))

                        backups.add(
                            BackupFileInfo(
                                fileName = name,
                                displayName = name.removeSuffix(BACKUP_FILE_EXTENSION)
                                    .removePrefix(BACKUP_FILE_PREFIX),
                                dateTime = dateTime,
                                fileSizeBytes = size,
                                fileSizeDisplay = formatFileSize(size),
                                uri = uri.toString()
                            )
                        )
                    }
                }
            } else {
                // Direct file access for Android 9 and below
                val downloadsDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS
                )
                downloadsDir.listFiles { file ->
                    file.name.startsWith(BACKUP_FILE_PREFIX) &&
                            file.name.endsWith(BACKUP_FILE_EXTENSION)
                }?.sortedByDescending { it.lastModified() }?.forEach { file ->
                    val dateTime = java.text.SimpleDateFormat(
                        "MMM dd, yyyy HH:mm",
                        java.util.Locale.getDefault()
                    ).format(java.util.Date(file.lastModified()))

                    backups.add(
                        BackupFileInfo(
                            fileName = file.name,
                            displayName = file.name.removeSuffix(BACKUP_FILE_EXTENSION)
                                .removePrefix(BACKUP_FILE_PREFIX),
                            dateTime = dateTime,
                            fileSizeBytes = file.length(),
                            fileSizeDisplay = formatFileSize(file.length()),
                            uri = Uri.fromFile(file).toString()
                        )
                    )
                }
            }
        } catch (e: Exception) {
            // Return empty list on error
        }

        backups
    }

    suspend fun deleteBackup(uri: Uri): BackupResult = withContext(Dispatchers.IO) {
        try {
            val rowsDeleted = context.contentResolver.delete(uri, null, null)
            if (rowsDeleted > 0) {
                BackupResult.Success("Backup deleted successfully")
            } else {
                BackupResult.Error("Could not delete backup file")
            }
        } catch (e: Exception) {
            BackupResult.Error("Delete failed: ${e.message}")
        }
    }


    private fun validateBackup(backupData: BackupData): String? {
        // Check metadata
        if (backupData.metadata.appVersion.isEmpty()) {
            return "Invalid backup: missing app version"
        }

        // Check database version compatibility
        if (backupData.metadata.databaseVersion > DATABASE_VERSION) {
            return "Backup was created with a newer version of the app. Please update the app first."
        }

        return null
    }


    private suspend fun clearAllData() {
        // Delete in reverse order of dependencies
        subtaskDao.deleteAllSubtasks()
        imageProgressDao.deleteAll()
        habitProgressDao.deleteAllProgress()
        habitDao.deleteAllHabits()
        goalDao.deleteAllGoals()
        oneTimeTaskDao.deleteAllTasks()

        // Clear existing images
        imageBackupManager.clearExistingImages()
    }


    private fun generateFileName(): String {
        val timestamp = LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
        )
        return "$BACKUP_FILE_PREFIX$timestamp$BACKUP_FILE_EXTENSION"
    }

    private fun saveToDownloads(fileName: String, content: String): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Use MediaStore for Android 10+
                val contentValues = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                    put(MediaStore.Downloads.MIME_TYPE, "application/json")
                    put(MediaStore.Downloads.IS_PENDING, 1)
                }

                val uri = context.contentResolver.insert(
                    MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                    contentValues
                )

                uri?.let {
                    context.contentResolver.openOutputStream(it)?.use { outputStream ->
                        outputStream.write(content.toByteArray())
                    }

                    contentValues.clear()
                    contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
                    context.contentResolver.update(it, contentValues, null, null)
                    true
                } ?: false
            } else {
                // Direct file access for Android 9 and below
                val downloadsDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS
                )
                val file = File(downloadsDir, fileName)
                file.writeText(content)
                true
            }
        } catch (e: Exception) {
            false
        }
    }


    private fun formatFileSize(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> String.format("%.1f KB", bytes / 1024.0)
            else -> String.format("%.1f MB", bytes / (1024.0 * 1024.0))
        }
    }


    suspend fun getBackupSummary(uri: Uri): Map<String, Int>? = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val jsonContent = inputStream?.bufferedReader()?.use { it.readText() } ?: return@withContext null
            val backupData = gson.fromJson(jsonContent, BackupData::class.java)

            mapOf(
                "habits" to backupData.habits.size,
                "progress" to backupData.habitProgress.size,
                "goals" to backupData.goals.size,
                "tasks" to backupData.oneTimeTasks.size,
                "images" to backupData.images.size
            )
        } catch (e: Exception) {
            null
        }
    }
}
