package com.fliqu.memes.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BrowseGallery
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Trash
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.fliqu.memes.model.MemeProject
import com.fliqu.memes.model.TextLayer
import com.fliqu.memes.ui.theme.DarkBg
import com.fliqu.memes.ui.theme.DarkSurface
import com.fliqu.memes.ui.theme.DarkSurfaceVariant
import com.fliqu.memes.ui.theme.RedAccent
import com.fliqu.memes.ui.theme.TealAccent
import com.fliqu.memes.ui.theme.TextPrimary
import com.fliqu.memes.ui.theme.TextSecondary
import com.fliqu.memes.viewmodel.MainViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(viewModel: MainViewModel) {
    val editorGifUrl by viewModel.editorGifUrl.collectAsState()
    val selectedMediaForEditor by viewModel.selectedMediaForEditor.collectAsState()
    val textLayers = viewModel.textLayers
    val context = LocalContext.current
    var showColorPicker by remember { mutableStateOf(false) }
    var editingLayer by remember { mutableStateOf<TextLayer?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }

    val imageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { viewModel.openEditorWithMedia(it.toString()) }
    }
    val gifLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { viewModel.openEditorWithMedia(it.toString()) }
    }
    val videoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { viewModel.openEditorWithMedia(it.toString()) }
    }

    val hasContent = editorGifUrl != null || selectedMediaForEditor != null

    Column(modifier = Modifier.fillMaxSize().background(DarkBg)) {
        TopAppBar(
            title = {
                Text(
                    text = "Meme Editor",
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            },
            navigationIcon = {
                if (hasContent) {
                    IconButton(onClick = { viewModel.closeEditor() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                }
            },
            actions = {
                if (hasContent) {
                    IconButton(onClick = {
                        val source = editorGifUrl ?: selectedMediaForEditor ?: ""
                        viewModel.saveMeme(
                            MemeProject(
                                id = UUID.randomUUID().toString(),
                                name = "Meme_${System.currentTimeMillis()}",
                                sourceUrl = source,
                                createdAt = System.currentTimeMillis()
                            )
                        )
                        viewModel.triggerToast("Meme saved!")
                    }) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Save",
                            tint = TealAccent
                        )
                    }
                    IconButton(onClick = {
                        viewModel.triggerToast("Exported successfully")
                    }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = TealAccent
                        )
                    }
                    IconButton(onClick = { viewModel.closeEditor() }) {
                        Icon(
                            imageVector = Icons.Default.Trash,
                            contentDescription = "Delete",
                            tint = RedAccent
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
        )

        if (!hasContent) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Start Creating",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Choose a source to begin editing",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(24.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SourceOptionCard(
                            icon = Icons.Default.Search,
                            label = "Browse GIFs",
                            description = "Search from GIF libraries",
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.selectedTab.value = 1 }
                        )
                        SourceOptionCard(
                            icon = Icons.Default.Image,
                            label = "Pick Image",
                            description = "Choose from gallery",
                            modifier = Modifier.weight(1f),
                            onClick = { imageLauncher.launch("image/*") }
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SourceOptionCard(
                            icon = Icons.Default.BrowseGallery,
                            label = "Pick GIF",
                            description = "Select a GIF file",
                            modifier = Modifier.weight(1f),
                            onClick = { gifLauncher.launch("image/gif") }
                        )
                        SourceOptionCard(
                            icon = Icons.Default.VideoFile,
                            label = "Pick Video",
                            description = "Select a video file",
                            modifier = Modifier.weight(1f),
                            onClick = { videoLauncher.launch("video/*") }
                        )
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(editorGifUrl ?: selectedMediaForEditor)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Editor Canvas",
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Fit
                )

                textLayers.forEach { layer ->
                    var totalDrag by remember(layer.id) { mutableFloatStateOf(0f) }
                    Text(
                        text = layer.text,
                        color = Color(layer.color),
                        fontSize = layer.fontSize.sp,
                        fontWeight = if (layer.isBold) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier
                            .offset(layer.x.dp, layer.y.dp)
                            .pointerInput(layer.id) {
                                detectDragGestures(
                                    onDragStart = { totalDrag = 0f },
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        totalDrag += dragAmount.getDistance()
                                        viewModel.updateTextLayer(
                                            layer.copy(
                                                x = layer.x + dragAmount.x,
                                                y = layer.y + dragAmount.y
                                            )
                                        )
                                    },
                                    onDragEnd = {
                                        if (totalDrag < 10f) {
                                            editingLayer = layer
                                            showEditDialog = true
                                        }
                                    },
                                    onDragCancel = { }
                                )
                            }
                    )
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = DarkSurface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ToolCircleButton(
                        icon = Icons.Default.TextFields,
                        label = "Text",
                        onClick = { viewModel.addTextLayer() }
                    )
                    ToolCircleButton(
                        icon = Icons.Default.FormatBold,
                        label = "Bold",
                        onClick = {
                            editingLayer?.let { layer ->
                                viewModel.updateTextLayer(layer.copy(isBold = !layer.isBold))
                            }
                        }
                    )
                    ToolCircleButton(
                        icon = Icons.Default.Palette,
                        label = "Color",
                        onClick = { showColorPicker = true }
                    )
                    ToolCircleButton(
                        icon = Icons.Default.FormatSize,
                        label = "Size",
                        onClick = {
                            editingLayer?.let { layer ->
                                viewModel.updateTextLayer(layer.copy(fontSize = layer.fontSize + 4f))
                            }
                        }
                    )
                    ToolCircleButton(
                        icon = Icons.Default.Trash,
                        label = "Clear",
                        onClick = { viewModel.clearTextLayers() }
                    )
                }
            }
        }
    }

    if (showColorPicker) {
        AlertDialog(
            onDismissRequest = { showColorPicker = false },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(16.dp),
            title = {
                Text(
                    text = "Pick Color",
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            },
            text = {
                val colors = listOf(
                    Color.White,
                    Color.Black,
                    Color.Red,
                    Color(0xFFFFA500),
                    Color.Yellow,
                    Color.Green,
                    Color.Blue,
                    Color.Cyan,
                    Color.Magenta,
                    Color(0xFFFF69B4),
                    Color(0xFF00BFA5),
                    Color(0xFFFF6B6B),
                    Color(0xFFFFA657),
                    Color(0xFFE3B341),
                    Color(0xFF3FB950),
                    Color(0xFF58A6FF),
                    Color(0xFFBC8CFF),
                    Color(0xFFF778BA),
                    Color.LightGray,
                    Color.DarkGray
                )
                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(colors) { color ->
                        Surface(
                            modifier = Modifier
                                .size(40.dp)
                                .border(1.dp, DarkSurfaceVariant, CircleShape),
                            shape = CircleShape,
                            color = color,
                            onClick = {
                                editingLayer?.let { layer ->
                                    viewModel.updateTextLayer(layer.copy(color = color.value.toLong()))
                                } ?: run {
                                    if (textLayers.isNotEmpty()) {
                                        viewModel.updateTextLayer(
                                            textLayers.last().copy(color = color.value.toLong())
                                        )
                                    }
                                }
                                showColorPicker = false
                            }
                        ) {}
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showColorPicker = false }) {
                    Text("Close", color = TealAccent)
                }
            }
        )
    }

    if (showEditDialog && editingLayer != null) {
        var editText by remember(editingLayer?.id) { mutableStateOf(editingLayer?.text ?: "") }
        AlertDialog(
            onDismissRequest = {
                showEditDialog = false
                editingLayer = null
            },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(16.dp),
            title = {
                Text(
                    text = "Edit Text",
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = editText,
                        onValueChange = { editText = it },
                        label = { Text("Text", color = TextSecondary) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TealAccent,
                            unfocusedBorderColor = DarkSurfaceVariant,
                            cursorColor = TealAccent,
                            textColor = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = {
                            editingLayer?.let { layer ->
                                viewModel.removeTextLayer(layer.id)
                            }
                            showEditDialog = false
                            editingLayer = null
                        }) {
                            Text("Delete", color = RedAccent)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    editingLayer?.let { layer ->
                        viewModel.updateTextLayer(layer.copy(text = editText))
                    }
                    showEditDialog = false
                    editingLayer = null
                }) {
                    Text("Apply", color = TealAccent)
                }
            }
        )
    }
}

@Composable
private fun SourceOptionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    description: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = DarkSurfaceVariant
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = TealAccent.copy(alpha = 0.15f),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = TealAccent,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                color = TextPrimary,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                color = TextSecondary,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ToolCircleButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            onClick = onClick,
            shape = CircleShape,
            color = DarkSurfaceVariant,
            modifier = Modifier.size(40.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = TealAccent,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 10.sp
        )
    }
}


