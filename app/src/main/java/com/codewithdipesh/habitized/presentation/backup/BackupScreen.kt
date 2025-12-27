package com.codewithdipesh.habitized.presentation.backup

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.codewithdipesh.habitized.R
import com.codewithdipesh.habitized.data.backup.model.BackupFileInfo
import com.codewithdipesh.habitized.presentation.addscreen.component.AddScreenTopBar
import com.codewithdipesh.habitized.ui.theme.instrumentSerif
import com.codewithdipesh.habitized.ui.theme.regular

@Composable
fun BackupScreen(
    navController: NavController,
    viewModel: BackupViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { viewModel.restoreFromUri(it) }
    }

    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessage()
        }
    }

    // Restore confirmation dialog
    if (uiState.showRestoreDialog && uiState.selectedBackupForRestore != null) {
        RestoreConfirmationDialog(
            backup = uiState.selectedBackupForRestore!!,
            summary = uiState.backupSummary,
            onConfirm = { viewModel.restoreBackup(uiState.selectedBackupForRestore!!) },
            onDismiss = { viewModel.hideRestoreDialog() }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AddScreenTopBar(
                isShowingLeftIcon = true,
                leftIcon = {
                    IconButton(
                        onClick = { navController.navigateUp() },
                        modifier = Modifier.padding(top = 30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary.copy(0.3f),
                            MaterialTheme.colorScheme.primary.copy(0.2f),
                            MaterialTheme.colorScheme.primary.copy(0.1f),
                            Color.Transparent,
                            Color.Transparent,
                            Color.Transparent,
                        )
                    )
                )
                .padding(innerPadding)
        ) {
            if (uiState.isLoading || uiState.isRestoring) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    // Title
                    Text(
                        text = "Backup & Restore",
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontFamily = instrumentSerif,
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Italic,
                            fontSize = 28.sp
                        )
                    )
                    Text(
                        text = "Keep your data safe",
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.onPrimary.copy(0.5f),
                            fontFamily = regular,
                            fontWeight = FontWeight.Light,
                            fontSize = 14.sp
                        )
                    )
                    Spacer(Modifier.height(16.dp))
                }

                // Last backup info
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceBright
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.backup_icon),
                                    contentDescription = "backup",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Last Backup",
                                        style = TextStyle(
                                            color = MaterialTheme.colorScheme.onPrimary.copy(0.6f),
                                            fontFamily = regular,
                                            fontSize = 12.sp
                                        )
                                    )
                                    Text(
                                        text = uiState.lastBackupDate ?: "Never",
                                        style = TextStyle(
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            fontFamily = regular,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 16.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // Create backup button
                item {
                    Button(
                        onClick = { viewModel.createBackup() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(16.dp),
                        enabled = !uiState.isLoading && !uiState.isRestoring
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.backup_icon),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Create Backup Now",
                            style = TextStyle(
                                fontFamily = regular,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            )
                        )
                    }
                }

                // Auto-backup toggle
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceBright
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Auto-backup (Daily)",
                                    style = TextStyle(
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontFamily = regular,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 16.sp
                                    )
                                )
                                Text(
                                    text = "Automatically backup when battery is not low",
                                    style = TextStyle(
                                        color = MaterialTheme.colorScheme.onPrimary.copy(0.6f),
                                        fontFamily = regular,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                            Switch(
                                checked = uiState.isAutoBackupEnabled,
                                onCheckedChange = { viewModel.toggleAutoBackup(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                                    checkedTrackColor = MaterialTheme.colorScheme.primary.copy(0.3f)
                                )
                            )
                        }
                    }
                }

                // Available backups section
                item {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Available Backups",
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontFamily = regular,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp
                        )
                    )
                    Text(
                        text = "Stored in Downloads folder",
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.onPrimary.copy(0.5f),
                            fontFamily = regular,
                            fontSize = 12.sp
                        )
                    )
                    Spacer(Modifier.height(8.dp))
                }

                if (uiState.backups.isEmpty() && !uiState.isLoading) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceBright
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = "No backups found",
                                modifier = Modifier.padding(24.dp),
                                style = TextStyle(
                                    color = MaterialTheme.colorScheme.onPrimary.copy(0.6f),
                                    fontFamily = regular,
                                    fontSize = 14.sp
                                )
                            )
                        }
                    }
                }

                items(uiState.backups) { backup ->
                    BackupItem(
                        backup = backup,
                        onRestore = { viewModel.showRestoreDialog(backup) },
                        onDelete = { viewModel.deleteBackup(backup) }
                    )
                }

                // Import from file button
                item {
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = {
                            filePickerLauncher.launch(arrayOf("application/json"))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        enabled = !uiState.isLoading && !uiState.isRestoring
                    ) {
                        Text(
                            text = "Import from file...",
                            style = TextStyle(
                                fontFamily = regular,
                                fontSize = 14.sp
                            )
                        )
                    }
                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun BackupItem(
    backup: BackupFileInfo,
    onRestore: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceBright
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = backup.displayName,
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontFamily = regular,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "${backup.dateTime} - ${backup.fileSizeDisplay}",
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.onPrimary.copy(0.6f),
                            fontFamily = regular,
                            fontSize = 12.sp
                        )
                    )
                }
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error.copy(0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(0.1f))
                    .clickable { onRestore() }
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Restore",
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontFamily = regular,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun RestoreConfirmationDialog(
    backup: BackupFileInfo,
    summary: Map<String, Int>?,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        title = {
            Text(
                text = "Restore Backup?",
                style = TextStyle(
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontFamily = regular,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            )
        },
        text = {
            Column {
                Text(
                    text = "Backup from ${backup.dateTime}",
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontFamily = regular,
                        fontSize = 14.sp
                    )
                )
                Spacer(Modifier.height(12.dp))

                if (summary != null) {
                    Text(
                        text = "This backup contains:",
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.onPrimary.copy(0.7f),
                            fontFamily = regular,
                            fontSize = 13.sp
                        )
                    )
                    Spacer(Modifier.height(8.dp))
                    summary["habits"]?.let {
                        Text(
                            text = "  - $it habits",
                            style = TextStyle(
                                color = MaterialTheme.colorScheme.onPrimary.copy(0.7f),
                                fontFamily = regular,
                                fontSize = 13.sp
                            )
                        )
                    }
                    summary["goals"]?.let {
                        Text(
                            text = "  - $it goals",
                            style = TextStyle(
                                color = MaterialTheme.colorScheme.onPrimary.copy(0.7f),
                                fontFamily = regular,
                                fontSize = 13.sp
                            )
                        )
                    }
                    summary["progress"]?.let {
                        Text(
                            text = "  - $it progress entries",
                            style = TextStyle(
                                color = MaterialTheme.colorScheme.onPrimary.copy(0.7f),
                                fontFamily = regular,
                                fontSize = 13.sp
                            )
                        )
                    }
                    summary["tasks"]?.let {
                        Text(
                            text = "  - $it tasks",
                            style = TextStyle(
                                color = MaterialTheme.colorScheme.onPrimary.copy(0.7f),
                                fontFamily = regular,
                                fontSize = 13.sp
                            )
                        )
                    }
                    summary["images"]?.let {
                        Text(
                            text = "  - $it images",
                            style = TextStyle(
                                color = MaterialTheme.colorScheme.onPrimary.copy(0.7f),
                                fontFamily = regular,
                                fontSize = 13.sp
                            )
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                }

                Text(
                    text = "Warning: All current data will be replaced with the backup data.",
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.error,
                        fontFamily = regular,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Restore")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
