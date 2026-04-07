package com.fliqu.memes.viewmodel

import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fliqu.memes.model.GifItem
import com.fliqu.memes.model.MediaFile
import com.fliqu.memes.model.MemeProject
import com.fliqu.memes.model.TextLayer
import com.fliqu.memes.model.ToolItem
import com.fliqu.memes.service.GifSearchService
import com.fliqu.memes.service.ImageProcessService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class MainViewModel : ViewModel() {

    val selectedTab = MutableStateFlow(0)
    val searchQuery = MutableStateFlow("")
    val gifResults = MutableStateFlow<List<GifItem>>(emptyList())
    val isSearching = MutableStateFlow(false)
    val savedMemes = MutableStateFlow<List<MemeProject>>(emptyList())
    val uploadedMedia = MutableStateFlow<List<MediaFile>>(emptyList())
    val currentGifSource = MutableStateFlow("all")
    val showToast = MutableStateFlow<String?>(null)
    val editorGifUrl = MutableStateFlow<String?>(null)
    val selectedMediaForEditor = MutableStateFlow<String?>(null)
    val isProcessing = MutableStateFlow(false)

    var textLayers by mutableStateOf<List<TextLayer>>(emptyList())
        private set

    var processedBitmap by mutableStateOf<Bitmap?>(null)
        private set

    fun searchGifs(query: String) {
        viewModelScope.launch {
            isSearching.value = true
            try {
                val source = currentGifSource.value
                val results = if (source == "all") {
                    val tenorDeferred = async { GifSearchService.searchTenor(query, 10) }
                    val giphyDeferred = async { GifSearchService.searchGiphy(query, 10) }
                    tenorDeferred.await() + giphyDeferred.await()
                } else if (source == "tenor") {
                    GifSearchService.searchTenor(query, 20)
                } else if (source == "giphy") {
                    GifSearchService.searchGiphy(query, 20)
                } else {
                    emptyList()
                }
                gifResults.value = results
            } catch (_: Exception) {
                gifResults.value = emptyList()
            } finally {
                isSearching.value = false
            }
        }
    }

    fun trendGifs() {
        viewModelScope.launch {
            isSearching.value = true
            try {
                val tenorDeferred = async { GifSearchService.searchTenor("trending", 10) }
                val giphyDeferred = async { GifSearchService.searchGiphy("trending", 10) }
                val results = tenorDeferred.await() + giphyDeferred.await()
                gifResults.value = results
            } catch (_: Exception) {
                gifResults.value = emptyList()
            } finally {
                isSearching.value = false
            }
        }
    }

    fun openEditor(url: String) {
        editorGifUrl.value = url
        selectedMediaForEditor.value = null
        textLayers = emptyList()
        selectedTab.value = 2
    }

    fun openEditorWithMedia(uri: String) {
        selectedMediaForEditor.value = uri
        editorGifUrl.value = null
        textLayers = emptyList()
        selectedTab.value = 2
    }

    fun closeEditor() {
        editorGifUrl.value = null
        selectedMediaForEditor.value = null
        textLayers = emptyList()
        processedBitmap = null
    }

    fun addTextLayer() {
        val newLayer = TextLayer(
            id = UUID.randomUUID().toString(),
            text = "New Text",
            x = 100f,
            y = 100f
        )
        textLayers = textLayers + newLayer
    }

    fun updateTextLayer(layer: TextLayer) {
        textLayers = textLayers.map { if (it.id == layer.id) layer else it }
    }

    fun removeTextLayer(id: String) {
        textLayers = textLayers.filter { it.id != id }
    }

    fun clearTextLayers() {
        textLayers = emptyList()
    }

    fun processImage(bitmap: Bitmap, toolType: String) {
        viewModelScope.launch {
            isProcessing.value = true
            try {
                val result = withContext(Dispatchers.Default) {
                    when (toolType) {
                        "bg_remove" -> ImageProcessService.removeBackground(bitmap)
                        "crop_resize" -> ImageProcessService.cropCenter(bitmap)
                        "rotate_flip" -> ImageProcessService.rotate90(bitmap)
                        "watermark" -> ImageProcessService.addWatermark(bitmap, "Fliqu Memes")
                        "filters" -> ImageProcessService.applyGrayscale(bitmap)
                        "combine" -> ImageProcessService.combineHorizontal(listOf(bitmap, bitmap))
                        else -> bitmap
                    }
                }
                processedBitmap = result
            } catch (_: Exception) {
                processedBitmap = null
            } finally {
                isProcessing.value = false
            }
        }
    }

    fun saveMeme(project: MemeProject) {
        val current = savedMemes.value.toMutableList()
        current.add(project)
        savedMemes.value = current
    }

    fun deleteMeme(id: String) {
        savedMemes.value = savedMemes.value.filter { it.id != id }
    }

    fun toggleFavorite(id: String) {
        savedMemes.value = savedMemes.value.map {
            if (it.id == id) it.copy(isFavorite = !it.isFavorite) else it
        }
    }

    fun addUploadedMedia(media: MediaFile) {
        uploadedMedia.value = uploadedMedia.value + media
    }

    fun removeUploadedMedia(id: String) {
        uploadedMedia.value = uploadedMedia.value.filter { it.id != id }
    }

    fun triggerToast(message: String) {
        showToast.value = message
    }

    fun clearToast() {
        showToast.value = null
    }

    fun getToolList(): List<ToolItem> = listOf(
        ToolItem("bg_remove", "BG Remove", "Remove image backgrounds automatically", "image_search"),
        ToolItem("text_overlay", "Text Overlay", "Add text layers to images", "text_fields"),
        ToolItem("crop_resize", "Crop & Resize", "Crop and resize images", "crop"),
        ToolItem("rotate_flip", "Rotate & Flip", "Rotate and flip images", "rotate_right"),
        ToolItem("watermark", "Watermark", "Add watermark to images", "branding_watermark"),
        ToolItem("filters", "Filters", "Apply image filters and effects", "filter"),
        ToolItem("combine", "Combine", "Combine multiple images", "view_column"),
        ToolItem("convert", "Convert", "Convert image formats", "transform"),
        ToolItem("speed", "Speed", "Adjust video speed", "speed"),
        ToolItem("frames", "Frames", "Extract frames from video", "filter_frames"),
        ToolItem("reverse", "Reverse", "Reverse video playback", "fast_rewind"),
        ToolItem("trim", "Trim", "Trim video clips", "content_cut")
    )
}
