package com.fliqu.memes.service

import android.graphics.*
import android.graphics.PorterDuff.Mode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

object ImageProcessService {

    fun removeBackground(bitmap: Bitmap, tolerance: Int = 30): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val result = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val pixels = IntArray(width * height)
        result.getPixels(pixels, 0, width, 0, 0, width, height)

        val visited = BooleanArray(width * height)
        val queue = ArrayDeque<Int>()

        fun getPixel(idx: Int): Int = pixels[idx]
        fun colorDist(a: Int, b: Int): Int {
            val dr = (a shr 16 and 0xFF) - (b shr 16 and 0xFF)
            val dg = (a shr 8 and 0xFF) - (b shr 8 and 0xFF)
            val db = (a and 0xFF) - (b and 0xFF)
            return (dr * dr + dg * dg + db * db)
        }

        val cornerPixels = listOf(0, width - 1, (height - 1) * width, (height - 1) * width + width - 1)
        val sampleColors = cornerPixels.map { getPixel(it) }
        var bgColor = sampleColors[0]

        var bestCount = 0
        val colorCounts = mutableMapOf<Int, Int>()
        for (c in sampleColors) {
            var isMatch = false
            for (existing in colorCounts.keys) {
                if (colorDist(c, existing) < tolerance * tolerance * 9) {
                    colorCounts[existing] = (colorCounts[existing] ?: 0) + 1
                    if (colorCounts[existing]!! > bestCount) {
                        bestCount = colorCounts[existing]!!
                        bgColor = existing
                    }
                    isMatch = true
                    break
                }
            }
            if (!isMatch) {
                colorCounts[c] = 1
            }
        }

        val tolSq = tolerance * tolerance

        for (idx in cornerPixels) {
            if (!visited[idx] && colorDist(getPixel(idx), bgColor) < tolSq) {
                queue.add(idx)
                visited[idx] = true
            }
        }

        val dx = intArrayOf(0, 0, 1, -1)
        val dy = intArrayOf(1, -1, 0, 0)

        while (queue.isNotEmpty()) {
            val idx = queue.removeFirst()
            pixels[idx] = 0x00000000

            val x = idx % width
            val y = idx / width

            for (d in 0 until 4) {
                val nx = x + dx[d]
                val ny = y + dy[d]
                if (nx in 0 until width && ny in 0 until height) {
                    val nIdx = ny * width + nx
                    if (!visited[nIdx] && colorDist(getPixel(nIdx), bgColor) < tolSq) {
                        visited[nIdx] = true
                        queue.add(nIdx)
                    }
                }
            }
        }

        result.setPixels(pixels, 0, width, 0, 0, width, height)
        return result
    }

    fun applyGrayscale(bitmap: Bitmap): Bitmap {
        val result = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        val paint = Paint()
        val colorMatrix = ColorMatrix(
            floatArrayOf(
                0.299f, 0.587f, 0.114f, 0f, 0f,
                0.299f, 0.587f, 0.114f, 0f, 0f,
                0.299f, 0.587f, 0.114f, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            )
        )
        paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        return result
    }

    fun applySepia(bitmap: Bitmap): Bitmap {
        val result = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        val paint = Paint()
        val colorMatrix = ColorMatrix(
            floatArrayOf(
                0.393f, 0.769f, 0.189f, 0f, 0f,
                0.349f, 0.686f, 0.168f, 0f, 0f,
                0.272f, 0.534f, 0.131f, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            )
        )
        paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        return result
    }

    fun applyBrightness(bitmap: Bitmap, value: Float): Bitmap {
        val result = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        val paint = Paint()
        val colorMatrix = ColorMatrix(
            floatArrayOf(
                1f, 0f, 0f, 0f, value,
                0f, 1f, 0f, 0f, value,
                0f, 0f, 1f, 0f, value,
                0f, 0f, 0f, 1f, 0f
            )
        )
        paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        return result
    }

    fun applyContrast(bitmap: Bitmap, value: Float): Bitmap {
        val result = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        val paint = Paint()
        val scale = value + 1f
        val translate = (-128f * scale + 128f)
        val colorMatrix = ColorMatrix(
            floatArrayOf(
                scale, 0f, 0f, 0f, translate,
                0f, scale, 0f, 0f, translate,
                0f, 0f, scale, 0f, translate,
                0f, 0f, 0f, 1f, 0f
            )
        )
        paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        return result
    }

    fun applyInvert(bitmap: Bitmap): Bitmap {
        val result = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        val paint = Paint()
        val colorMatrix = ColorMatrix(
            floatArrayOf(
                -1f, 0f, 0f, 0f, 255f,
                0f, -1f, 0f, 0f, 255f,
                0f, 0f, -1f, 0f, 255f,
                0f, 0f, 0f, 1f, 0f
            )
        )
        paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        return result
    }

    fun rotate90(bitmap: Bitmap): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(90f)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    fun flipHorizontal(bitmap: Bitmap): Bitmap {
        val matrix = Matrix()
        matrix.postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    fun flipVertical(bitmap: Bitmap): Bitmap {
        val matrix = Matrix()
        matrix.postScale(1f, -1f, bitmap.width / 2f, bitmap.height / 2f)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    fun addWatermark(bitmap: Bitmap, text: String): Bitmap {
        val result = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(result)
        val paint = Paint().apply {
            color = Color.argb(128, 255, 255, 255)
            textSize = 36f
            isAntiAlias = true
            typeface = Typeface.DEFAULT_BOLD
            setShadowLayer(2f, 1f, 1f, Color.argb(128, 0, 0, 0))
        }
        val textWidth = paint.measureText(text)
        val x = bitmap.width - textWidth - 16f
        val y = bitmap.height - 24f
        canvas.drawText(text, x, y, paint)
        return result
    }

    fun cropCenter(bitmap: Bitmap): Bitmap {
        val size = min(bitmap.width, bitmap.height)
        if (size <= 0) return bitmap
        val x = (bitmap.width - size) / 2
        val y = (bitmap.height - size) / 2
        return Bitmap.createBitmap(bitmap, x, y, size, size)
    }

    fun resize(bitmap: Bitmap, maxDim: Int): Bitmap {
        if (maxDim <= 0) return bitmap
        val width = bitmap.width
        val height = bitmap.height
        if (width <= maxDim && height <= maxDim) return bitmap
        val ratio: Float
        if (width >= height) {
            ratio = maxDim.toFloat() / width
        } else {
            ratio = maxDim.toFloat() / height
        }
        val newWidth = max(1, (width * ratio).roundToInt())
        val newHeight = max(1, (height * ratio).roundToInt())
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    fun combineHorizontal(bitmaps: List<Bitmap>): Bitmap {
        if (bitmaps.isEmpty()) return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        if (bitmaps.size == 1) return bitmaps[0]

        val maxHeight = bitmaps.maxOf { it.height }
        var totalWidth = 0
        for (b in bitmaps) {
            totalWidth += b.width
        }
        if (totalWidth <= 0 || maxHeight <= 0) return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)

        val result = Bitmap.createBitmap(totalWidth, maxHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        var x = 0
        for (bm in bitmaps) {
            val dstY = (maxHeight - bm.height) / 2f
            canvas.drawBitmap(bm, x.toFloat(), dstY, null)
            x += bm.width
        }
        return result
    }

    suspend fun removeBackgroundSuspend(bitmap: Bitmap, tolerance: Int = 30): Bitmap = withContext(Dispatchers.Default) {
        removeBackground(bitmap, tolerance)
    }

    suspend fun applyGrayscaleSuspend(bitmap: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        applyGrayscale(bitmap)
    }

    suspend fun applySepiaSuspend(bitmap: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        applySepia(bitmap)
    }

    suspend fun applyBrightnessSuspend(bitmap: Bitmap, value: Float): Bitmap = withContext(Dispatchers.Default) {
        applyBrightness(bitmap, value)
    }

    suspend fun applyContrastSuspend(bitmap: Bitmap, value: Float): Bitmap = withContext(Dispatchers.Default) {
        applyContrast(bitmap, value)
    }

    suspend fun applyInvertSuspend(bitmap: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        applyInvert(bitmap)
    }

    suspend fun rotate90Suspend(bitmap: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        rotate90(bitmap)
    }

    suspend fun flipHorizontalSuspend(bitmap: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        flipHorizontal(bitmap)
    }

    suspend fun flipVerticalSuspend(bitmap: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        flipVertical(bitmap)
    }

    suspend fun addWatermarkSuspend(bitmap: Bitmap, text: String): Bitmap = withContext(Dispatchers.Default) {
        addWatermark(bitmap, text)
    }

    suspend fun cropCenterSuspend(bitmap: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        cropCenter(bitmap)
    }

    suspend fun resizeSuspend(bitmap: Bitmap, maxDim: Int): Bitmap = withContext(Dispatchers.Default) {
        resize(bitmap, maxDim)
    }

    suspend fun combineHorizontalSuspend(bitmaps: List<Bitmap>): Bitmap = withContext(Dispatchers.Default) {
        combineHorizontal(bitmaps)
    }
}
