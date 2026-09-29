package com.robogyaan.invoice

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VerticalSplit
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.robogyaan.invoice.ui.InvoiceViewModel
import com.robogyaan.invoice.ui.components.AuthPreferences
import com.robogyaan.invoice.ui.components.HistorySidebarSheet
import com.robogyaan.invoice.ui.components.InvoiceEditorScreen
import com.robogyaan.invoice.ui.components.LoginScreen
import com.robogyaan.invoice.ui.components.NeoBrutalButton
import com.robogyaan.invoice.ui.neoBrutal
import com.robogyaan.invoice.ui.preview.InvoicePreviewScreen
import com.robogyaan.invoice.ui.theme.NeoBlack
import com.robogyaan.invoice.ui.theme.NeoYellow
import com.robogyaan.invoice.ui.theme.RoboGyaanInvoiceTheme
import com.robogyaan.invoice.ui.theme.VirgilFontFamily
import com.robogyaan.invoice.util.PdfGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class MainActivity : ComponentActivity() {

    private val invoiceViewModel: InvoiceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RoboGyaanInvoiceTheme {
                val context = LocalContext.current
                var isLoggedIn by remember { mutableStateOf(AuthPreferences.isLoggedIn(context)) }

                if (!isLoggedIn) {
                    LoginScreen(onLoginSuccess = { isLoggedIn = true })
                } else {
                    MainScreen(
                        invoiceViewModel = invoiceViewModel,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    invoiceViewModel: InvoiceViewModel,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val invoiceData by invoiceViewModel.invoiceState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    var isGeneratingPdf by remember { mutableStateOf(false) }
    var isHistoryOpen by remember { mutableStateOf(false) }
    var activeInvoiceId by remember { mutableStateOf<String?>(null) }
    var autosaveStatus by remember { mutableStateOf("idle") }
    var isFirstLaunch by remember { mutableStateOf(true) }

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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NeoYellow)
                    .neoBrutal(
                        backgroundColor = NeoYellow,
                        shadowOffset = 3.dp,
                        cornerRadius = 0.dp
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                // Top Row: Heading on Top Left, Editor/Split/Preview on Top Right
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
                                .background(Color.Black, RoundedCornerShape(6.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "RG",
                                color = NeoYellow,
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
                                    color = Color.Black,
                                    fontFamily = VirgilFontFamily,
                                    lineHeight = 15.sp
                                )
                                Box(
                                    modifier = Modifier
                                        .background(Color.Black, RoundedCornerShape(3.dp))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "INVOICE",
                                        color = Color.White,
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
                                color = Color.Black.copy(alpha = 0.8f),
                                fontFamily = VirgilFontFamily
                            )
                        }
                    }

                    // TOP RIGHT: EDITOR, SPLIT, PREVIEW segmented control (ONLY ICONS ON PHONE SCREEN)
                    Row(
                        modifier = Modifier
                            .background(Color.White, RoundedCornerShape(8.dp))
                            .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                            .padding(2.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Tab 0: Editor (Icon Only)
                        Box(
                            modifier = Modifier
                                .background(
                                    if (selectedTab == 0) Color.Black else Color.Transparent,
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable { selectedTab = 0 }
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Editor Tab",
                                tint = if (selectedTab == 0) NeoYellow else Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        // Tab 1: Split (Icon Only)
                        Box(
                            modifier = Modifier
                                .background(
                                    if (selectedTab == 1) Color.Black else Color.Transparent,
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable { selectedTab = 1 }
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.VerticalSplit,
                                contentDescription = "Split Tab",
                                tint = if (selectedTab == 1) NeoYellow else Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        // Tab 2: Preview (Icon Only)
                        Box(
                            modifier = Modifier
                                .background(
                                    if (selectedTab == 2) Color.Black else Color.Transparent,
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable { selectedTab = 2 }
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Visibility,
                                contentDescription = "Preview Tab",
                                tint = if (selectedTab == 2) NeoYellow else Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Action Bar Below Navbar: 3-DASH BUTTON ON TOP LEFT BELOW NAVBAR & PDF Export + Logout on Right
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 3-DASH PROMPT HISTORY BUTTON ON LEFT TOP BELOW NAVBAR
                    Box(
                        modifier = Modifier
                            .neoBrutal(
                                backgroundColor = Color.White,
                                shadowOffset = 2.5.dp,
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
                                        .background(Color.Black, RoundedCornerShape(1.dp))
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(2.dp)
                                        .background(Color.Black, RoundedCornerShape(1.dp))
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(2.dp)
                                        .background(Color.Black, RoundedCornerShape(1.dp))
                                )
                            }
                            Text(
                                text = "Prompt History",
                                fontFamily = VirgilFontFamily,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp,
                                color = NeoBlack
                            )
                            Box(
                                modifier = Modifier
                                    .background(Color.Black, RoundedCornerShape(3.dp))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "Firestore",
                                    color = NeoYellow,
                                    fontFamily = VirgilFontFamily,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 8.sp
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

                    // Right Actions: Export PDF & Logout
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        NeoBrutalButton(
                            text = if (isGeneratingPdf) "Exporting..." else "Export PDF",
                            onClick = { exportAndSharePdf() },
                            enabled = !isGeneratingPdf,
                            backgroundColor = Color.Black,
                            contentColor = Color.White,
                            icon = {
                                Icon(
                                    Icons.Default.Share,
                                    contentDescription = "Share PDF",
                                    tint = NeoYellow,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        )

                        // Logout button
                        Box(
                            modifier = Modifier
                                .neoBrutal(
                                    backgroundColor = Color.White,
                                    shadowOffset = 2.dp,
                                    cornerRadius = 6.dp
                                )
                                .clickable { onLogout() }
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.ExitToApp,
                                contentDescription = "Logout",
                                tint = Color.Black,
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
            when (selectedTab) {
                0 -> InvoiceEditorScreen(
                    invoiceData = invoiceData,
                    viewModel = invoiceViewModel
                )
                1 -> Column(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.weight(1f)) {
                        InvoiceEditorScreen(
                            invoiceData = invoiceData,
                            viewModel = invoiceViewModel
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .background(Color.Black)
                    )
                    Box(modifier = Modifier.weight(1f)) {
                        InvoicePreviewScreen(
                            invoiceData = invoiceData
                        )
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
        }
    }
}
