package com.codewithdipesh.habitized.data.backup.model

import com.codewithdipesh.habitized.data.local.entity.GoalEntity
import com.codewithdipesh.habitized.data.local.entity.HabitEntity
import com.codewithdipesh.habitized.data.local.entity.HabitProgressEntity
import com.codewithdipesh.habitized.data.local.entity.ImageProgressEntity
import com.codewithdipesh.habitized.data.local.entity.OneTimeTaskEntity
import com.codewithdipesh.habitized.data.local.entity.SubtaskEntity
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID


data class BackupData(
    val metadata: BackupMetadata,
    val habits: List<HabitBackup>,
    val habitProgress: List<HabitProgressBackup>,
    val goals: List<GoalBackup>,
    val subtasks: List<SubtaskBackup>,
    val imageProgress: List<ImageProgressBackup>,
    val oneTimeTasks: List<OneTimeTaskBackup>,
    val preferences: BackupPreferences,
    val images: List<ImageFileData>
)


data class BackupMetadata(
    val appVersion: String,
    val appVersionCode: Int,
    val databaseVersion: Int,
    val backupTimestamp: String,
    val backupType: String,  // "manual" or "automatic"
    val deviceModel: String,
    val androidVersion: Int
)


data class BackupPreferences(
    val theme: String,
    val introShown: Boolean,
    val autoBackupEnabled: Boolean
)


data class ImageFileData(
    val fileName: String,
    val base64Content: String
)

data class HabitBackup(
    val habitId: String,
    val title: String,
    val description: String?,
    val type: String,
    val goalId: String?,
    val startDate: String,
    val frequency: String,
    val daysOfWeek: String,
    val daysOfMonth: String?,
    val reminderType: String?,
    val reminderFrom: String?,
    val reminderTo: String?,
    val reminderInterval: Int?,
    val reminderTime: String?,
    val isActive: Boolean,
    val colorKey: String,
    val countParam: String,
    val countTarget: Int?,
    val duration: String?,
    val currentStreak: Int,
    val maxStreak: Int
) {
    companion object {
        fun fromEntity(entity: HabitEntity): HabitBackup {
            return HabitBackup(
                habitId = entity.habit_id.toString(),
                title = entity.title,
                description = entity.description,
                type = entity.type,
                goalId = entity.goal_id?.toString(),
                startDate = entity.start_date.toString(),
                frequency = entity.frequency,
                daysOfWeek = entity.days_of_week,
                daysOfMonth = entity.daysOfMonth,
                reminderType = entity.reminderType,
                reminderFrom = entity.reminderFrom?.toString(),
                reminderTo = entity.reminderTo?.toString(),
                reminderInterval = entity.reminderInterval,
                reminderTime = entity.reminder_time?.toString(),
                isActive = entity.is_active,
                colorKey = entity.colorKey,
                countParam = entity.countParam,
                countTarget = entity.countTarget,
                duration = entity.duration,
                currentStreak = entity.currentStreak,
                maxStreak = entity.maxStreak
            )
        }
    }

    fun toEntity(): HabitEntity {
        return HabitEntity(
            habit_id = UUID.fromString(habitId),
            title = title,
            description = description,
            type = type,
            goal_id = goalId?.let { UUID.fromString(it) },
            start_date = LocalDate.parse(startDate),
            frequency = frequency,
            days_of_week = daysOfWeek,
            daysOfMonth = daysOfMonth,
            reminderType = reminderType,
            reminderFrom = reminderFrom?.let { LocalTime.parse(it) },
            reminderTo = reminderTo?.let { LocalTime.parse(it) },
            reminderInterval = reminderInterval,
            reminder_time = reminderTime?.let { LocalTime.parse(it) },
            is_active = isActive,
            colorKey = colorKey,
            countParam = countParam,
            countTarget = countTarget,
            duration = duration,
            currentStreak = currentStreak,
            maxStreak = maxStreak
        )
    }
}

data class HabitProgressBackup(
    val progressId: String,
    val habitId: String,
    val date: String,
    val type: String,
    val countParam: String,
    val currentCount: Int?,
    val targetCount: Int?,
    val targetDurationValue: String?,
    val currentSessionNumber: Int?,
    val targetSessionNumber: Int?,
    val status: String,
    val notes: String?,
    val excuse: String?
) {
    companion object {
        fun fromEntity(entity: HabitProgressEntity): HabitProgressBackup {
            return HabitProgressBackup(
                progressId = entity.progressId.toString(),
                habitId = entity.habitId.toString(),
                date = entity.date.toString(),
                type = entity.type,
                countParam = entity.countParam,
                currentCount = entity.currentCount,
                targetCount = entity.targetCount,
                targetDurationValue = entity.targetDurationValue,
                currentSessionNumber = entity.currentSessionNumber,
                targetSessionNumber = entity.targetSessionNumber,
                status = entity.status,
                notes = entity.notes,
                excuse = entity.excuse
            )
        }
    }

    fun toEntity(): HabitProgressEntity {
        return HabitProgressEntity(
            progressId = UUID.fromString(progressId),
            habitId = UUID.fromString(habitId),
            date = LocalDate.parse(date),
            type = type,
            countParam = countParam,
            currentCount = currentCount,
            targetCount = targetCount,
            targetDurationValue = targetDurationValue,
            currentSessionNumber = currentSessionNumber,
            targetSessionNumber = targetSessionNumber,
            status = status,
            notes = notes,
            excuse = excuse
        )
    }
}

data class GoalBackup(
    val goalId: String,
    val title: String,
    val description: String?,
    val targetDate: String?,
    val startDate: String?,
    val progress: Int?
) {
    companion object {
        fun fromEntity(entity: GoalEntity): GoalBackup {
            return GoalBackup(
                goalId = entity.goal_id.toString(),
                title = entity.title,
                description = entity.description,
                targetDate = entity.target_date?.toString(),
                startDate = entity.start_date?.toString(),
                progress = entity.progress
            )
        }
    }

    fun toEntity(): GoalEntity {
        return GoalEntity(
            goal_id = UUID.fromString(goalId),
            title = title,
            description = description,
            target_date = targetDate?.let { LocalDate.parse(it) },
            start_date = startDate?.let { LocalDate.parse(it) },
            progress = progress
        )
    }
}

data class SubtaskBackup(
    val subtaskId: String,
    val title: String,
    val isCompleted: Boolean,
    val habitProgressId: String
) {
    companion object {
        fun fromEntity(entity: SubtaskEntity): SubtaskBackup {
            return SubtaskBackup(
                subtaskId = entity.subtaskId.toString(),
                title = entity.title,
                isCompleted = entity.isCompleted,
                habitProgressId = entity.habitProgressId.toString()
            )
        }
    }

    fun toEntity(): SubtaskEntity {
        return SubtaskEntity(
            subtaskId = UUID.fromString(subtaskId),
            title = title,
            isCompleted = isCompleted,
            habitProgressId = UUID.fromString(habitProgressId)
        )
    }
}

data class ImageProgressBackup(
    val id: String,
    val habitId: String,
    val description: String,
    val date: String,
    val imagePath: String
) {
    companion object {
        fun fromEntity(entity: ImageProgressEntity): ImageProgressBackup {
            return ImageProgressBackup(
                id = entity.id.toString(),
                habitId = entity.habitId.toString(),
                description = entity.description,
                date = entity.date.toString(),
                imagePath = entity.imagePath
            )
        }
    }

    fun toEntity(): ImageProgressEntity {
        return ImageProgressEntity(
            id = UUID.fromString(id),
            habitId = UUID.fromString(habitId),
            description = description,
            date = LocalDate.parse(date),
            imagePath = imagePath
        )
    }
}

data class OneTimeTaskBackup(
    val taskId: String,
    val title: String,
    val isCompleted: Boolean,
    val date: String,
    val reminderTime: String?
) {
    companion object {
        fun fromEntity(entity: OneTimeTaskEntity): OneTimeTaskBackup {
            return OneTimeTaskBackup(
                taskId = entity.taskId.toString(),
                title = entity.title,
                isCompleted = entity.isCompleted,
                date = entity.date.toString(),
                reminderTime = entity.reminder_time?.toString()
            )
        }
    }

    fun toEntity(): OneTimeTaskEntity {
        return OneTimeTaskEntity(
            taskId = UUID.fromString(taskId),
            title = title,
            isCompleted = isCompleted,
            date = LocalDate.parse(date),
            reminder_time = reminderTime?.let { LocalTime.parse(it) }
        )
    }
}

/**
 * Info about a backup file in Downloads folder
 */
data class BackupFileInfo(
    val fileName: String,
    val displayName: String,
    val dateTime: String,
    val fileSizeBytes: Long,
    val fileSizeDisplay: String,
    val uri: String
)
