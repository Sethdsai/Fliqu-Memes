package com.fliqu.memes.model

data class GifItem(
    val id: String,
    val url: String,
    val previewUrl: String,
    val title: String,
    val source: String,
    val width: Int = 0,
    val height: Int = 0
)

data class MemeProject(
    val id: String,
    val name: String,
    val sourceUrl: String,
    val createdAt: Long,
    val isFavorite: Boolean = false
)

data class MediaFile(
    val id: String,
    val name: String,
    val uri: String,
    val type: MediaType,
    val size: Long,
    val addedAt: Long
)

enum class MediaType {
    IMAGE, VIDEO, AUDIO, GIF
}

data class ToolItem(
    val id: String,
    val name: String,
    val description: String,
    val icon: String
)
