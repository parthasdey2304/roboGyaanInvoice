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
    const val CURRENT_VERSION = "1.5.7"
    private const val GITHUB_RELEASES_API =
        "https://api.github.com/repos/parthasdey2304/roboGyaanInvoice/releases/latest"

    fun getInstalledVersion(context: Context): String {
        return try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            pInfo.versionName ?: CURRENT_VERSION
        } catch (_: Exception) {
            CURRENT_VERSION
        }
    }

    suspend fun checkForUpdates(context: Context): UpdateInfo = withContext(Dispatchers.IO) {
        val currentInstalled = getInstalledVersion(context)

        // 1. Try official GitHub Releases API first
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

                val isNewer = compareVersions(tagName, currentInstalled) > 0
                return@withContext UpdateInfo(
                    isAvailable = isNewer,
                    latestVersion = tagName.ifEmpty { currentInstalled },
                    downloadUrl = apkDownloadUrl,
                    releaseNotes = body
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 2. Secondary fallback: check web portal API endpoint
        try {
            val fallbackUrl = URL("https://invoice.robogyaan.in/api/version")
            val connection = fallbackUrl.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("Accept", "application/json")
            connection.setRequestProperty("User-Agent", "RoboGyaanInvoice-Android")
            connection.connectTimeout = 6000
            connection.readTimeout = 6000

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(response)
                val latestVer = json.optString("latestVersion", "").removePrefix("v").trim()
                val apkUrl = json.optString("downloadUrl", "")
                val notes = json.optString("releaseNotes", "")

                if (latestVer.isNotEmpty()) {
                    val isNewer = compareVersions(latestVer, currentInstalled) > 0
                    return@withContext UpdateInfo(
                        isAvailable = isNewer,
                        latestVersion = latestVer,
                        downloadUrl = apkUrl.ifEmpty {
                            "https://github.com/parthasdey2304/roboGyaanInvoice/releases/download/v$latestVer/robogyaan-invoice-v$latestVer.apk"
                        },
                        releaseNotes = notes
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return@withContext UpdateInfo(
            isAvailable = false,
            latestVersion = currentInstalled,
            downloadUrl = "https://github.com/parthasdey2304/roboGyaanInvoice/releases/download/v$currentInstalled/robogyaan-invoice-v$currentInstalled.apk"
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
        if (targetFile.exists()) {
            targetFile.delete()
        }

        var connection: HttpURLConnection? = null
        try {
            var currentUrl = downloadUrl
            var responseCode: Int
            var redirects = 0

            // Follow HTTP redirects safely (e.g. GitHub release -> AWS S3)
            do {
                connection?.disconnect()
                val conn = URL(currentUrl).openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.setRequestProperty("User-Agent", "RoboGyaanInvoice-Android")
                conn.connectTimeout = 15000
                conn.readTimeout = 20000
                conn.instanceFollowRedirects = true
                connection = conn

                responseCode = conn.responseCode
                if (responseCode == HttpURLConnection.HTTP_MOVED_TEMP ||
                    responseCode == HttpURLConnection.HTTP_MOVED_PERM ||
                    responseCode == 307 || responseCode == 308 || responseCode == 303 || responseCode == 302
                ) {
                    currentUrl = conn.getHeaderField("Location") ?: break
                    redirects++
                } else {
                    break
                }
            } while (redirects < 6)

            val activeConn = connection
            if (activeConn == null || responseCode != HttpURLConnection.HTTP_OK) {
                targetFile.delete()
                return@withContext null
            }

            val fileLength = activeConn.contentLength.toLong()
            val totalBytes = if (fileLength > 0) fileLength else (24L * 1024 * 1024)

            activeConn.inputStream.use { input ->
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
