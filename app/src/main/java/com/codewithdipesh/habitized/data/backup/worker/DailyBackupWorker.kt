package com.codewithdipesh.habitized.data.backup.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.codewithdipesh.habitized.data.backup.manager.BackupManager
import com.codewithdipesh.habitized.data.backup.manager.BackupResult
import com.codewithdipesh.habitized.data.sharedPref.HabitPreference
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class DailyBackupWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val backupManager: BackupManager,
    private val habitPreference: HabitPreference
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val WORK_NAME = "daily_backup_worker"
        private const val REPEAT_INTERVAL_HOURS = 24L


        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build()

            val workRequest = PeriodicWorkRequestBuilder<DailyBackupWorker>(
                REPEAT_INTERVAL_HOURS,
                TimeUnit.HOURS
            )
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }


        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }

    override suspend fun doWork(): Result {
        if (!habitPreference.isAutoBackupEnabled()) {
            return Result.success()
        }

        return when (val result = backupManager.createBackup(backupType = "automatic")) {
            is BackupResult.Success -> {
                Result.success()
            }
            is BackupResult.Error -> {
                if (runAttemptCount < 3) {
                    Result.retry()
                } else {
                    Result.failure()
                }
            }
        }
    }
}
