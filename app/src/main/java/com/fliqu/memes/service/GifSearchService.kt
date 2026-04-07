package com.fliqu.memes.service

import com.fliqu.memes.model.GifItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

object GifSearchService {

    private const val TENOR_BASE = "https://g.tenor.com/v1"
    private const val GIPHY_BASE = "https://api.giphy.com/v1/gifs"

    suspend fun searchTenor(query: String, limit: Int = 20): List<GifItem> = withContext(Dispatchers.IO) {
        try {
            val encodedQuery = URLEncoder.encode(query, "UTF-8")
            val urlStr = "$TENOR_BASE/search?q=$encodedQuery&key=LIVDSRZULELA&limit=$limit&media_filter=minimal"
            val conn = URL(urlStr).openConnection() as HttpURLConnection
            conn.connectTimeout = 10000
            conn.readTimeout = 10000

            val responseCode = conn.responseCode
            if (responseCode !in 200..299) {
                conn.disconnect()
                return@withContext emptyList()
            }

            val response = conn.inputStream?.bufferedReader()?.readText() ?: return@withContext emptyList()
            conn.disconnect()

            val json = JSONObject(response)
            val results = json.optJSONArray("results") ?: return@withContext emptyList()
            val items = mutableListOf<GifItem>()

            for (i in 0 until minOf(results.length(), limit)) {
                val item = results.optJSONObject(i) ?: continue
                val mediaArray = item.optJSONArray("media") ?: continue
                if (mediaArray.length() == 0) continue

                val media = mediaArray.optJSONObject(0) ?: continue
                val gifObj = media.optJSONObject("gif") ?: continue
                val gifUrl = gifObj.optString("url", "")
                if (gifUrl.isBlank()) continue

                items.add(
                    GifItem(
                        id = item.optString("id", ""),
                        url = gifUrl,
                        previewUrl = gifObj.optString("preview", gifUrl),
                        title = item.optString("title", query).ifBlank { query },
                        source = "tenor",
                        width = 480,
                        height = 0
                    )
                )
            }
            items
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun searchGiphy(query: String, limit: Int = 20): List<GifItem> = withContext(Dispatchers.IO) {
        try {
            val encodedQuery = URLEncoder.encode(query, "UTF-8")
            val urlStr = "$GIPHY_BASE/search?api_key=dc6zaTOxFJmzC&q=$encodedQuery&limit=$limit&rating=g"
            val conn = URL(urlStr).openConnection() as HttpURLConnection
            conn.connectTimeout = 10000
            conn.readTimeout = 10000

            val responseCode = conn.responseCode
            if (responseCode !in 200..299) {
                conn.disconnect()
                return@withContext emptyList()
            }

            val response = conn.inputStream?.bufferedReader()?.readText() ?: return@withContext emptyList()
            conn.disconnect()

            val json = JSONObject(response)
            val data = json.optJSONObject("data") ?: json
            val results = data.optJSONArray("data") ?: return@withContext emptyList()
            val items = mutableListOf<GifItem>()

            for (i in 0 until minOf(results.length(), limit)) {
                val item = results.optJSONObject(i) ?: continue
                val images = item.optJSONObject("images") ?: continue
                val fixed = images.optJSONObject("fixed_height") ?: images.optJSONObject("fixed_width") ?: continue
                val gifUrl = fixed.optString("url", "")
                if (gifUrl.isBlank()) continue

                items.add(
                    GifItem(
                        id = item.optString("id", ""),
                        url = gifUrl,
                        previewUrl = gifUrl,
                        title = item.optString("title", query).ifBlank { query },
                        source = "giphy",
                        width = fixed.optInt("width", 480),
                        height = fixed.optInt("height", 480)
                    )
                )
            }
            items
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun trendingTenor(limit: Int = 15): List<GifItem> = withContext(Dispatchers.IO) {
        try {
            val urlStr = "$TENOR_BASE/trending?key=LIVDSRZULELA&limit=$limit&media_filter=minimal"
            val conn = URL(urlStr).openConnection() as HttpURLConnection
            conn.connectTimeout = 10000
            conn.readTimeout = 10000

            val responseCode = conn.responseCode
            if (responseCode !in 200..299) {
                conn.disconnect()
                return@withContext emptyList()
            }

            val response = conn.inputStream?.bufferedReader()?.readText() ?: return@withContext emptyList()
            conn.disconnect()

            val json = JSONObject(response)
            val results = json.optJSONArray("results") ?: return@withContext emptyList()
            val items = mutableListOf<GifItem>()

            for (i in 0 until minOf(results.length(), limit)) {
                val item = results.optJSONObject(i) ?: continue
                val mediaArray = item.optJSONArray("media") ?: continue
                if (mediaArray.length() == 0) continue
                val media = mediaArray.optJSONObject(0) ?: continue
                val gifObj = media.optJSONObject("gif") ?: continue
                val gifUrl = gifObj.optString("url", "")
                if (gifUrl.isBlank()) continue

                items.add(
                    GifItem(
                        id = item.optString("id", ""),
                        url = gifUrl,
                        previewUrl = gifObj.optString("preview", gifUrl),
                        title = item.optString("title", "Trending").ifBlank { "Trending" },
                        source = "tenor",
                        width = 480,
                        height = 0
                    )
                )
            }
            items
        } catch (e: Exception) {
            emptyList()
        }
    }
}
