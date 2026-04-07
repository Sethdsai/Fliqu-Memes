package com.fliqu.memes.ui.screen

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.fliqu.memes.model.GifItem
import com.fliqu.memes.model.MediaType
import com.fliqu.memes.model.MemeProject
import com.fliqu.memes.ui.theme.DarkBg
import com.fliqu.memes.ui.theme.DarkSurface
import com.fliqu.memes.ui.theme.DarkSurfaceVariant
import com.fliqu.memes.ui.theme.TealAccent
import com.fliqu.memes.ui.theme.TextPrimary
import com.fliqu.memes.ui.theme.TextSecondary
import com.fliqu.memes.viewmodel.MainViewModel
import java.util.UUID

@Composable
fun BrowseScreen(viewModel: MainViewModel) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val gifResults by viewModel.gifResults.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val currentSource by viewModel.currentGifSource.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.trendGifs()
    }

    val uploadLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val fileName = it.lastPathSegment ?: "media_${System.currentTimeMillis()}"
            val extension = fileName.substringAfterLast('.', "").lowercase()
            val mediaType = when (extension) {
                "jpg", "jpeg", "png", "webp", "bmp" -> MediaType.IMAGE
                "gif" -> MediaType.GIF
                "mp4", "avi", "mov", "mkv" -> MediaType.VIDEO
                "mp3", "wav", "ogg" -> MediaType.AUDIO
                else -> MediaType.IMAGE
            }
            viewModel.addUploadedMedia(
                com.fliqu.memes.model.MediaFile(
                    id = UUID.randomUUID().toString(),
                    name = fileName,
                    uri = it.toString(),
                    type = mediaType,
                    size = 0L,
                    addedAt = System.currentTimeMillis()
                )
            )
            viewModel.triggerToast("Media uploaded successfully")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                color = DarkSurfaceVariant
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.searchQuery.value = it },
                    placeholder = {
                        Text(
                            text = "Search GIFs...",
                            color = TextSecondary
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = TextSecondary
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = TextSecondary
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TealAccent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = DarkSurfaceVariant,
                        unfocusedContainerColor = DarkSurfaceVariant,
                        cursorColor = TealAccent
                    ),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            FilledTonalButton(
                onClick = { uploadLauncher.launch("*/*") },
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Upload,
                    contentDescription = "Upload",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Upload", fontSize = 13.sp)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val sources = listOf("all", "tenor", "giphy", "imgur")
            sources.forEach { source ->
                FilterChip(
                    selected = currentSource == source,
                    onClick = {
                        viewModel.currentGifSource.value = source
                        if (searchQuery.isNotEmpty()) {
                            viewModel.searchGifs(searchQuery)
                        } else {
                            viewModel.trendGifs()
                        }
                    },
                    label = {
                        Text(
                            text = source.replaceFirstChar { it.uppercase() },
                            fontSize = 12.sp
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TealAccent,
                        selectedLabelColor = Color.White,
                        containerColor = DarkSurfaceVariant,
                        labelColor = TextSecondary
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        if (isSearching) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = TealAccent)
            }
        } else if (gifResults.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No GIFs found",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Try a different search term",
                        color = TextSecondary.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(gifResults, key = { it.id }) { gif ->
                    GifCard(
                        gif = gif,
                        viewModel = viewModel,
                        context = context
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun GifCard(
    gif: GifItem,
    viewModel: MainViewModel,
    context: Context
) {
    var showMenu by remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = DarkSurfaceVariant)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(gif.previewUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = gif.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (gif.title.length > 18) gif.title.take(18) + "..." else gif.title.ifEmpty { "Untitled" },
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = TealAccent.copy(alpha = 0.8f)
                    ) {
                        Text(
                            text = gif.source.uppercase(),
                            color = Color.White,
                            fontSize = 9.sp,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }
            }

            Box(modifier = Modifier.align(Alignment.TopEnd)) {
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier
                        .size(28.dp)
                        .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "More",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        containerColor = DarkSurface,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Open in Editor", color = TextPrimary) },
                            onClick = {
                                viewModel.openEditor(gif.url)
                                showMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Save to Collection", color = TextPrimary) },
                            onClick = {
                                viewModel.saveMeme(
                                    MemeProject(
                                        id = UUID.randomUUID().toString(),
                                        name = gif.title.ifEmpty { "Meme" },
                                        sourceUrl = gif.url,
                                        createdAt = System.currentTimeMillis()
                                    )
                                )
                                viewModel.triggerToast("Saved to collection!")
                                showMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Copy URL", color = TextPrimary) },
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("GIF URL", gif.url))
                                viewModel.triggerToast("URL copied!")
                                showMenu = false
                            }
                        )
                    }
                }
            }
        }
    }
}
