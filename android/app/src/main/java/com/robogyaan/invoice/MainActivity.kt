package com.robogyaan.invoice

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VerticalSplit
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.onSizeChanged
import androidx.core.content.FileProvider
import com.robogyaan.invoice.ui.InvoiceViewModel
import com.robogyaan.invoice.ui.components.AuthPreferences
import com.robogyaan.invoice.ui.components.BoxGridBackground
import com.robogyaan.invoice.ui.components.HistorySidebarSheet
import com.robogyaan.invoice.ui.components.InvoiceEditorScreen
import com.robogyaan.invoice.ui.components.LoginScreen
import com.robogyaan.invoice.ui.components.NeoBrutalAlertDialog
import com.robogyaan.invoice.ui.components.NeoBrutalButton
import com.robogyaan.invoice.ui.components.SettingsDialog
import com.robogyaan.invoice.ui.neoBrutal
import com.robogyaan.invoice.ui.preview.InvoicePreviewScreen
import com.robogyaan.invoice.ui.theme.NeoBlack
import com.robogyaan.invoice.ui.theme.NeoYellow
import com.robogyaan.invoice.ui.theme.RoboGyaanInvoiceTheme
import com.robogyaan.invoice.ui.theme.VirgilFontFamily
import com.robogyaan.invoice.util.PdfGenerator
import kotlinx.coroutines.Dispatchers
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class MainActivity : FragmentActivity() {

    private val invoiceViewModel: InvoiceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Enable edge-to-edge so Compose can observe real WindowInsets including IME
        androidx.core.view.WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val context = LocalContext.current
            var isDarkMode by remember { mutableStateOf(AuthPreferences.isDarkMode(context)) }
            var isGridBackground by remember { mutableStateOf(AuthPreferences.isGridBackground(context)) }
            var isLoggedIn by remember { mutableStateOf(AuthPreferences.isLoggedIn(context)) }

            RoboGyaanInvoiceTheme(darkTheme = isDarkMode) {
                if (!isLoggedIn) {
                    LoginScreen(
                        isDarkMode = isDarkMode,
                        isGridBackground = isGridBackground,
                        onToggleDarkMode = {
                            val next = !isDarkMode
                            isDarkMode = next
                            AuthPreferences.setDarkMode(context, next)
                        },
                        onLoginSuccess = { isLoggedIn = true }
                    )
                } else {
                    MainScreen(
                        invoiceViewModel = invoiceViewModel,
                        isDarkMode = isDarkMode,
                        onToggleDarkMode = {
                            val next = !isDarkMode
                            isDarkMode = next
                            AuthPreferences.setDarkMode(context, next)
                        },
                        isGridBackground = isGridBackground,
                        onToggleGridBackground = {
                            val next = !isGridBackground
                            isGridBackground = next
                            AuthPreferences.setGridBackground(context, next)
                        },
                        onLogout = {
                            AuthPreferences.setLoggedIn(context, false)
                            isLoggedIn = false
                        }
                    )
                }
            }
        }
    }
}

/**
 * Hand-Drawn Asterisk / Splat Sticker Icon from Image 2
 */
@Composable
fun HandDrawnAsteriskIcon(
    modifier: Modifier = Modifier,
    color: Color = Color.Black
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f
        val strokeWidth = 2.dp.toPx()

        // 8 rays with organic hand-drawn length variations matching Image 2
        val angles = listOf(0.0, 45.0, 90.0, 135.0, 180.0, 225.0, 270.0, 315.0)
        angles.forEachIndexed { i, deg ->
            val rad = Math.toRadians(deg)
            val len = if (i % 2 == 0) w * 0.46f else w * 0.36f
            val endX = cx + (len * Math.cos(rad)).toFloat()
            val endY = cy + (len * Math.sin(rad)).toFloat()
            drawLine(
                color = color,
                start = androidx.compose.ui.geometry.Offset(cx, cy),
                end = androidx.compose.ui.geometry.Offset(endX, endY),
                strokeWidth = strokeWidth,
                cap = androidx.compose.ui.graphics.StrokeCap.Round
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MainScreen(
    invoiceViewModel: InvoiceViewModel,
    isDarkMode: Boolean,
    onToggleDarkMode: (Boolean) -> Unit,
    isGridBackground: Boolean,
    onToggleGridBackground: (Boolean) -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val invoiceData by invoiceViewModel.invoiceState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    var splitRatio by remember { mutableFloatStateOf(0.5f) }
    var isGeneratingPdf by remember { mutableStateOf(false) }
    var isHistoryOpen by remember { mutableStateOf(false) }
    var activeInvoiceId by remember { mutableStateOf<String?>(null) }
    var autosaveStatus by remember { mutableStateOf("idle") }
    var isFirstLaunch by remember { mutableStateOf(true) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showResetConfirmDialog by remember { mutableStateOf(false) }

    // Restore cached autosave draft on launch if available and currently on default
    LaunchedEffect(Unit) {
        val cached = com.robogyaan.invoice.data.FirebaseFirestoreService.getCachedHistory(context)
        val draft = cached.find { it.id == "autosave_working_draft" }
        if (draft != null && invoiceData.invoiceNo == "RG/2024-25/084") {
            invoiceViewModel.setInvoiceData(draft.invoiceData)
            activeInvoiceId = "autosave_working_draft"
        }
    }

    // Debounced Autosave to Firestore without creating duplicate copies
    LaunchedEffect(invoiceData) {
        if (isFirstLaunch) {
            isFirstLaunch = false
            return@LaunchedEffect
        }
        autosaveStatus = "saving"
        kotlinx.coroutines.delay(1000)
        val savedId = com.robogyaan.invoice.data.FirebaseFirestoreService.autosaveDraft(
            context = context,
            invoiceData = invoiceData,
            activeId = activeInvoiceId
        )
        if (activeInvoiceId == null) {
            activeInvoiceId = savedId
        }
        autosaveStatus = "saved"
        kotlinx.coroutines.delay(2000)
        if (autosaveStatus == "saved") {
            autosaveStatus = "idle"
        }
    }

    fun exportAndSharePdf() {
        coroutineScope.launch {
            try {
                isGeneratingPdf = true
                val pdfFile: File = withContext(Dispatchers.IO) {
                    PdfGenerator.generateA4Pdf(context, invoiceData)
                }

                val contentUri: Uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    pdfFile
                )

                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "application/pdf"
                    putExtra(Intent.EXTRA_STREAM, contentUri)
                    putExtra(Intent.EXTRA_SUBJECT, "Robogyaan Invoice - ${invoiceData.invoiceNo}")
                    putExtra(Intent.EXTRA_TEXT, "Attached is the invoice ${invoiceData.invoiceNo} for ${invoiceData.billedTo.name}.")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                context.startActivity(Intent.createChooser(shareIntent, "Share Robogyaan Invoice PDF"))
            } catch (e: Exception) {
                Toast.makeText(context, "Error generating PDF: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                isGeneratingPdf = false
            }
        }
    }

    Scaffold(
        topBar = {
            val topBarBg = if (isDarkMode) Color.Black else NeoYellow
            val topBarTextColor = if (isDarkMode) Color.White else Color.Black
            val topBarBorder = if (isDarkMode) Color(0xFF27272A) else Color.Black

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(topBarBg)
                    .statusBarsPadding()
                    .drawBehind {
                        val strokeWidth = 2.dp.toPx()
                        val shadowHeight = if (isDarkMode) 0.dp.toPx() else 3.dp.toPx()
                        if (shadowHeight > 0f) {
                            drawRect(
                                color = Color.Black,
                                topLeft = Offset(0f, size.height),
                                size = Size(size.width, shadowHeight)
                            )
                        }
                        drawLine(
                            color = topBarBorder,
                            start = Offset(0f, size.height),
                            end = Offset(size.width, size.height),
                            strokeWidth = strokeWidth
                        )
                    }
                    .padding(start = 12.dp, end = 12.dp, top = 6.dp, bottom = 8.dp)
            ) {
                // Top Row: Heading on Top Left, Editor/Split/Preview & Theme Toggle on Top Right
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Logo + App Title (TOP LEFT)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(
                                    if (isDarkMode) NeoYellow else Color.Black,
                                    RoundedCornerShape(6.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "RG",
                                color = if (isDarkMode) Color.Black else NeoYellow,
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp,
                                fontFamily = VirgilFontFamily
                            )
                        }

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "ROBOGYAAN",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp,
                                    color = topBarTextColor,
                                    fontFamily = VirgilFontFamily,
                                    lineHeight = 15.sp
                                )
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (isDarkMode) NeoYellow else Color.Black,
                                            RoundedCornerShape(3.dp)
                                        )
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "INVOICE",
                                        color = if (isDarkMode) Color.Black else Color.White,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 8.sp,
                                        fontFamily = VirgilFontFamily
                                    )
                                }
                            }
                            Text(
                                text = "NEO-BRUTALIST LIVE ENGINE",
                                fontWeight = FontWeight.Bold,
                                fontSize = 8.sp,
                                color = topBarTextColor.copy(alpha = 0.8f),
                                fontFamily = VirgilFontFamily
                            )
                        }
                    }

                    // TOP RIGHT: Segmented control + Sun/Moon Toggle in SAME SPAN
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // EDITOR, SPLIT, PREVIEW segmented control
                        Row(
                            modifier = Modifier
                                .background(
                                    if (isDarkMode) Color(0xFF27272A) else Color.White,
                                    RoundedCornerShape(8.dp)
                                )
                                .border(
                                    2.dp,
                                    if (isDarkMode) Color(0xFF52525B) else Color.Black,
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(2.dp),
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Tab 0: Editor
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (selectedTab == 0) (if (isDarkMode) NeoYellow else Color.Black) else Color.Transparent,
                                        RoundedCornerShape(6.dp)
                                    )
                                    .clickable { selectedTab = 0 }
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Editor Tab",
                                    tint = if (selectedTab == 0) (if (isDarkMode) Color.Black else NeoYellow) else (if (isDarkMode) Color.White else Color.Black),
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            // Tab 1: Split
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (selectedTab == 1) (if (isDarkMode) NeoYellow else Color.Black) else Color.Transparent,
                                        RoundedCornerShape(6.dp)
                                    )
                                    .clickable { selectedTab = 1 }
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.VerticalSplit,
                                    contentDescription = "Split Tab",
                                    tint = if (selectedTab == 1) (if (isDarkMode) Color.Black else NeoYellow) else (if (isDarkMode) Color.White else Color.Black),
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            // Tab 2: Preview
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (selectedTab == 2) (if (isDarkMode) NeoYellow else Color.Black) else Color.Transparent,
                                        RoundedCornerShape(6.dp)
                                    )
                                    .clickable { selectedTab = 2 }
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Visibility,
                                    contentDescription = "Preview Tab",
                                    tint = if (selectedTab == 2) (if (isDarkMode) Color.Black else NeoYellow) else (if (isDarkMode) Color.White else Color.Black),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        // Sun / Moon toggle in the EXACT same span
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(
                                    if (isDarkMode) Color(0xFF27272A) else Color.White,
                                    RoundedCornerShape(8.dp)
                                )
                                .border(
                                    2.dp,
                                    if (isDarkMode) Color(0xFF52525B) else Color.Black,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { onToggleDarkMode(!isDarkMode) },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isDarkMode) {
                                Icon(
                                    Icons.Default.WbSunny,
                                    contentDescription = "Switch to Light Mode",
                                    tint = NeoYellow,
                                    modifier = Modifier.size(18.dp)
                                )
                            } else {
                                Icon(
                                    Icons.Default.Nightlight,
                                    contentDescription = "Switch to Dark Mode",
                                    tint = Color.Black,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        // App Settings Button in Navbar (User Request)
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(
                                    if (isDarkMode) Color(0xFF27272A) else Color.White,
                                    RoundedCornerShape(8.dp)
                                )
                                .border(
                                    2.dp,
                                    if (isDarkMode) Color(0xFF52525B) else Color.Black,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { showSettingsDialog = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Settings,
                                contentDescription = "App Settings & In-App Updater",
                                tint = if (isDarkMode) NeoYellow else Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Action Bar Below Navbar: 3-DASH BUTTON ON TOP LEFT BELOW NAVBAR & PDF Export + Settings + Logout on Right
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left side: History + Reset buttons grouped together
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // 3-DASH HISTORY BUTTON
                        Box(
                            modifier = Modifier
                                .neoBrutal(
                                    backgroundColor = if (isDarkMode) Color(0xFF27272A) else Color.White,
                                    borderColor = if (isDarkMode) Color(0xFF52525B) else Color.Black,
                                    shadowOffset = if (isDarkMode) 0.dp else 2.5.dp,
                                    cornerRadius = 6.dp
                                )
                                .clickable { isHistoryOpen = true }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                // 3 horizontal dashes icon
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(2.5.dp),
                                    modifier = Modifier.width(14.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(2.dp)
                                            .background(if (isDarkMode) Color.White else Color.Black, RoundedCornerShape(1.dp))
                                    )
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(2.dp)
                                            .background(if (isDarkMode) Color.White else Color.Black, RoundedCornerShape(1.dp))
                                    )
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(2.dp)
                                            .background(if (isDarkMode) Color.White else Color.Black, RoundedCornerShape(1.dp))
                                    )
                                }
                                Text(
                                    text = "History",
                                    fontFamily = VirgilFontFamily,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp,
                                    color = if (isDarkMode) Color.White else NeoBlack
                                )
                            }
                        }

                        // Reset Button (Phone/Android only)
                        Box(
                            modifier = Modifier
                                .neoBrutal(
                                    backgroundColor = if (isDarkMode) Color(0xFF27272A) else Color.White,
                                    borderColor = if (isDarkMode) Color(0xFF52525B) else Color.Black,
                                    shadowOffset = if (isDarkMode) 0.dp else 2.5.dp,
                                    cornerRadius = 6.dp
                                )
                                .clickable { showResetConfirmDialog = true }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Icon(
                                    Icons.Default.Refresh,
                                    contentDescription = "Reset Invoice",
                                    tint = if (isDarkMode) Color.White else Color.Black,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = "Reset",
                                    fontFamily = VirgilFontFamily,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp,
                                    color = if (isDarkMode) Color.White else NeoBlack
                                )
                            }
                        }
                    }

                    // Green Autosave Indicator in Virgil Font
                    Box(
                        modifier = Modifier.padding(horizontal = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (autosaveStatus == "saving") {
                            Text(
                                text = "Saving....",
                                color = Color(0xFF16A34A),
                                fontFamily = VirgilFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        } else if (autosaveStatus == "saved") {
                            Text(
                                text = "✓ Saved",
                                color = Color(0xFF16A34A).copy(alpha = 0.85f),
                                fontFamily = VirgilFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }
                    }

                    // Right Actions: Export PDF, Settings (with sticker), & Logout
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        NeoBrutalButton(
                            text = if (isGeneratingPdf) "Exporting..." else "Export PDF",
                            onClick = { exportAndSharePdf() },
                            enabled = !isGeneratingPdf,
                            backgroundColor = if (isDarkMode) NeoYellow else Color.Black,
                            contentColor = if (isDarkMode) Color.Black else Color.White,
                            icon = {
                                Icon(
                                    Icons.Default.Share,
                                    contentDescription = "Share PDF",
                                    tint = if (isDarkMode) Color.Black else NeoYellow,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        )

                        // Logout button
                        Box(
                            modifier = Modifier
                                .neoBrutal(
                                    backgroundColor = if (isDarkMode) Color(0xFF27272A) else Color.White,
                                    borderColor = if (isDarkMode) Color(0xFF52525B) else Color.Black,
                                    shadowOffset = if (isDarkMode) 0.dp else 2.dp,
                                    cornerRadius = 6.dp
                                )
                                .clickable { showLogoutDialog = true }
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.ExitToApp,
                                contentDescription = "Logout",
                                tint = if (isDarkMode) Color.White else Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Interactive/Composable Box Grid Background (Image 1)
            BoxGridBackground(isDarkMode = isDarkMode, enabled = isGridBackground)

            when (selectedTab) {
                0 -> InvoiceEditorScreen(
                    invoiceData = invoiceData,
                    viewModel = invoiceViewModel
                )
                1 -> {
                    val isKeyboardOpen = WindowInsets.isImeVisible

                    if (isKeyboardOpen) {
                        // Keyboard open → full-screen editor only, no PDF preview
                        InvoiceEditorScreen(
                            invoiceData = invoiceData,
                            viewModel = invoiceViewModel,
                            modifier = Modifier
                                .fillMaxSize()
                                .imePadding()
                        )
                    } else {
                        // Normal split: editor on top, yellow handle bar, preview below
                        val editorFraction = splitRatio.coerceIn(0.25f, 0.75f)
                        val previewFraction = 1f - editorFraction
                        var containerHeightPx by remember { mutableFloatStateOf(1800f) }

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .onSizeChanged { containerHeightPx = it.height.toFloat() }
                        ) {
                            // TOP: Editor panel
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(editorFraction)
                            ) {
                                InvoiceEditorScreen(
                                    invoiceData = invoiceData,
                                    viewModel = invoiceViewModel,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            // MIDDLE: Yellow handle divider — drag to resize, tap to cycle presets
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(28.dp)
                                    .pointerInput(containerHeightPx) {
                                        detectVerticalDragGestures { change, dragAmount ->
                                            change.consume()
                                            if (containerHeightPx > 0f) {
                                                splitRatio = (splitRatio + dragAmount / containerHeightPx)
                                                    .coerceIn(0.25f, 0.75f)
                                            }
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                // Full-width line
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(2.dp)
                                        .background(if (isDarkMode) Color(0xFF52525B) else Color.Black)
                                )

                                // Yellow neo-brutal handle — tap to cycle 50:50 → 25:75 → 75:25
                                Box(
                                    modifier = Modifier
                                        .neoBrutal(
                                            backgroundColor = NeoYellow,
                                            borderColor = Color.Black,
                                            borderWidth = 2.dp,
                                            shadowOffset = 2.dp,
                                            cornerRadius = 6.dp
                                        )
                                        .clickable {
                                            splitRatio = when {
                                                kotlin.math.abs(splitRatio - 0.50f) < 0.10f -> 0.25f
                                                kotlin.math.abs(splitRatio - 0.25f) < 0.10f -> 0.75f
                                                else -> 0.50f
                                            }
                                        }
                                        .padding(horizontal = 12.dp, vertical = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .width(10.dp)
                                                .height(2.5.dp)
                                                .background(Color.Black, RoundedCornerShape(2.dp))
                                        )
                                        Text(
                                            text = "${(editorFraction * 100).toInt()}:${(previewFraction * 100).toInt()}",
                                            fontFamily = VirgilFontFamily,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 11.sp,
                                            color = Color.Black
                                        )
                                        Box(
                                            modifier = Modifier
                                                .width(10.dp)
                                                .height(2.5.dp)
                                                .background(Color.Black, RoundedCornerShape(2.dp))
                                        )
                                    }
                                }
                            }

                            // BOTTOM: PDF Preview — fills remaining space
                            Box(modifier = Modifier.fillMaxSize()) {
                                InvoicePreviewScreen(invoiceData = invoiceData)
                            }
                        }
                    }
                }
                2 -> InvoicePreviewScreen(
                    invoiceData = invoiceData
                )
            }

            // FIRESTORE PROMPT HISTORY SIDEBAR SHEET
            HistorySidebarSheet(
                isOpen = isHistoryOpen,
                onClose = { isHistoryOpen = false },
                currentInvoice = invoiceData,
                onLoadInvoice = { loaded, id ->
                    invoiceViewModel.setInvoiceData(loaded)
                    activeInvoiceId = id
                },
                activeInvoiceId = activeInvoiceId
            )

            // NEO-BRUTALIST LOGOUT ALERT DIALOG
            NeoBrutalAlertDialog(
                isOpen = showLogoutDialog,
                onDismiss = { showLogoutDialog = false },
                onConfirm = onLogout,
                title = "Log Out Session",
                message = "Are you sure you want to log out of the admin session?",
                confirmText = "Log Out",
                cancelText = "Stay Logged In",
                isDanger = true
            )

            // APP SETTINGS & IN-APP UPDATER DIALOG (Image 2)
            SettingsDialog(
                isOpen = showSettingsDialog,
                onDismiss = { showSettingsDialog = false },
                isDarkMode = isDarkMode,
                onToggleDarkMode = onToggleDarkMode,
                isGridBackground = isGridBackground,
                onToggleGridBackground = onToggleGridBackground
            )

            // RESET INVOICE CONFIRM DIALOG (Phone/Android only)
            NeoBrutalAlertDialog(
                isOpen = showResetConfirmDialog,
                onDismiss = { showResetConfirmDialog = false },
                onConfirm = {
                    invoiceViewModel.resetToDefaults()
                    showResetConfirmDialog = false
                },
                title = "Reset Invoice",
                message = "This will clear all current invoice data and reset it back to the default template. Are you sure?",
                confirmText = "Reset",
                cancelText = "Cancel",
                isDanger = true
            )
        }
    }
}
