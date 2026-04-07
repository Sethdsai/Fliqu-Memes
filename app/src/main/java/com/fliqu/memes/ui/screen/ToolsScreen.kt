package com.fliqu.memes.ui.screen

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Filter
import androidx.compose.material.icons.filled.FilterFrames
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Transform
import androidx.compose.material.icons.filled.ViewColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.filled.BrandingWatermark
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.fliqu.memes.model.ToolItem
import com.fliqu.memes.ui.theme.BlueAccent
import com.fliqu.memes.ui.theme.DarkBg
import com.fliqu.memes.ui.theme.DarkSurface
import com.fliqu.memes.ui.theme.DarkSurfaceVariant
import com.fliqu.memes.ui.theme.GreenAccent
import com.fliqu.memes.ui.theme.OrangeAccent
import com.fliqu.memes.ui.theme.PinkAccent
import com.fliqu.memes.ui.theme.PurpleAccent
import com.fliqu.memes.ui.theme.RedAccent
import com.fliqu.memes.ui.theme.TealAccent
import com.fliqu.memes.ui.theme.TextPrimary
import com.fliqu.memes.ui.theme.TextSecondary
import com.fliqu.memes.ui.theme.YellowAccent
import com.fliqu.memes.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolsScreen(viewModel: MainViewModel) {
    var selectedTool by remember { mutableStateOf<ToolItem?>(null) }

    if (selectedTool != null) {
        ToolExecutionScreen(
            tool = selectedTool!!,
            viewModel = viewModel,
            onBack = { selectedTool = null }
        )
    } else {
        Column(modifier = Modifier.fillMaxSize().background(DarkBg)) {
            TopAppBar(
                title = {
                    Text(
                        text = "Tools",
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
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                viewModel.getToolList().chunked(2).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        row.forEach { tool ->
                            ToolCard(
                                tool = tool,
                                onClick = {
                                    if (tool.id == "text_overlay") {
                                        viewModel.selectedTab.value = 2
                                        viewModel.triggerToast("Text overlay available in Editor")
                                    } else {
                                        selectedTool = tool
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (row.size < 2) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun ToolCard(
    tool: ToolItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val iconColor = when (tool.id) {
        "bg_remove" -> PurpleAccent
        "text_overlay" -> TealAccent
        "crop_resize" -> GreenAccent
        "rotate_flip" -> BlueAccent
        "watermark" -> OrangeAccent
        "filters" -> PinkAccent
        "combine" -> YellowAccent
        "convert" -> BlueAccent
        "speed" -> RedAccent
        "frames" -> PurpleAccent
        "reverse" -> OrangeAccent
        "trim" -> GreenAccent
        else -> TealAccent
    }

    val iconVector = when (tool.id) {
        "bg_remove" -> Icons.Default.ImageSearch
        "text_overlay" -> Icons.Default.TextFields
        "crop_resize" -> Icons.Default.Crop
        "rotate_flip" -> Icons.Default.RotateRight
        "watermark" -> Icons.Default.BrandingWatermark
        "filters" -> Icons.Default.Filter
        "combine" -> Icons.Default.ViewColumn
        "convert" -> Icons.Default.Transform
        "speed" -> Icons.Default.Speed
        "frames" -> Icons.Default.FilterFrames
        "reverse" -> Icons.Default.FastRewind
        "trim" -> Icons.Default.ContentCut
        else -> Icons.Default.Filter
    }

    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = DarkSurfaceVariant
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = iconColor.copy(alpha = 0.15f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = iconVector,
                        contentDescription = tool.name,
                        tint = iconColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = tool.name,
                color = TextPrimary,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = tool.description,
                color = TextSecondary,
                style = MaterialTheme.typography.labelSmall,
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
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isProcessing by viewModel.isProcessing.collectAsState()
    val processedBitmap = viewModel.processedBitmap

    val iconColor = when (tool.id) {
        "bg_remove" -> PurpleAccent
        "crop_resize" -> GreenAccent
        "rotate_flip" -> BlueAccent
        "watermark" -> OrangeAccent
        "filters" -> PinkAccent
        "combine" -> YellowAccent
        else -> TealAccent
    }

    val imageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            scope.launch {
                val bitmap = withContext(Dispatchers.IO) {
                    context.contentResolver.openInputStream(it)?.use { stream ->
                        BitmapFactory.decodeStream(stream)
                    }
                }
                bitmap?.let { bm ->
                    viewModel.processImage(bm, tool.id)
                }
            }
        }
    }

    var showWatermarkDialog by remember { mutableStateOf(false) }
    var watermarkText by remember { mutableStateOf("Fliqu Memes") }

    val selectFileLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            if (tool.id == "watermark") {
                showWatermarkDialog = true
            }
            scope.launch {
                val bitmap = withContext(Dispatchers.IO) {
                    context.contentResolver.openInputStream(it)?.use { stream ->
                        BitmapFactory.decodeStream(stream)
                    }
                }
                bitmap?.let { bm ->
                    viewModel.processImage(bm, tool.id)
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(DarkBg)) {
        TopAppBar(
            title = {
                Text(
                    text = tool.name,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
        )

        if (isProcessing) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = TealAccent)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Processing image...",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    LinearProgressIndicator(
                        progress = 0.7f,
                        color = TealAccent,
                        trackColor = DarkSurfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp)
                            .height(4.dp)
                    )
                }
            }
        } else if (processedBitmap != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(processedBitmap)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Processed Result",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(16.dp))

                FilledTonalButton(
                    onClick = {
                        scope.launch(Dispatchers.IO) {
                            try {
                                val contentValues = ContentValues().apply {
                                    put(MediaStore.Images.Media.DISPLAY_NAME, "fliqu_${System.currentTimeMillis()}.png")
                                    put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                        put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/FliquMemes")
                                    }
                                }
                                val resolver = context.contentResolver
                                val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                                imageUri?.let { uri ->
                                    resolver.openOutputStream(uri)?.use { stream ->
                                        processedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                                    }
                                }
                                withContext(Dispatchers.Main) {
                                    viewModel.triggerToast("Saved to gallery!")
                                }
                            } catch (_: Exception) {
                                withContext(Dispatchers.Main) {
                                    viewModel.triggerToast("Failed to save image")
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = TealAccent)
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save Result", color = Color.White, fontWeight = FontWeight.Medium)
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = iconColor.copy(alpha = 0.15f),
                    modifier = Modifier.size(72.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = tool.name.first().toString(),
                            color = iconColor,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = tool.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = tool.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                FilledTonalButton(
                    onClick = { selectFileLauncher.launch("image/*") },
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = TealAccent)
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterFrames,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Select File", color = Color.White, fontWeight = FontWeight.Medium)
                }
            }
        }
    }

    if (showWatermarkDialog) {
        AlertDialog(
            onDismissRequest = { showWatermarkDialog = false },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(16.dp),
            title = {
                Text("Watermark Text", color = TextPrimary, fontWeight = FontWeight.SemiBold)
            },
            text = {
                androidx.compose.material3.OutlinedTextField(
                    value = watermarkText,
                    onValueChange = { watermarkText = it },
                    label = { Text("Enter watermark text", color = TextSecondary) },
                    singleLine = true,
                    colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TealAccent,
                        unfocusedBorderColor = DarkSurfaceVariant,
                        cursorColor = TealAccent,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = { showWatermarkDialog = false }) {
                    Text("OK", color = TealAccent)
                }
            }
        )
    }
}
