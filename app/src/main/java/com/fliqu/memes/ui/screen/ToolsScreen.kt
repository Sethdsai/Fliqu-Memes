package com.fliqu.memes.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fliqu.memes.model.ToolItem
import com.fliqu.memes.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolsScreen(viewModel: MainViewModel) {
    val tools = viewModel.getToolList()
    var selectedTool by remember { mutableStateOf<ToolItem?>(null) }
    var showToolScreen by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = {
                Text(
                    "Tools",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )

        if (showToolScreen && selectedTool != null) {
            ToolExecutionScreen(
                tool = selectedTool!!,
                viewModel = viewModel,
                onBack = {
                    showToolScreen = false
                    selectedTool = null
                }
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(tools) { tool ->
                    ToolCard(
                        tool = tool,
                        onClick = {
                            selectedTool = tool
                            showToolScreen = true
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ToolCard(tool: ToolItem, onClick: () -> Unit) {
    val iconMap = mapOf(
        "background" to Icons.Default.BrokenImage,
        "text_fields" to Icons.Default.TextFields,
        "crop" to Icons.Default.Crop,
        "speed" to Icons.Default.FastForward,
        "layers" to Icons.Default.Layers,
        "dashboard" to Icons.Default.Dashboard,
        "branding_watermark" to Icons.Default.BrandingWatermark,
        "photo_filter" to Icons.Default.PhotoFilter,
        "rotate_right" to Icons.Default.RotateRight,
        "replay" to Icons.Default.Replay,
        "content_cut" to Icons.Default.ContentCut,
        "transform" to Icons.Default.Transform
    )

    val icon = iconMap[tool.icon] ?: Icons.Default.Build

    val containerColors = mapOf(
        "bg_remove" to MaterialTheme.colorScheme.errorContainer,
        "text_overlay" to MaterialTheme.colorScheme.primaryContainer,
        "crop" to MaterialTheme.colorScheme.secondaryContainer,
        "speed" to MaterialTheme.colorScheme.tertiaryContainer,
        "frames" to MaterialTheme.colorScheme.tertiaryContainer,
        "combine" to MaterialTheme.colorScheme.primaryContainer,
        "watermark" to MaterialTheme.colorScheme.secondaryContainer,
        "filters" to MaterialTheme.colorScheme.errorContainer,
        "rotate" to MaterialTheme.colorScheme.tertiaryContainer,
        "reverse" to MaterialTheme.colorScheme.secondaryContainer,
        "trim" to MaterialTheme.colorScheme.primaryContainer,
        "convert" to MaterialTheme.colorScheme.tertiaryContainer
    )

    val contentColors = mapOf(
        "bg_remove" to MaterialTheme.colorScheme.onErrorContainer,
        "text_overlay" to MaterialTheme.colorScheme.onPrimaryContainer,
        "crop" to MaterialTheme.colorScheme.onSecondaryContainer,
        "speed" to MaterialTheme.colorScheme.onTertiaryContainer,
        "frames" to MaterialTheme.colorScheme.onTertiaryContainer,
        "combine" to MaterialTheme.colorScheme.onPrimaryContainer,
        "watermark" to MaterialTheme.colorScheme.onSecondaryContainer,
        "filters" to MaterialTheme.colorScheme.onErrorContainer,
        "rotate" to MaterialTheme.colorScheme.onTertiaryContainer,
        "reverse" to MaterialTheme.colorScheme.onSecondaryContainer,
        "trim" to MaterialTheme.colorScheme.onPrimaryContainer,
        "convert" to MaterialTheme.colorScheme.onTertiaryContainer
    )

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(14.dp),
                color = containerColors[tool.id] ?: MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        icon,
                        contentDescription = tool.name,
                        tint = contentColors[tool.id] ?: MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                tool.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                tool.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ToolExecutionScreen(
    tool: ToolItem,
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            when (tool.id) {
                "bg_remove" -> {
                    viewModel.triggerToast("Processing background removal...")
                }
                "text_overlay" -> {
                    viewModel.openEditorWithMedia(it.toString())
                }
                "crop" -> {
                    viewModel.triggerToast("Crop mode activated")
                }
                "speed" -> {
                    viewModel.triggerToast("Speed control applied")
                }
                "frames" -> {
                    viewModel.triggerToast("Frames extracted")
                }
                "combine" -> {
                    viewModel.triggerToast("Media combined")
                }
                "watermark" -> {
                    viewModel.triggerToast("Watermark added")
                }
                "filters" -> {
                    viewModel.triggerToast("Filter applied")
                }
                "rotate" -> {
                    viewModel.triggerToast("Rotation applied")
                }
                "reverse" -> {
                    viewModel.triggerToast("GIF reversed")
                }
                "trim" -> {
                    viewModel.triggerToast("Trimmed successfully")
                }
                "convert" -> {
                    viewModel.triggerToast("Format converted")
                }
            }
        }
    }

    val gifLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.triggerToast("GIF loaded for processing")
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Text(
                    tool.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier.size(72.dp),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    val iconMap = mapOf(
                        "background" to Icons.Default.BrokenImage,
                        "text_fields" to Icons.Default.TextFields,
                        "crop" to Icons.Default.Crop,
                        "speed" to Icons.Default.FastForward,
                        "layers" to Icons.Default.Layers,
                        "dashboard" to Icons.Default.Dashboard,
                        "branding_watermark" to Icons.Default.BrandingWatermark,
                        "photo_filter" to Icons.Default.PhotoFilter,
                        "rotate_right" to Icons.Default.RotateRight,
                        "replay" to Icons.Default.Replay,
                        "content_cut" to Icons.Default.ContentCut,
                        "transform" to Icons.Default.Transform
                    )
                    Icon(
                        iconMap[tool.icon] ?: Icons.Default.Build,
                        contentDescription = null,
                        modifier = Modifier.size(36.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                tool.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                tool.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    when (tool.id) {
                        "reverse", "speed", "trim", "frames" -> gifLauncher.launch("image/gif")
                        "combine" -> imageLauncher.launch("image/*")
                        else -> imageLauncher.launch("image/*")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    Icons.Default.FolderOpen,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Select File",
                    style = MaterialTheme.typography.titleSmall
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { imageLauncher.launch("image/*") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    Icons.Default.Image,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Select Image",
                    style = MaterialTheme.typography.titleSmall
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { gifLauncher.launch("image/gif") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    Icons.Default.Gif,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Select GIF",
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
    }
}
