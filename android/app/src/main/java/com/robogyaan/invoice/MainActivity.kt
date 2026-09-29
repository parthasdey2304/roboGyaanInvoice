package com.robogyaan.invoice

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
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
import com.robogyaan.invoice.ui.components.InvoiceEditorScreen
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
                MainScreen(invoiceViewModel = invoiceViewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(invoiceViewModel: InvoiceViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val invoiceData by invoiceViewModel.invoiceState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    var isGeneratingPdf by remember { mutableStateOf(false) }

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
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Logo + App Title
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color.Black, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "RG",
                                color = NeoYellow,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                fontFamily = VirgilFontFamily
                            )
                        }

                        Column {
                            Text(
                                text = "ROBOGYAAN",
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                color = Color.Black,
                                fontFamily = VirgilFontFamily,
                                lineHeight = 18.sp
                            )
                            Text(
                                text = "INVOICE GENERATOR",
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = Color.Black.copy(alpha = 0.8f),
                                fontFamily = VirgilFontFamily
                            )
                        }
                    }

                    // PDF Export & Share Button
                    NeoBrutalButton(
                        text = if (isGeneratingPdf) "Exporting..." else "PDF",
                        onClick = { exportAndSharePdf() },
                        enabled = !isGeneratingPdf,
                        backgroundColor = Color.Black,
                        contentColor = Color.White,
                        icon = {
                            Icon(
                                Icons.Default.Share,
                                contentDescription = "Share PDF",
                                tint = NeoYellow,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Tab Switcher (Editor vs Preview)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .neoBrutal(
                                backgroundColor = if (selectedTab == 0) Color.Black else Color.White,
                                shadowOffset = if (selectedTab == 0) 2.dp else 1.dp,
                                cornerRadius = 6.dp
                            )
                            .clickable { selectedTab = 0 }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Editor Tab",
                                tint = if (selectedTab == 0) NeoYellow else Color.Black,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "EDITOR FORM",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = VirgilFontFamily,
                                color = if (selectedTab == 0) Color.White else Color.Black
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .neoBrutal(
                                backgroundColor = if (selectedTab == 1) Color.Black else Color.White,
                                shadowOffset = if (selectedTab == 1) 2.dp else 1.dp,
                                cornerRadius = 6.dp
                            )
                            .clickable { selectedTab = 1 }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Default.Visibility,
                                contentDescription = "Preview Tab",
                                tint = if (selectedTab == 1) NeoYellow else Color.Black,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "A4 PREVIEW",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = VirgilFontFamily,
                                color = if (selectedTab == 1) Color.White else Color.Black
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
                1 -> InvoicePreviewScreen(
                    invoiceData = invoiceData
                )
            }
        }
    }
}
