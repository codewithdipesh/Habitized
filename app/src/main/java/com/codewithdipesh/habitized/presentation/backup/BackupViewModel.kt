package com.codewithdipesh.habitized.presentation.backup

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codewithdipesh.habitized.data.backup.manager.BackupResult
import com.codewithdipesh.habitized.data.backup.model.BackupFileInfo
import com.codewithdipesh.habitized.data.backup.repository.BackupRepository
import com.codewithdipesh.habitized.data.backup.worker.DailyBackupWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class BackupUiState(
    val isLoading: Boolean = false,
    val backups: List<BackupFileInfo> = emptyList(),
    val lastBackupDate: String? = null,
    val isAutoBackupEnabled: Boolean = false,
    val message: String? = null,
    val showRestoreDialog: Boolean = false,
    val selectedBackupForRestore: BackupFileInfo? = null,
    val backupSummary: Map<String, Int>? = null,
    val isRestoring: Boolean = false
)

@HiltViewModel
class BackupViewModel @Inject constructor(
    private val backupRepository: BackupRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(BackupUiState())
    val uiState: StateFlow<BackupUiState> = _uiState.asStateFlow()

    init {
        loadBackupInfo()
    }

    fun loadBackupInfo() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val backups = backupRepository.listBackups()
            val lastBackupDate = backupRepository.getLastBackupDate()?.let { formatBackupDate(it) }
            val isAutoBackupEnabled = backupRepository.isAutoBackupEnabled()

            _uiState.update {
                it.copy(
                    isLoading = false,
                    backups = backups,
                    lastBackupDate = lastBackupDate,
                    isAutoBackupEnabled = isAutoBackupEnabled
                )
            }
        }
    }

    fun createBackup() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            when (val result = backupRepository.createBackup("manual")) {
                is BackupResult.Success -> {
                    loadBackupInfo()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            message = "Backup created successfully"
                        )
                    }
                }
                is BackupResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            message = result.message
                        )
                    }
                }
            }
        }
    }

    fun showRestoreDialog(backup: BackupFileInfo) {
        viewModelScope.launch {
            val uri = Uri.parse(backup.uri)
            val summary = backupRepository.getBackupSummary(uri)

            _uiState.update {
                it.copy(
                    showRestoreDialog = true,
                    selectedBackupForRestore = backup,
                    backupSummary = summary
                )
            }
        }
    }

    fun hideRestoreDialog() {
        _uiState.update {
            it.copy(
                showRestoreDialog = false,
                selectedBackupForRestore = null,
                backupSummary = null
            )
        }
    }

    fun restoreBackup(backup: BackupFileInfo) {
        viewModelScope.launch {
            _uiState.update { it.copy(isRestoring = true, showRestoreDialog = false) }

            val uri = Uri.parse(backup.uri)
            when (val result = backupRepository.restoreBackup(uri)) {
                is BackupResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isRestoring = false,
                            message = "Data restored successfully",
                            selectedBackupForRestore = null,
                            backupSummary = null
                        )
                    }
                }
                is BackupResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isRestoring = false,
                            message = result.message,
                            selectedBackupForRestore = null,
                            backupSummary = null
                        )
                    }
                }
            }
        }
    }

    fun restoreFromUri(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isRestoring = true) }

            when (val result = backupRepository.restoreBackup(uri)) {
                is BackupResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isRestoring = false,
                            message = "Data restored successfully"
                        )
                    }
                }
                is BackupResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isRestoring = false,
                            message = result.message
                        )
                    }
                }
            }
        }
    }

    fun deleteBackup(backup: BackupFileInfo) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val uri = Uri.parse(backup.uri)
            when (val result = backupRepository.deleteBackup(uri)) {
                is BackupResult.Success -> {
                    loadBackupInfo()
                    _uiState.update {
                        it.copy(
                            message = "Backup deleted"
                        )
                    }
                }
                is BackupResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            message = result.message
                        )
                    }
                }
            }
        }
    }

    fun toggleAutoBackup(enabled: Boolean) {
        backupRepository.setAutoBackupEnabled(enabled)
        _uiState.update { it.copy(isAutoBackupEnabled = enabled) }

        if (enabled) {
            DailyBackupWorker.schedule(context)
        } else {
            DailyBackupWorker.cancel(context)
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }

    private fun formatBackupDate(isoDate: String): String {
        return try {
            val dateTime = LocalDateTime.parse(isoDate)
            dateTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))
        } catch (e: Exception) {
            isoDate
        }
    }
}
