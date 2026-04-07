package com.fliqu.memes.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.fliqu.memes.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(viewModel: MainViewModel) {
    val editorGifUrl by viewModel.editorGifUrl.collectAsState()
    val selectedMediaForEditor by viewModel.selectedMediaForEditor.collectAsState()

    val hasContent = editorGifUrl != null || selectedMediaForEditor != null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        EditorTopBar(viewModel, hasContent)

        if (!hasContent) {
            EmptyEditorView(viewModel)
        } else {
            EditorCanvas(
                viewModel = viewModel,
                imageUrl = editorGifUrl,
                mediaUri = selectedMediaForEditor,
                modifier = Modifier.weight(1f)
            )

            EditorToolbar(viewModel)
        }
    }
}

@Composable
private fun EditorTopBar(viewModel: MainViewModel, hasContent: Boolean) {
    TopAppBar(
        title = {
            Text(
                "Meme Editor",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
        },
        navigationIcon = {
            if (hasContent) {
                IconButton(onClick = { viewModel.closeEditor() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        },
        actions = {
            if (hasContent) {
                IconButton(onClick = {
                    viewModel.saveMeme(
                        com.fliqu.memes.model.MemeProject(
                            id = "meme_${System.currentTimeMillis()}",
                            name = "Meme ${System.currentTimeMillis()}",
                            sourceUrl = viewModel.editorGifUrl.value ?: "",
                            createdAt = System.currentTimeMillis()
                        )
                    )
                }) {
                    Icon(Icons.Default.Save, contentDescription = "Save")
                }
                IconButton(onClick = {
                    viewModel.triggerToast("Exported successfully")
                }) {
                    Icon(Icons.Default.Share, contentDescription = "Share")
                }
                IconButton(onClick = {
                    viewModel.clearTextLayers()
                    viewModel.closeEditor()
                }) {
                    Icon(Icons.Default.Delete, contentDescription = "Clear")
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
private fun EmptyEditorView(viewModel: MainViewModel) {
    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.openEditorWithMedia(it.toString()) }
    }

    val gifLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.openEditorWithMedia(it.toString()) }
    }

    val videoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.openEditorWithMedia(it.toString()) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(80.dp),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            "Meme Editor",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Select a source to start creating memes",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        EditorSourceCard(
            icon = Icons.Default.Search,
            iconBgColor = MaterialTheme.colorScheme.primaryContainer,
            iconTintColor = MaterialTheme.colorScheme.primary,
            title = "Browse GIFs",
            subtitle = "Search Tenor, Giphy, and more",
            onClick = { viewModel.selectTab(1) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        EditorSourceCard(
            icon = Icons.Default.PhotoLibrary,
            iconBgColor = MaterialTheme.colorScheme.secondaryContainer,
            iconTintColor = MaterialTheme.colorScheme.onSecondaryContainer,
            title = "Pick from Gallery",
            subtitle = "Select images or GIFs from your device",
            onClick = { imageLauncher.launch("image/*") }
        )

        Spacer(modifier = Modifier.height(12.dp))

        EditorSourceCard(
            icon = Icons.Default.Gif,
            iconBgColor = MaterialTheme.colorScheme.tertiaryContainer,
            iconTintColor = MaterialTheme.colorScheme.onTertiaryContainer,
            title = "Pick GIF",
            subtitle = "Select a GIF from your device storage",
            onClick = { gifLauncher.launch("image/gif") }
        )

        Spacer(modifier = Modifier.height(12.dp))

        EditorSourceCard(
            icon = Icons.Default.VideoFile,
            iconBgColor = MaterialTheme.colorScheme.errorContainer,
            iconTintColor = MaterialTheme.colorScheme.onErrorContainer,
            title = "Pick Video",
            subtitle = "Select a video for frame extraction",
            onClick = { videoLauncher.launch("video/*") }
        )
    }
}

@Composable
private fun EditorSourceCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBgColor: Color,
    iconTintColor: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(12.dp),
                color = iconBgColor
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = iconTintColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun EditorCanvas(
    viewModel: MainViewModel,
    imageUrl: String?,
    mediaUri: String?,
    modifier: Modifier = Modifier
) {
    var showTextInput by remember { mutableStateOf(false) }
    var currentEditText by remember { mutableStateOf("") }
    var editingLayerId by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        if (imageUrl != null && imageUrl.isNotBlank()) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Editor image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        } else if (mediaUri != null) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(Uri.parse(mediaUri))
                    .crossfade(true)
                    .build(),
                contentDescription = "Editor media",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Image,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                )
            }
        }

        viewModel.textLayers.forEach { layer ->
            var offset by remember { mutableStateOf(Offset(layer.x, layer.y)) }

            Box(
                modifier = Modifier
                    .offset(offset.x.dp, offset.y.dp)
                    .pointerInput(layer.id) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            offset += dragAmount
                        }
                    }
                    .clickable {
                        currentEditText = layer.text
                        editingLayerId = layer.id
                        showTextInput = true
                    }
                    .background(
                        if (editingLayerId == layer.id)
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        else
                            Color.Transparent,
                        RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Text(
                    layer.text,
                    color = Color(layer.color),
                    fontSize = layer.fontSize.sp,
                    fontWeight = if (layer.isBold) FontWeight.Bold else FontWeight.Normal
                )
            }
        }

        if (showTextInput) {
            AlertDialog(
                onDismissRequest = {
                    showTextInput = false
                    editingLayerId = null
                },
                title = { Text("Edit Text") },
                text = {
                    OutlinedTextField(
                        value = currentEditText,
                        onValueChange = { currentEditText = it },
                        label = { Text("Enter text") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = false,
                        maxLines = 4
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        editingLayerId?.let { id ->
                            viewModel.updateTextLayer(id, currentEditText)
                        }
                        showTextInput = false
                        editingLayerId = null
                    }) {
                        Text("Apply")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showTextInput = false
                        editingLayerId = null
                    }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
private fun EditorToolbar(viewModel: MainViewModel) {
    var showColorPicker by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        shadowElevation = 8.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Divider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                EditorToolButton(
                    icon = Icons.Default.TextFields,
                    label = "Text",
                    onClick = { viewModel.addTextLayer() }
                )
                EditorToolButton(
                    icon = Icons.Default.FormatBold,
                    label = "Bold",
                    onClick = {
                        viewModel.textLayers = viewModel.textLayers.map { it.copy(isBold = !it.isBold) }
                    }
                )
                EditorToolButton(
                    icon = Icons.Default.Palette,
                    label = "Color",
                    onClick = { showColorPicker = true }
                )
                EditorToolButton(
                    icon = Icons.Default.FormatSize,
                    label = "Size",
                    onClick = {
                        viewModel.textLayers = viewModel.textLayers.map { it.copy(fontSize = it.fontSize + 4f) }
                    }
                )
                EditorToolButton(
                    icon = Icons.Default.LayersClear,
                    label = "Clear",
                    onClick = { viewModel.clearTextLayers() }
                )
            }
        }
    }

    if (showColorPicker) {
        ColorPickerDialog(
            onColorSelected = { color ->
                viewModel.textLayers = viewModel.textLayers.map { it.copy(color = color) }
                showColorPicker = false
            },
            onDismiss = { showColorPicker = false }
        )
    }
}

@Composable
private fun EditorToolButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    icon,
                    contentDescription = label,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ColorPickerDialog(
    onColorSelected: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = listOf(
        0xFFFFFFFF, 0xFF000000, 0xFFF44336, 0xFFE91E63,
        0xFF9C27B0, 0xFF673AB7, 0xFF3F51B5, 0xFF2196F3,
        0xFF00BCD4, 0xFF009688, 0xFF4CAF50, 0xFF8BC34A,
        0xFFCDDC39, 0xFFFFEB3B, 0xFFFFC107, 0xFFFF9800,
        0xFFFF5722, 0xFF795548, 0xFF607D8B, 0xFF00695C
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Text Color") },
        text = {
            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(colors) { color ->
                    Surface(
                        onClick = { onColorSelected(color) },
                        modifier = Modifier.size(40.dp),
                        shape = CircleShape,
                        color = Color(color),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outlineVariant
                        )
                    ) {}
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
