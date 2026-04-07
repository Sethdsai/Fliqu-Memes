package com.fliqu.memes.model

data class TextLayer(
    val id: String,
    val text: String,
    val x: Float = 0f,
    val y: Float = 0f,
    val fontSize: Float = 32f,
    val color: Long = 0xFFFFFFFF,
    val strokeWidth: Float = 0f,
    val strokeColor: Long = 0xFF000000,
    val isBold: Boolean = false,
    val isItalic: Boolean = false
)

data class FilterType(
    val id: String,
    val name: String,
    val matrix: FloatArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FilterType) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}
