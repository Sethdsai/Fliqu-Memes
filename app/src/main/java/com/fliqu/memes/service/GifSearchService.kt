package com.fliqu.memes.service

import com.fliqu.memes.model.GifItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URLEncoder
import java.net.URL

object GifSearchService {

    private const val TENOR_KEY = "LIVDSRZULELA"
    private const val GIPHY_KEY = "dc6zaTOxFJmzC"
    private const val TENOR_BASE = "https://api.tenor.com/v1/search"
    private const val GIPHY_BASE = "https://api.giphy.com/v1/gifs/search"

    suspend fun searchTenor(query: String, limit: Int = 20, offset: Int = 0): List<GifItem> = withContext(Dispatchers.IO) {
        val encoded = URLEncoder.encode(query, "UTF-8")
        val urlStr = "$TENOR_BASE?q=$encoded&key=$TENOR_KEY&limit=$limit&pos=$offset"
        val url = URL(urlStr)
        val conn = url.openConnection() as HttpURLConnection
        conn.connectTimeout = 15000
        conn.readTimeout = 15000
        conn.requestMethod = "GET"

        try {
            val responseCode = conn.responseCode
            val stream = if (responseCode in 200..299) conn.inputStream else conn.errorStream
            val body = stream?.bufferedReader()?.use { it.readText() } ?: return@withContext emptyList()

            if (responseCode !in 200..299) return@withContext emptyList()

            val json = JSONObject(body)
            val results = json.getJSONArray("results")
            val items = mutableListOf<GifItem>()

            for (i in 0 until results.length()) {
                val item = results.getJSONObject(i)
                val mediaArray = item.optJSONArray("media")
                if (mediaArray != null && mediaArray.length() > 0) {
                    val media = mediaArray.getJSONObject(0)
                    val gifObj = media.optJSONObject("gif")
                    val tinygifObj = media.optJSONObject("tinygif")

                    if (gifObj != null) {
                        items.add(
                            GifItem(
                                id = item.getString("id"),
                                url = gifObj.getString("url"),
                                previewUrl = tinygifObj?.getString("url") ?: gifObj.getString("url"),
                                title = item.optString("title", ""),
                                source = "tenor",
                                width = gifObj.optInt("dims", 0).let {
                                    val dims = gifObj.optJSONArray("dims")
                                    if (dims != null && dims.length() >= 2) dims.getInt(0) else 0
                                },
                                height = gifObj.optJSONArray("dims")?.optInt(1) ?: 0
                            )
                        )
                    }
                }
            }
            items
        } finally {
            conn.disconnect()
        }
    }

    suspend fun searchGiphy(query: String, limit: Int = 20, offset: Int = 0): List<GifItem> = withContext(Dispatchers.IO) {
        val encoded = URLEncoder.encode(query, "UTF-8")
        val urlStr = "$GIPHY_BASE?q=$encoded&api_key=$GIPHY_KEY&limit=$limit&offset=$offset"
        val url = URL(urlStr)
        val conn = url.openConnection() as HttpURLConnection
        conn.connectTimeout = 15000
        conn.readTimeout = 15000
        conn.requestMethod = "GET"

        try {
            val responseCode = conn.responseCode
            val stream = if (responseCode in 200..299) conn.inputStream else conn.errorStream
            val body = stream?.bufferedReader()?.use { it.readText() } ?: return@withContext emptyList()

            if (responseCode !in 200..299) return@withContext emptyList()

            val json = JSONObject(body)
            val dataArray = json.getJSONArray("data")
            val items = mutableListOf<GifItem>()

            for (i in 0 until dataArray.length()) {
                val item = dataArray.getJSONObject(i)
                val images = item.getJSONObject("images")
                val original = images.optJSONObject("original")
                val fixedHeight = images.optJSONObject("fixed_height")
                val fixedHeightSmall = images.optJSONObject("fixed_height_small_still")

                if (original != null) {
                    items.add(
                        GifItem(
                            id = item.getString("id"),
                            url = original.getString("url"),
                            previewUrl = fixedHeightSmall?.getString("url") ?: fixedHeight?.getString("url") ?: original.getString("url"),
                            title = item.optString("title", ""),
                            source = "giphy",
                            width = original.optInt("width", 0),
                            height = original.optInt("height", 0)
                        )
                    )
                }
            }
            items
        } finally {
            conn.disconnect()
        }
    }

    suspend fun searchAll(query: String, limit: Int = 20): List<GifItem> = coroutineScope {
        val halfLimit = limit / 2
        val tenorDeferred = async(Dispatchers.IO) { searchTenor(query, halfLimit) }
        val giphyDeferred = async(Dispatchers.IO) { searchGiphy(query, limit - halfLimit) }
        val tenorResults = tenorDeferred.await()
        val giphyResults = giphyDeferred.await()
        tenorResults + giphyResults
    }
}
