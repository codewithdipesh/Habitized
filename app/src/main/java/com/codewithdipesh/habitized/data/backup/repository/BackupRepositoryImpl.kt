package com.codewithdipesh.habitized.data.backup.repository

import android.net.Uri
import com.codewithdipesh.habitized.data.backup.manager.BackupManager
import com.codewithdipesh.habitized.data.backup.manager.BackupResult
import com.codewithdipesh.habitized.data.backup.model.BackupFileInfo
import com.codewithdipesh.habitized.data.sharedPref.HabitPreference
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackupRepositoryImpl @Inject constructor(
    private val backupManager: BackupManager,
    private val habitPreference: HabitPreference
) : BackupRepository {

    override suspend fun createBackup(backupType: String): BackupResult {
        return backupManager.createBackup(backupType)
    }

    override suspend fun restoreBackup(uri: Uri): BackupResult {
        return backupManager.restoreBackup(uri)
    }

    override suspend fun listBackups(): List<BackupFileInfo> {
        return backupManager.listAvailableBackups()
    }

    override suspend fun deleteBackup(uri: Uri): BackupResult {
        return backupManager.deleteBackup(uri)
    }

    override suspend fun getBackupSummary(uri: Uri): Map<String, Int>? {
        return backupManager.getBackupSummary(uri)
    }

    override fun getLastBackupDate(): String? {
        return habitPreference.getLastBackupDate()
    }

    override fun isAutoBackupEnabled(): Boolean {
        return habitPreference.isAutoBackupEnabled()
    }

    override fun setAutoBackupEnabled(enabled: Boolean) {
        habitPreference.setAutoBackupEnabled(enabled)
    }
}
