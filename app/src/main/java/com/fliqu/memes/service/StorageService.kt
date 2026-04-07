package com.fliqu.memes.service

import android.content.ContentResolver
import android.content.ContentValues
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.OutputStream

object StorageService {

    fun saveBitmapToGallery(contentResolver: ContentResolver, bitmap: Bitmap, name: String): Boolean {
        return try {
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "$name.png")
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Fliqu")
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }
            }

            val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                ?: return false

            val stream: OutputStream? = contentResolver.openOutputStream(uri)
            stream?.use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.clear()
                values.put(MediaStore.Images.Media.IS_PENDING, 0)
                contentResolver.update(uri, values, null, null)
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getFileName(contentResolver: ContentResolver, uri: Uri): String {
        var name = "file_${System.currentTimeMillis()}"
        try {
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val index = it.getColumnIndexOrThrow("_display_name")
                    name = it.getString(index)
                }
            }
        } catch (_: Exception) {
        }
        return name
    }

    fun getFileSize(contentResolver: ContentResolver, uri: Uri): Long {
        var size = 0L
        try {
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val index = it.getColumnIndexOrThrow("_size")
                    size = it.getLong(index)
                }
            }
        } catch (_: Exception) {
        }
        return size
    }
}
