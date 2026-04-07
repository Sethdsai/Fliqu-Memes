package com.fliqu.memes.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fliqu.memes.ui.theme.DarkBg
import com.fliqu.memes.ui.theme.DarkSurface
import com.fliqu.memes.ui.theme.DarkSurfaceVariant
import com.fliqu.memes.ui.theme.RedAccent
import com.fliqu.memes.ui.theme.TealAccent
import com.fliqu.memes.ui.theme.TextPrimary
import com.fliqu.memes.ui.theme.TextSecondary
import com.fliqu.memes.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: MainViewModel) {
    val savedMemes by viewModel.savedMemes.collectAsState()
    var showClearDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(DarkBg)) {
        TopAppBar(
            title = {
                Text(
                    text = "Settings",
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = DarkSurface
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = TealAccent.copy(alpha = 0.15f),
                        modifier = Modifier.size(48.dp)
                    ) {
                        androidx.compose.foundation.layout.Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "F",
                                color = TealAccent,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Fliqu Memes",
                            color = TextPrimary,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Version 1.0.0",
                            color = TextSecondary,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            SettingsSectionTitle(title = "Appearance")

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = DarkSurface
            ) {
                Column {
                    SettingsRow(
                        icon = Icons.Default.DarkMode,
                        title = "Dark Theme",
                        subtitle = "Always enabled for comfortable viewing"
                    )
                }
            }

            SettingsSectionTitle(title = "Storage")

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = DarkSurface
            ) {
                Column {
                    SettingsRow(
                        icon = Icons.Default.Verified,
                        title = "Saved Memes",
                        subtitle = "${savedMemes.size} meme(s) saved"
                    )
                    Divider(color = DarkSurfaceVariant, thickness = 1.dp)
                    SettingsRow(
                        icon = Icons.Default.Storage,
                        title = "Cache",
                        subtitle = "Temporary files and thumbnails"
                    )
                    Divider(color = DarkSurfaceVariant, thickness = 1.dp)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showClearDialog = true }
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Clear Data",
                            tint = RedAccent,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Clear All Data",
                                color = RedAccent,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Remove all saved memes and cache",
                                color = TextSecondary,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            SettingsSectionTitle(title = "About")

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = DarkSurface
            ) {
                Column {
                    SettingsRow(
                        icon = Icons.Default.Code,
                        title = "GitHub",
                        subtitle = "View source code and contribute"
                    )
                    Divider(color = DarkSurfaceVariant, thickness = 1.dp)
                    SettingsRow(
                        icon = Icons.Default.Info,
                        title = "Version",
                        subtitle = "1.0.0 (Build 1)"
                    )
                    Divider(color = DarkSurfaceVariant, thickness = 1.dp)
                    SettingsRow(
                        icon = Icons.Default.Verified,
                        title = "Licenses",
                        subtitle = "Open source licenses"
                    )
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(16.dp),
            title = {
                Text(
                    text = "Clear All Data?",
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            },
            text = {
                Text(
                    text = "This will permanently delete all your saved memes and cached data. This action cannot be undone.",
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.savedMemes.value = emptyList()
                    viewModel.uploadedMedia.value = emptyList()
                    showClearDialog = false
                    viewModel.triggerToast("All data cleared")
                }) {
                    Text("Clear", color = RedAccent)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
private fun SettingsSectionTitle(title: String) {
    Text(
        text = title,
        color = TealAccent,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
    )
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    showToggle: Boolean = false,
    toggleState: Boolean = false,
    onToggle: (() -> Unit)? = null,
    showChevron: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = TealAccent,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                color = TextPrimary,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                color = TextSecondary,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        if (showToggle && onToggle != null) {
            Switch(
                checked = toggleState,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedTrackColor = TealAccent,
                    uncheckedTrackColor = DarkSurfaceVariant,
                    checkedThumbColor = Color.White
                )
            )
        } else if (showChevron) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Navigate",
                tint = TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
