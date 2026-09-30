package com.robogyaan.invoice.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

data class UpdateInfo(
    val isAvailable: Boolean,
    val latestVersion: String,
    val downloadUrl: String,
    val releaseNotes: String = ""
)

object AppUpdateManager {
    const val CURRENT_VERSION = "1.5.2"
    private const val GITHUB_RELEASES_API =
        "https://api.github.com/repos/parthasdey2304/roboGyaanInvoice/releases/latest"

    suspend fun checkForUpdates(context: Context): UpdateInfo = withContext(Dispatchers.IO) {
        try {
            val url = URL(GITHUB_RELEASES_API)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("Accept", "application/vnd.github.v3+json")
            connection.setRequestProperty("User-Agent", "RoboGyaanInvoice-Android")
            connection.connectTimeout = 8000
            connection.readTimeout = 8000

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(response)
                val tagName = json.optString("tag_name", "").removePrefix("v").trim()
                val body = json.optString("body", "")

                var apkDownloadUrl = ""
                val assets = json.optJSONArray("assets")
                if (assets != null) {
                    for (i in 0 until assets.length()) {
                        val asset = assets.getJSONObject(i)
                        val name = asset.optString("name", "")
                        if (name.endsWith(".apk", ignoreCase = true)) {
                            apkDownloadUrl = asset.optString("browser_download_url", "")
                            break
                        }
                    }
                }

                if (apkDownloadUrl.isEmpty()) {
                    apkDownloadUrl = "https://github.com/parthasdey2304/roboGyaanInvoice/releases/download/v$tagName/robogyaan-invoice-v$tagName.apk"
                }

                val isNewer = compareVersions(tagName, CURRENT_VERSION) > 0
                return@withContext UpdateInfo(
                    isAvailable = isNewer,
                    latestVersion = tagName.ifEmpty { CURRENT_VERSION },
                    downloadUrl = apkDownloadUrl,
                    releaseNotes = body
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return@withContext UpdateInfo(
            isAvailable = false,
            latestVersion = CURRENT_VERSION,
            downloadUrl = "https://github.com/parthasdey2304/roboGyaanInvoice/releases/download/v$CURRENT_VERSION/robogyaan-invoice-v$CURRENT_VERSION.apk"
        )
    }

    private fun compareVersions(v1: String, v2: String): Int {
        val parts1 = v1.split(".").mapNotNull { it.toIntOrNull() }
        val parts2 = v2.split(".").mapNotNull { it.toIntOrNull() }
        val length = maxOf(parts1.size, parts2.size)
        for (i in 0 until length) {
            val p1 = parts1.getOrElse(i) { 0 }
            val p2 = parts2.getOrElse(i) { 0 }
            if (p1 != p2) return p1.compareTo(p2)
        }
        return 0
    }

    suspend fun downloadApk(
        context: Context,
        downloadUrl: String,
        onProgress: (progress: Int, speedMBs: Float, downloadedBytes: Long, totalBytes: Long) -> Unit
    ): File? = withContext(Dispatchers.IO) {
        val targetFile = File(context.cacheDir, "robogyaan-invoice-update.apk")
        // If file exists from previous partial or incomplete attempt, safely remove it
        if (targetFile.exists()) {
            targetFile.delete()
        }

        var connection: HttpURLConnection? = null
        try {
            val url = URL(downloadUrl)
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("User-Agent", "RoboGyaanInvoice-Android")
            connection.connectTimeout = 15000
            connection.readTimeout = 20000
            connection.instanceFollowRedirects = true

            // Follow HTTP redirects if needed
            var responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_MOVED_TEMP ||
                responseCode == HttpURLConnection.HTTP_MOVED_PERM ||
                responseCode == 307 || responseCode == 308
            ) {
                val newUrl = connection.getHeaderField("Location")
                connection.disconnect()
                connection = URL(newUrl).openConnection() as HttpURLConnection
                connection.setRequestProperty("User-Agent", "RoboGyaanInvoice-Android")
                connection.connectTimeout = 15000
                connection.readTimeout = 20000
                responseCode = connection.responseCode
            }

            if (responseCode != HttpURLConnection.HTTP_OK) {
                targetFile.delete()
                return@withContext null
            }

            val fileLength = connection.contentLength.toLong()
            val totalBytes = if (fileLength > 0) fileLength else (24L * 1024 * 1024)

            connection.inputStream.use { input ->
                FileOutputStream(targetFile).use { output ->
                    val buffer = ByteArray(16384)
                    var bytesRead: Long = 0
                    var count: Int
                    var lastTime = System.currentTimeMillis()
                    var bytesSinceLast = 0L

                    while (input.read(buffer).also { count = it } != -1) {
                        output.write(buffer, 0, count)
                        bytesRead += count
                        bytesSinceLast += count

                        val currentTime = System.currentTimeMillis()
                        val elapsed = currentTime - lastTime
                        if (elapsed >= 250) {
                            val speedMBs = (bytesSinceLast.toFloat() / (1024f * 1024f)) / (elapsed.toFloat() / 1000f)
                            val progress = ((bytesRead.toDouble() / totalBytes.toDouble()) * 100).toInt().coerceIn(0, 99)
                            onProgress(progress, speedMBs, bytesRead, totalBytes)
                            lastTime = currentTime
                            bytesSinceLast = 0L
                        }
                    }
                    output.flush()
                }
            }

            onProgress(100, 0f, targetFile.length(), targetFile.length())
            return@withContext targetFile
        } catch (e: Exception) {
            e.printStackTrace()
            // Delete partial or failed download so restart always begins from beginning
            if (targetFile.exists()) {
                targetFile.delete()
            }
            return@withContext null
        } finally {
            connection?.disconnect()
        }
    }

    fun deletePendingApk(context: Context) {
        val targetFile = File(context.cacheDir, "robogyaan-invoice-update.apk")
        if (targetFile.exists()) {
            targetFile.delete()
        }
    }

    fun installApk(context: Context, apkFile: File) {
        if (!apkFile.exists()) return
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            apkFile
        )
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }
}
