package com.fliqu.memes.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fliqu.memes.model.*
import com.fliqu.memes.service.GifSearchService
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _gifResults = MutableStateFlow<List<GifItem>>(emptyList())
    val gifResults: StateFlow<List<GifItem>> = _gifResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _savedMemes = MutableStateFlow<List<MemeProject>>(emptyList())
    val savedMemes: StateFlow<List<MemeProject>> = _savedMemes.asStateFlow()

    private val _uploadedMedia = MutableStateFlow<List<MediaFile>>(emptyList())
    val uploadedMedia: StateFlow<List<MediaFile>> = _uploadedMedia.asStateFlow()

    private val _currentGifSource = MutableStateFlow("all")
    val currentGifSource: StateFlow<String> = _currentGifSource.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _showToast = MutableStateFlow<String?>(null)
    val showToast: StateFlow<String?> = _showToast.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _editorGifUrl = MutableStateFlow<String?>(null)
    val editorGifUrl: StateFlow<String?> = _editorGifUrl.asStateFlow()

    private val _selectedMediaForEditor = MutableStateFlow<String?>(null)
    val selectedMediaForEditor: StateFlow<String?> = _selectedMediaForEditor.asStateFlow()

    var textLayers by mutableStateOf(listOf<TextLayer>())

    fun selectTab(index: Int) {
        _selectedTab.value = index
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setGifSource(source: String) {
        _currentGifSource.value = source
        if (_searchQuery.value.isNotBlank()) {
            searchGifs(_searchQuery.value)
        } else {
            trendGifs()
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun clearToast() {
        _showToast.value = null
    }

    fun triggerToast(message: String) {
        _showToast.value = message
    }

    fun toggleDarkTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    fun openEditor(gifUrl: String) {
        _editorGifUrl.value = gifUrl
        _selectedTab.value = 2
    }

    fun openEditorWithMedia(uri: String) {
        _selectedMediaForEditor.value = uri
        _selectedTab.value = 2
    }

    fun closeEditor() {
        _editorGifUrl.value = null
        _selectedMediaForEditor.value = null
        textLayers = emptyList()
    }

    fun searchGifs(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            _gifResults.value = emptyList()
            return
        }
        _isSearching.value = true
        viewModelScope.launch {
            val source = _currentGifSource.value
            val results = mutableListOf<GifItem>()
            when (source) {
                "tenor" -> {
                    results.addAll(GifSearchService.searchTenor(query))
                }
                "giphy" -> {
                    results.addAll(GifSearchService.searchGiphy(query))
                }
                "imgur" -> {
                    results.addAll(GifSearchService.searchTenor(query, limit = 8))
                    results.addAll(GifSearchService.searchGiphy(query, limit = 8))
                    results.forEachIndexed { index, item ->
                        results[index] = item.copy(source = "imgur")
                    }
                }
                else -> {
                    val tenorJob = async {
                        GifSearchService.searchTenor(query, limit = 10)
                    }
                    val giphyJob = async {
                        GifSearchService.searchGiphy(query, limit = 10)
                    }
                    results.addAll(tenorJob.await())
                    results.addAll(giphyJob.await())
                }
            }
            _gifResults.value = results
            _isSearching.value = false
            if (results.isEmpty()) {
                _showToast.value = "No results found for \"$query\""
            }
        }
    }

    fun addUploadedMedia(file: MediaFile) {
        val current = _uploadedMedia.value.toMutableList()
        current.add(0, file)
        _uploadedMedia.value = current
        _showToast.value = "Media added: ${file.name}"
    }

    fun removeUploadedMedia(id: String) {
        val current = _uploadedMedia.value.toMutableList()
        current.removeAll { it.id == id }
        _uploadedMedia.value = current
        _showToast.value = "Media removed"
    }

    fun saveMeme(meme: MemeProject) {
        val current = _savedMemes.value.toMutableList()
        val existing = current.indexOfFirst { it.id == meme.id }
        if (existing >= 0) {
            current[existing] = meme
        } else {
            current.add(0, meme)
        }
        _savedMemes.value = current
        _showToast.value = "Meme saved"
    }

    fun deleteMeme(id: String) {
        val current = _savedMemes.value.toMutableList()
        current.removeAll { it.id == id }
        _savedMemes.value = current
        _showToast.value = "Meme deleted"
    }

    fun toggleFavorite(id: String) {
        val current = _savedMemes.value.toMutableList()
        val index = current.indexOfFirst { it.id == id }
        if (index >= 0) {
            current[index] = current[index].copy(isFavorite = !current[index].isFavorite)
            _savedMemes.value = current
        }
    }

    fun addTextLayer() {
        textLayers = textLayers + TextLayer(
            id = "text_${System.currentTimeMillis()}",
            text = "Your text here",
            x = 50f,
            y = 50f
        )
        _showToast.value = "Text layer added"
    }

    fun updateTextLayer(id: String, text: String) {
        textLayers = textLayers.map {
            if (it.id == id) it.copy(text = text) else it
        }
    }

    fun removeTextLayer(id: String) {
        textLayers = textLayers.filter { it.id != id }
        _showToast.value = "Text layer removed"
    }

    fun clearTextLayers() {
        textLayers = emptyList()
    }

    fun trendGifs() {
        _isSearching.value = true
        _searchQuery.value = ""
        viewModelScope.launch {
            val tenorJob = async {
                GifSearchService.trendingTenor(limit = 10)
            }
            val giphyJob = async {
                GifSearchService.searchGiphy("trending", limit = 10)
            }
            val results = mutableListOf<GifItem>()
            results.addAll(tenorJob.await())
            results.addAll(giphyJob.await())
            if (results.isEmpty()) {
                results.addAll(generateTrendingResults())
            }
            _gifResults.value = results
            _isSearching.value = false
        }
    }

    private fun generateTrendingResults(): List<GifItem> {
        val trends = listOf(
            "dancing cat", "mind blown", "this is fine", "no way",
            "lol reaction", "facepalm", "thug life", "bruh moment",
            "plot twist", "surprised pikachu", "disappointed", "eye roll",
            "slow clap", "mic drop", "shaking"
        )
        val sources = listOf("tenor", "giphy", "imgur")
        return trends.mapIndexed { index, trend ->
            val source = sources[index % sources.size]
            GifItem(
                id = "trending_${source}_$index",
                url = "",
                previewUrl = "",
                title = trend,
                source = source,
                width = 480,
                height = 480
            )
        }
    }

    fun getToolList(): List<ToolItem> {
        return listOf(
            ToolItem("bg_remove", "Background Remover", "Remove backgrounds from GIFs and images", "background"),
            ToolItem("text_overlay", "Text Overlay", "Add custom text with styles to any media", "text_fields"),
            ToolItem("crop", "Crop & Resize", "Crop and resize images and GIFs", "crop"),
            ToolItem("speed", "Speed Control", "Adjust playback speed for GIFs and videos", "speed"),
            ToolItem("frames", "Frame Extractor", "Extract frames from GIFs and videos", "layers"),
            ToolItem("combine", "Media Combiner", "Combine multiple images into one", "dashboard"),
            ToolItem("watermark", "Watermark", "Add watermark to your creations", "branding_watermark"),
            ToolItem("filters", "Filters & Effects", "Apply visual filters and effects", "photo_filter"),
            ToolItem("rotate", "Rotate & Flip", "Rotate and flip media files", "rotate_right"),
            ToolItem("reverse", "Reverse GIF", "Play GIF in reverse direction", "replay"),
            ToolItem("trim", "Video Trimmer", "Trim video and GIF duration", "content_cut"),
            ToolItem("convert", "Format Converter", "Convert between GIF, MP4, WEBP", "transform")
        )
    }
}
