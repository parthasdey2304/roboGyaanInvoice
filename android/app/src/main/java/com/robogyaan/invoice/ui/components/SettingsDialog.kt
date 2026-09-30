package com.robogyaan.invoice.ui.components

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.robogyaan.invoice.ui.neoBrutal
import com.robogyaan.invoice.ui.theme.NeoBlack
import com.robogyaan.invoice.ui.theme.NeoYellow
import com.robogyaan.invoice.ui.theme.PoppinsFontFamily
import com.robogyaan.invoice.ui.theme.VirgilFontFamily
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun SettingsDialog(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    isDarkMode: Boolean,
    onToggleDarkMode: (Boolean) -> Unit,
    isGridBackground: Boolean,
    onToggleGridBackground: (Boolean) -> Unit
) {
    if (!isOpen) return

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var updateInfo by remember { mutableStateOf<UpdateInfo?>(null) }
    var isCheckingUpdate by remember { mutableStateOf(false) }

    var downloadProgress by remember { mutableStateOf<Int?>(null) }
    var downloadSpeedMBs by remember { mutableFloatStateOf(0f) }
    var downloadedBytes by remember { mutableLongStateOf(0L) }
    var totalBytes by remember { mutableLongStateOf(24L * 1024 * 1024) }
    var downloadedFile by remember { mutableStateOf<File?>(null) }
    var downloadJob by remember { mutableStateOf<Job?>(null) }

    // Check updates on opening dialog
    LaunchedEffect(Unit) {
        isCheckingUpdate = true
        updateInfo = AppUpdateManager.checkForUpdates(context)
        isCheckingUpdate = false
    }

    Dialog(
        onDismissRequest = {
            if (downloadProgress != null && downloadedFile == null) {
                // Cancel active download and remove partial file
                downloadJob?.cancel()
                AppUpdateManager.deletePendingApk(context)
            }
            onDismiss()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.65f))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .widthIn(max = 480.dp)
                    .neoBrutal(
                        backgroundColor = if (isDarkMode) Color(0xFF181A22) else Color(0xFFFFFDF7),
                        shadowOffset = 6.dp,
                        cornerRadius = 12.dp
                    )
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(NeoYellow, RoundedCornerShape(6.dp))
                                .border(2.dp, Color.Black, RoundedCornerShape(6.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "APP SETTINGS & UPDATES",
                                fontFamily = VirgilFontFamily,
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp,
                                color = if (isDarkMode) Color.White else Color.Black
                            )
                            Text(
                                text = "Appearance & In-App APK Updater",
                                fontFamily = PoppinsFontFamily,
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .background(Color.White, RoundedCornerShape(6.dp))
                            .border(1.5.dp, Color.Black, RoundedCornerShape(6.dp))
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 1. APPEARANCE SETTINGS
                Text(
                    text = "VISUAL APPEARANCE",
                    fontFamily = VirgilFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(6.dp))

                // Theme Mode Selector
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (isDarkMode) Color(0xFF2A2A2E) else Color.White,
                            RoundedCornerShape(8.dp)
                        )
                        .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Color Mode",
                                fontFamily = VirgilFontFamily,
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                color = if (isDarkMode) Color.White else Color.Black
                            )
                            Text(
                                text = if (isDarkMode) "Dark mode (Black Navbar)" else "Light mode (Yellow Navbar)",
                                fontFamily = PoppinsFontFamily,
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                        }

                        // Sun/Moon Toggle in Same Span/Container
                        Row(
                            modifier = Modifier
                                .background(
                                    if (isDarkMode) Color.Black else Color(0xFFF3F4F6),
                                    RoundedCornerShape(6.dp)
                                )
                                .border(1.5.dp, Color.Black, RoundedCornerShape(6.dp))
                                .padding(2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (!isDarkMode) NeoYellow else Color.Transparent,
                                        RoundedCornerShape(4.dp)
                                    )
                                    .clickable { onToggleDarkMode(false) }
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        Icons.Default.WbSunny,
                                        contentDescription = "Light Mode",
                                        tint = Color.Black,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        "Light",
                                        fontFamily = VirgilFontFamily,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 10.sp,
                                        color = Color.Black
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .background(
                                        if (isDarkMode) Color.Black else Color.Transparent,
                                        RoundedCornerShape(4.dp)
                                    )
                                    .clickable { onToggleDarkMode(true) }
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Nightlight,
                                        contentDescription = "Dark Mode",
                                        tint = if (isDarkMode) NeoYellow else Color.Black,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        "Dark",
                                        fontFamily = VirgilFontFamily,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 10.sp,
                                        color = if (isDarkMode) Color.White else Color.Black
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Canvas Background Pattern Selector
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (isDarkMode) Color(0xFF2A2A2E) else Color.White,
                            RoundedCornerShape(8.dp)
                        )
                        .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = "Canvas Background Pattern",
                            fontFamily = VirgilFontFamily,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            color = if (isDarkMode) Color.White else Color.Black
                        )
                        Text(
                            text = "Choose between square box grid (Image 1) or clean solid background.",
                            fontFamily = PoppinsFontFamily,
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (!isGridBackground) NeoYellow else if (isDarkMode) Color(0xFF22242E) else Color(0xFFF3F4F6),
                                        RoundedCornerShape(6.dp)
                                    )
                                    .border(1.5.dp, Color.Black, RoundedCornerShape(6.dp))
                                    .clickable { onToggleGridBackground(false) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Solid Canvas",
                                    fontFamily = VirgilFontFamily,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp,
                                    color = if (!isGridBackground) Color.Black else if (isDarkMode) Color.White else Color.Black
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (isGridBackground) NeoYellow else if (isDarkMode) Color(0xFF22242E) else Color(0xFFF3F4F6),
                                        RoundedCornerShape(6.dp)
                                    )
                                    .border(1.5.dp, Color.Black, RoundedCornerShape(6.dp))
                                    .clickable { onToggleGridBackground(true) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Grid Boxes (Img 1)",
                                    fontFamily = VirgilFontFamily,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp,
                                    color = if (isGridBackground) Color.Black else if (isDarkMode) Color.White else Color.Black
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 2. IN-APP APP UPDATER
                Text(
                    text = "SOFTWARE UPDATE (APK)",
                    fontFamily = VirgilFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (isDarkMode) Color(0xFF2A2A2E) else Color.White,
                            RoundedCornerShape(8.dp)
                        )
                        .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = "Status",
                                    tint = Color(0xFF16A34A),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = if (updateInfo?.isAvailable == true) {
                                        "Update Available: v${updateInfo?.latestVersion}"
                                    } else {
                                        "You are using an app that is up to date (v${AppUpdateManager.CURRENT_VERSION})"
                                    },
                                    fontFamily = VirgilFontFamily,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp,
                                    color = if (updateInfo?.isAvailable == true) NeoYellow else Color(0xFF16A34A)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .background(Color.Black, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "v${AppUpdateManager.CURRENT_VERSION}",
                                    color = NeoYellow,
                                    fontFamily = VirgilFontFamily,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 9.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Download latest official Android APK release directly with multi-page print parity and in-app installer.",
                            fontFamily = PoppinsFontFamily,
                            fontSize = 10.sp,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Progress bar during download
                        if (downloadProgress != null) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Downloading APK: $downloadProgress%",
                                        fontFamily = PoppinsFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = if (isDarkMode) Color.White else Color.Black
                                    )
                                    Text(
                                        text = "%.1f MB/s".format(downloadSpeedMBs),
                                        fontFamily = PoppinsFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = Color(0xFF16A34A)
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                val animatedProgress by animateFloatAsState(
                                    targetValue = (downloadProgress ?: 0) / 100f,
                                    label = "progress"
                                )
                                LinearProgressIndicator(
                                    progress = { animatedProgress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .border(1.dp, Color.Black, RoundedCornerShape(4.dp)),
                                    color = NeoYellow,
                                    trackColor = if (isDarkMode) Color(0xFF18181B) else Color(0xFFE5E7EB),
                                )

                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "%.1f MB / %.1f MB".format(
                                            downloadedBytes / (1024f * 1024f),
                                            totalBytes / (1024f * 1024f)
                                        ),
                                        fontSize = 9.sp,
                                        fontFamily = PoppinsFontFamily,
                                        color = Color.Gray
                                    )

                                    if (downloadedFile == null) {
                                        Text(
                                            text = "Cancel & Delete",
                                            fontSize = 9.sp,
                                            fontFamily = PoppinsFontFamily,
                                            color = Color.Red,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.clickable {
                                                downloadJob?.cancel()
                                                AppUpdateManager.deletePendingApk(context)
                                                downloadProgress = null
                                                Toast.makeText(context, "Download cancelled and cleaned up", Toast.LENGTH_SHORT).show()
                                            }
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }

                        // Download / Install Button
                        if (downloadedFile != null) {
                            // 100% Downloaded -> Show Install Now button
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .neoBrutal(
                                        backgroundColor = Color(0xFF16A34A),
                                        shadowOffset = 3.dp,
                                        cornerRadius = 6.dp
                                    )
                                    .clickable {
                                        AppUpdateManager.installApk(context, downloadedFile!!)
                                    }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        Icons.Default.InstallMobile,
                                        contentDescription = "Install Now",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "Install Now (v${AppUpdateManager.CURRENT_VERSION})",
                                        fontFamily = VirgilFontFamily,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 13.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        } else if (downloadProgress == null) {
                            val buttonVersion = updateInfo?.latestVersion ?: AppUpdateManager.CURRENT_VERSION
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .neoBrutal(
                                        backgroundColor = NeoYellow,
                                        shadowOffset = 3.dp,
                                        cornerRadius = 6.dp
                                    )
                                    .clickable {
                                        downloadJob = coroutineScope.launch {
                                            downloadProgress = 0
                                            val url = updateInfo?.downloadUrl
                                                ?: "https://github.com/parthasdey2304/roboGyaanInvoice/releases/download/v$buttonVersion/robogyaan-invoice-v$buttonVersion.apk"
                                            val file = AppUpdateManager.downloadApk(context, url) { p, speed, dBytes, tBytes ->
                                                downloadProgress = p
                                                downloadSpeedMBs = speed
                                                downloadedBytes = dBytes
                                                totalBytes = tBytes
                                            }
                                            if (file != null && file.exists()) {
                                                downloadedFile = file
                                                Toast.makeText(context, "Download complete! Ready to install.", Toast.LENGTH_SHORT).show()
                                            } else {
                                                downloadProgress = null
                                                Toast.makeText(context, "Download failed. Cleaned partial file.", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Download,
                                        contentDescription = "Download Update",
                                        tint = Color.Black,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "Download Update v$buttonVersion",
                                        fontFamily = VirgilFontFamily,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 13.sp,
                                        color = Color.Black
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "RoboGyaan Invoice Suite • Android Native Edition",
                    fontFamily = PoppinsFontFamily,
                    fontSize = 9.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
