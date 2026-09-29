package com.robogyaan.invoice.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.robogyaan.invoice.data.FirebaseFirestoreService
import com.robogyaan.invoice.data.InvoiceData
import com.robogyaan.invoice.data.InvoiceHistoryItem
import com.robogyaan.invoice.ui.neoBrutal
import com.robogyaan.invoice.ui.theme.NeoBlack
import com.robogyaan.invoice.ui.theme.NeoYellow
import com.robogyaan.invoice.ui.theme.PoppinsFontFamily
import com.robogyaan.invoice.ui.theme.VirgilFontFamily
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorySidebarSheet(
    isOpen: Boolean,
    onClose: () -> Unit,
    currentInvoice: InvoiceData,
    onLoadInvoice: (InvoiceData, String) -> Unit,
    activeInvoiceId: String? = null
) {
    if (!isOpen) return

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var promptInput by remember { mutableStateOf("") }
    var historyItems by remember { mutableStateOf<List<InvoiceHistoryItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }
    var updatingId by remember { mutableStateOf<String?>(null) }

    fun refreshHistory() {
        coroutineScope.launch {
            isLoading = true
            val items = FirebaseFirestoreService.fetchHistory(context)
            historyItems = items
            isLoading = false
        }
    }

    LaunchedEffect(isOpen) {
        refreshHistory()
    }

    ModalBottomSheet(
        onDismissRequest = onClose,
        sheetState = sheetState,
        containerColor = Color(0xFFFDFBF7),
        tonalElevation = 8.dp,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .width(48.dp)
                    .height(5.dp)
                    .background(Color.Black, RoundedCornerShape(3.dp))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            // Header: Title + Firestore Badge + Close Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "PROMPT HISTORY",
                        fontFamily = VirgilFontFamily,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = NeoBlack
                    )
                    Box(
                        modifier = Modifier
                            .background(NeoYellow, RoundedCornerShape(4.dp))
                            .border(1.5.dp, Color.Black, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "FIRESTORE",
                            color = Color.Black,
                            fontFamily = VirgilFontFamily,
                            fontWeight = FontWeight.Black,
                            fontSize = 9.sp
                        )
                    }
                }

                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color.White, RoundedCornerShape(6.dp))
                        .border(1.5.dp, Color.Black, RoundedCornerShape(6.dp))
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Close History",
                        tint = Color.Black,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Save / Update Current Invoice Card
            NeoBrutalCard(
                backgroundColor = Color(0xFFFFFBEB),
                borderColor = Color.Black,
                borderWidth = 2.dp,
                shadowOffset = 3.dp,
                cornerRadius = 8.dp,
                title = if (activeInvoiceId != null) "Update or Save Invoice" else "Save Current Invoice",
                badge = "Cloud Sync"
            ) {
                // If an invoice is currently loaded in the editor, show direct "Update Existing" option
                if (activeInvoiceId != null) {
                    NeoBrutalButton(
                        text = if (updatingId == activeInvoiceId) "Updating Firestore..." else "Update Active in Firestore",
                        onClick = {
                            updatingId = activeInvoiceId
                            coroutineScope.launch {
                                val item = historyItems.find { it.id == activeInvoiceId }
                                val desc = if (promptInput.trim().isNotEmpty()) promptInput.trim() else item?.promptDescription ?: "RoboGyaan Invoice"
                                FirebaseFirestoreService.updatePrompt(context, activeInvoiceId, currentInvoice, desc)
                                updatingId = null
                                Toast.makeText(context, "Updated invoice in Firestore!", Toast.LENGTH_SHORT).show()
                                refreshHistory()
                            }
                        },
                        enabled = updatingId != activeInvoiceId,
                        backgroundColor = Color.Black,
                        contentColor = NeoYellow,
                        modifier = Modifier.fillMaxWidth(),
                        icon = {
                            Icon(
                                Icons.Default.Save,
                                contentDescription = "Update",
                                tint = NeoYellow,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                NeoBrutalTextField(
                    value = promptInput,
                    onValueChange = { promptInput = it },
                    label = "Prompt / Description Note",
                    placeholder = "e.g. RoboGyaan Robotics Workshop at School..."
                )

                Spacer(modifier = Modifier.height(10.dp))

                NeoBrutalButton(
                    text = if (isSaving) "Saving to Firestore..." else "Save as New Prompt",
                    onClick = {
                        val desc = if (promptInput.trim().isEmpty()) {
                            "RoboGyaan Invoice - ${currentInvoice.invoiceNo}"
                        } else {
                            promptInput.trim()
                        }
                        isSaving = true
                        coroutineScope.launch {
                            FirebaseFirestoreService.savePrompt(context, currentInvoice, desc)
                            isSaving = false
                            promptInput = ""
                            Toast.makeText(context, "Saved to Firestore History!", Toast.LENGTH_SHORT).show()
                            refreshHistory()
                        }
                    },
                    enabled = !isSaving,
                    backgroundColor = NeoYellow,
                    contentColor = Color.Black,
                    modifier = Modifier.fillMaxWidth(),
                    icon = {
                        Icon(
                            Icons.Default.CloudUpload,
                            contentDescription = "Save",
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Subtitle & Item Count
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SAVED INVOICES & PROMPTS",
                    fontFamily = VirgilFontFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    color = NeoBlack
                )
                Text(
                    text = "${historyItems.size} items",
                    fontFamily = VirgilFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.Black)
                }
            } else if (historyItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No saved invoices in history yet.\nSave your first invoice above!",
                        fontFamily = VirgilFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(vertical = 6.dp)
                ) {
                    items(historyItems, key = { it.id }) { item ->
                        val isActive = item.id == activeInvoiceId
                        NeoBrutalCard(
                            backgroundColor = if (isActive) Color(0xFFFFFDE6) else Color.White,
                            borderColor = Color.Black,
                            borderWidth = 2.dp,
                            shadowOffset = 3.dp,
                            cornerRadius = 8.dp
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                // Top row: Prompt description + Invoice No Badge
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.promptDescription,
                                            fontFamily = VirgilFontFamily,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 14.sp,
                                            color = NeoBlack
                                        )
                                        if (isActive) {
                                            Text(
                                                text = "ACTIVE IN EDITOR",
                                                fontFamily = VirgilFontFamily,
                                                fontWeight = FontWeight.Black,
                                                fontSize = 9.sp,
                                                color = Color(0xFF047857),
                                                modifier = Modifier.padding(top = 2.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .background(NeoYellow, RoundedCornerShape(4.dp))
                                            .border(1.dp, Color.Black, RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = item.invoiceNo,
                                            fontFamily = PoppinsFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp,
                                            color = Color.Black
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                // Client name & Total Amount
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = item.clientName,
                                        fontFamily = PoppinsFontFamily,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 12.sp,
                                        color = Color.DarkGray
                                    )
                                    Text(
                                        text = "₹${String.format(java.util.Locale.US, "%,.2f", item.totalAmount)}",
                                        fontFamily = VirgilFontFamily,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 14.sp,
                                        color = Color.Black
                                    )
                                }

                                if (item.createdAt.isNotEmpty()) {
                                    Text(
                                        text = item.createdAt,
                                        fontFamily = PoppinsFontFamily,
                                        fontSize = 10.sp,
                                        color = Color.Gray,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Actions: Load Invoice, Modify & Delete
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    // LOAD INVOICE BUTTON
                                    Box(
                                        modifier = Modifier
                                            .weight(1.3f)
                                            .neoBrutal(
                                                backgroundColor = Color.Black,
                                                shadowOffset = 2.dp,
                                                cornerRadius = 6.dp
                                            )
                                            .clickable {
                                                onLoadInvoice(item.invoiceData, item.id)
                                                Toast.makeText(context, "Loaded ${item.invoiceNo}", Toast.LENGTH_SHORT).show()
                                                onClose()
                                            }
                                            .padding(vertical = 7.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.FileDownload,
                                                contentDescription = "Load",
                                                tint = NeoYellow,
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Text(
                                                text = "LOAD",
                                                color = Color.White,
                                                fontFamily = VirgilFontFamily,
                                                fontWeight = FontWeight.Black,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }

                                    // MODIFY / UPDATE BUTTON
                                    Box(
                                        modifier = Modifier
                                            .weight(1.3f)
                                            .neoBrutal(
                                                backgroundColor = NeoYellow,
                                                shadowOffset = 2.dp,
                                                cornerRadius = 6.dp
                                            )
                                            .clickable {
                                                updatingId = item.id
                                                coroutineScope.launch {
                                                    FirebaseFirestoreService.updatePrompt(
                                                        context,
                                                        item.id,
                                                        currentInvoice,
                                                        item.promptDescription
                                                    )
                                                    updatingId = null
                                                    Toast.makeText(context, "Updated with current editor!", Toast.LENGTH_SHORT).show()
                                                    refreshHistory()
                                                }
                                            }
                                            .padding(vertical = 7.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Edit,
                                                contentDescription = "Modify",
                                                tint = Color.Black,
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Text(
                                                text = if (updatingId == item.id) "..." else "MODIFY",
                                                color = Color.Black,
                                                fontFamily = VirgilFontFamily,
                                                fontWeight = FontWeight.Black,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }

                                    // DELETE BUTTON
                                    Box(
                                        modifier = Modifier
                                            .neoBrutal(
                                                backgroundColor = Color(0xFFFFEBEB),
                                                shadowOffset = 2.dp,
                                                cornerRadius = 6.dp
                                            )
                                            .clickable {
                                                coroutineScope.launch {
                                                    FirebaseFirestoreService.deletePrompt(context, item.id)
                                                    refreshHistory()
                                                    Toast.makeText(context, "Deleted from history", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                            .padding(horizontal = 10.dp, vertical = 7.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = Color.Red,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
