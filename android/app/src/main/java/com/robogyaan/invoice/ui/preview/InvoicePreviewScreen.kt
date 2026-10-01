package com.robogyaan.invoice.ui.preview

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.robogyaan.invoice.R
import com.robogyaan.invoice.data.InvoiceData
import com.robogyaan.invoice.data.InvoicePageSlice
import com.robogyaan.invoice.data.paginateInvoiceItems
import com.robogyaan.invoice.ui.neoBrutal
import com.robogyaan.invoice.ui.neoBrutalClickable
import com.robogyaan.invoice.ui.theme.NeoBlack
import com.robogyaan.invoice.ui.theme.NeoOrange
import com.robogyaan.invoice.ui.theme.NeoYellow
import com.robogyaan.invoice.ui.theme.PoppinsFontFamily
import com.robogyaan.invoice.ui.theme.VirgilFontFamily
import com.robogyaan.invoice.util.NumberToWordsIndian

@Composable
fun InvoicePreviewScreen(
    invoiceData: InvoiceData,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val totalAmount = invoiceData.totalAmount
    val amountInWords = NumberToWordsIndian.convert(totalAmount)
    val pages = remember(invoiceData.items) { paginateInvoiceItems(invoiceData.items) }

    // -1 represents "All Pages"
    var selectedPageIndex by remember { mutableStateOf(-1) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    if (selectedPageIndex >= pages.size) {
        selectedPageIndex = -1
    }

    val displayPages = if (selectedPageIndex == -1) pages else listOf(pages[selectedPageIndex])

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFE5E7EB))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        // TOP CONTROL BAR: TITLE + PAGE SELECTOR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp, top = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Live A4 Preview",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = VirgilFontFamily,
                    color = NeoBlack
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Color(0xFF10B981), RoundedCornerShape(4.dp))
                )
            }

            // Dropdown Selector Button
            Box {
                Row(
                    modifier = Modifier
                        .neoBrutalClickable(
                            backgroundColor = Color.White,
                            borderWidth = 1.5.dp,
                            defaultShadowOffset = 2.dp,
                            cornerRadius = 6.dp,
                            onClick = { dropdownExpanded = true }
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val label = if (selectedPageIndex == -1) {
                        "All Pages (${pages.size})"
                    } else {
                        "Page ${selectedPageIndex + 1} of ${pages.size}"
                    }
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PoppinsFontFamily,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "▼",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black
                    )
                }

                DropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false },
                    modifier = Modifier
                        .background(Color.White)
                        .border(1.5.dp, Color.Black, RoundedCornerShape(6.dp))
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                "All Pages (${pages.size})",
                                fontWeight = if (selectedPageIndex == -1) FontWeight.Black else FontWeight.Normal,
                                fontFamily = PoppinsFontFamily,
                                fontSize = 12.sp
                            )
                        },
                        onClick = {
                            selectedPageIndex = -1
                            dropdownExpanded = false
                        }
                    )
                    pages.forEachIndexed { idx, _ ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "Page ${idx + 1} of ${pages.size}",
                                    fontWeight = if (selectedPageIndex == idx) FontWeight.Black else FontWeight.Normal,
                                    fontFamily = PoppinsFontFamily,
                                    fontSize = 12.sp
                                )
                            },
                            onClick = {
                                selectedPageIndex = idx
                                dropdownExpanded = false
                            }
                        )
                    }
                }
            }
        }

        // SCROLLABLE CONTAINER OF A4 SHEETS
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            displayPages.forEach { pageSlice ->
                InvoiceSheetCard(
                    pageSlice = pageSlice,
                    invoiceData = invoiceData,
                    totalAmount = totalAmount,
                    amountInWords = amountInWords
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun InvoiceSheetCard(
    pageSlice: InvoicePageSlice,
    invoiceData: InvoiceData,
    totalAmount: Double,
    amountInWords: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, Color.Black, RoundedCornerShape(16.dp))
            .background(Color.White, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        // 1. TOP HEADER SECTION
        if (pageSlice.isFirstPage) {
            // Full First-Page Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Left Logo + Brand
                Column(horizontalAlignment = Alignment.Start) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_robogyaan_logo),
                        contentDescription = "Robogyaan Logo",
                        modifier = Modifier.height(44.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                // Right Geometric Blocks
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.width(190.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black, RoundedCornerShape(topStart = 16.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "Invoice No. : ${invoiceData.invoiceNo}",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = PoppinsFontFamily,
                            textAlign = TextAlign.End,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Box(
                        modifier = Modifier
                            .width(160.dp)
                            .background(NeoOrange, RoundedCornerShape(topStart = 12.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "Registration No. - ${invoiceData.registrationNo}",
                            color = Color.Black,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = PoppinsFontFamily,
                            textAlign = TextAlign.End,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = Color(0xFFD1D5DB), thickness = 1.dp)
            Spacer(modifier = Modifier.height(10.dp))

            // Sender & Recipient (BILL TO / FROM)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // BILL To
                Column(modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFE5E7EB), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "BILL To:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = PoppinsFontFamily,
                            color = Color.Black,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = invoiceData.billedTo.name,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PoppinsFontFamily,
                        color = NeoBlack
                    )
                    Text(
                        text = invoiceData.billedTo.address,
                        fontSize = 11.sp,
                        fontFamily = PoppinsFontFamily,
                        color = Color.DarkGray
                    )
                    Text(
                        text = invoiceData.billedTo.pinState,
                        fontSize = 11.sp,
                        fontFamily = PoppinsFontFamily,
                        color = Color.DarkGray
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // From
                Column(modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFE5E7EB), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "From:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = PoppinsFontFamily,
                            color = Color.Black,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = invoiceData.from.company,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PoppinsFontFamily,
                        color = NeoBlack
                    )
                    Text(
                        text = invoiceData.from.address,
                        fontSize = 11.sp,
                        fontFamily = PoppinsFontFamily,
                        color = Color.DarkGray
                    )
                    Text(
                        text = invoiceData.from.cityPinState,
                        fontSize = 11.sp,
                        fontFamily = PoppinsFontFamily,
                        color = Color.DarkGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Meta Summary Bar (4 Columns)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFFFFB800), RoundedCornerShape(2.dp))
                        .padding(vertical = 4.dp, horizontal = 2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Invoice No.", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = PoppinsFontFamily)
                    Text(invoiceData.invoiceNo, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Black, fontFamily = PoppinsFontFamily, maxLines = 1)
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFFFFB800), RoundedCornerShape(2.dp))
                        .padding(vertical = 4.dp, horizontal = 2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Issue Date:", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = PoppinsFontFamily)
                    Text(invoiceData.issueDate, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Black, fontFamily = PoppinsFontFamily)
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFFFFB800), RoundedCornerShape(2.dp))
                        .padding(vertical = 4.dp, horizontal = 2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Due Date:", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = PoppinsFontFamily)
                    Text(invoiceData.dueDate, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Black, fontFamily = PoppinsFontFamily)
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFF505050), RoundedCornerShape(2.dp))
                        .padding(vertical = 4.dp, horizontal = 2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Total Due :", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = PoppinsFontFamily)
                    Text("₹ ${NumberToWordsIndian.formatINR(totalAmount)}/-", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Black, fontFamily = PoppinsFontFamily)
                }
            }
        } else {
            // Continuation Page Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Left Logo + Brand
                Column(horizontalAlignment = Alignment.Start) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_robogyaan_logo),
                        contentDescription = "Robogyaan Logo",
                        modifier = Modifier.height(44.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                // Right Geometric Blocks (matching Page 1 and user's sketch)
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.width(190.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black, RoundedCornerShape(topStart = 16.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "Invoice No. : ${invoiceData.invoiceNo}",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = PoppinsFontFamily,
                            textAlign = TextAlign.End,
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 1,
                            softWrap = false
                        )
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Box(
                        modifier = Modifier
                            .width(160.dp)
                            .background(NeoOrange, RoundedCornerShape(topStart = 12.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "PAGE ${pageSlice.pageNumber} OF ${pageSlice.totalPages}",
                            color = Color.Black,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = PoppinsFontFamily,
                            textAlign = TextAlign.End,
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 1,
                            softWrap = false
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = invoiceData.billedTo.name,
                        color = Color.DarkGray,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PoppinsFontFamily,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = Color(0xFFD1D5DB), thickness = 1.dp)
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2. ITEMIZED BILLING TABLE
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, Color.Black)
        ) {
            // Watermark
            if (invoiceData.showWatermark) {
                Image(
                    painter = painterResource(id = R.drawable.ic_robogyaan_symbol),
                    contentDescription = "Watermark",
                    modifier = Modifier
                        .size(130.dp)
                        .align(Alignment.Center)
                        .rotate(-25f)
                        .graphicsLayer(alpha = 0.08f)
                )
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                // Header row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                ) {
                    Text("Item", modifier = Modifier.weight(2f).padding(vertical = 6.dp, horizontal = 4.dp), fontSize = 10.sp, fontWeight = FontWeight.Black, fontFamily = PoppinsFontFamily, textAlign = TextAlign.Center)
                    Box(modifier = Modifier.width(1.5.dp).fillMaxHeight().background(Color.Black))
                    Text("Amount/\nStudent head", modifier = Modifier.weight(1.3f).padding(vertical = 4.dp, horizontal = 2.dp), fontSize = 9.sp, fontWeight = FontWeight.Black, fontFamily = PoppinsFontFamily, textAlign = TextAlign.Center)
                    Box(modifier = Modifier.width(1.5.dp).fillMaxHeight().background(Color.Black))
                    Text("No. of\nStudents", modifier = Modifier.weight(1.1f).padding(vertical = 4.dp, horizontal = 2.dp), fontSize = 9.sp, fontWeight = FontWeight.Black, fontFamily = PoppinsFontFamily, textAlign = TextAlign.Center)
                    Box(modifier = Modifier.width(1.5.dp).fillMaxHeight().background(Color.Black))
                    Text("Total Amount", modifier = Modifier.weight(1.4f).padding(vertical = 6.dp, horizontal = 4.dp), fontSize = 10.sp, fontWeight = FontWeight.Black, fontFamily = PoppinsFontFamily, textAlign = TextAlign.Center)
                }

                Box(modifier = Modifier.fillMaxWidth().height(1.5.dp).background(Color.Black))

                // Page-specific Item Rows
                pageSlice.items.forEachIndexed { idx, item ->
                    val absoluteIndex = pageSlice.itemStartIndex + idx + 1
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min)
                    ) {
                        Text(
                            text = "$absoluteIndex. ${item.description}",
                            modifier = Modifier.weight(2f).padding(6.dp),
                            fontSize = 10.sp,
                            fontFamily = PoppinsFontFamily
                        )
                        Box(modifier = Modifier.width(1.5.dp).fillMaxHeight().background(Color.Black))
                        Text(
                            text = "₹${NumberToWordsIndian.formatINR(item.amountPerHead)}",
                            modifier = Modifier.weight(1.3f).padding(6.dp),
                            fontSize = 10.sp,
                            fontFamily = PoppinsFontFamily,
                            textAlign = TextAlign.Center
                        )
                        Box(modifier = Modifier.width(1.5.dp).fillMaxHeight().background(Color.Black))
                        Text(
                            text = "${item.studentCount}",
                            modifier = Modifier.weight(1.1f).padding(6.dp),
                            fontSize = 10.sp,
                            fontFamily = PoppinsFontFamily,
                            textAlign = TextAlign.Center
                        )
                        Box(modifier = Modifier.width(1.5.dp).fillMaxHeight().background(Color.Black))
                        Text(
                            text = "₹${NumberToWordsIndian.formatINR(item.totalAmount)}",
                            modifier = Modifier.weight(1.4f).padding(6.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = PoppinsFontFamily,
                            textAlign = TextAlign.End
                        )
                    }
                }

                // Filler space with vertical dividers
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                ) {
                    Box(modifier = Modifier.weight(2f).fillMaxHeight())
                    Box(modifier = Modifier.width(1.5.dp).fillMaxHeight().background(Color.Black))
                    Box(modifier = Modifier.weight(1.3f).fillMaxHeight())
                    Box(modifier = Modifier.width(1.5.dp).fillMaxHeight().background(Color.Black))
                    Box(modifier = Modifier.weight(1.1f).fillMaxHeight())
                    Box(modifier = Modifier.width(1.5.dp).fillMaxHeight().background(Color.Black))
                    Box(modifier = Modifier.weight(1.4f).fillMaxHeight())
                }

                // Table Footer
                Box(modifier = Modifier.fillMaxWidth().height(1.5.dp).background(Color.Black))
                if (pageSlice.showSummaryAndSignatures) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left empty space spanning Item + Amount/Student head
                        Box(modifier = Modifier.weight(2f))
                        Box(modifier = Modifier.width(1.5.dp))
                        Box(modifier = Modifier.weight(1.3f))

                        // Box 1: In the column of "No. of Students"
                        Box(modifier = Modifier.width(1.5.dp).fillMaxHeight().background(Color.Black))
                        Box(
                            modifier = Modifier
                                .weight(1.1f)
                                .fillMaxHeight()
                                .padding(vertical = 6.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Text(
                                text = "Total Amount :",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = PoppinsFontFamily,
                                textAlign = TextAlign.End
                            )
                        }

                        // Box 2: In the column of "Total Amount"
                        Box(modifier = Modifier.width(1.5.dp).fillMaxHeight().background(Color.Black))
                        Box(
                            modifier = Modifier
                                .weight(1.4f)
                                .fillMaxHeight()
                                .padding(vertical = 6.dp, horizontal = 6.dp),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Text(
                                text = "₹${NumberToWordsIndian.formatINR(totalAmount)}",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = PoppinsFontFamily,
                                textAlign = TextAlign.End
                            )
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF9FAFB))
                            .padding(vertical = 4.dp, horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Items continued on next page...",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = PoppinsFontFamily,
                            color = Color.DarkGray
                        )
                        Text(
                            text = "Continued on Page ${pageSlice.pageNumber + 1} →",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = PoppinsFontFamily,
                            color = Color.Black
                        )
                    }
                }
            }
        }

        // 3. PAYMENT & LEGAL DETAILS (on final page)
        if (pageSlice.showSummaryAndSignatures) {
            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, Color.Black)
                    .padding(6.dp)
            ) {
                Text(
                    text = "Amount (In words) : $amountInWords",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = PoppinsFontFamily
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, Color.Black)
                    .padding(6.dp)
            ) {
                Text(
                    text = "Payment Method : ${invoiceData.paymentMethod.displayName}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = PoppinsFontFamily
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4. SIGNATURE FOOTER
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                // Customer Signature
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Spacer(modifier = Modifier.height(40.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .border(1.5.dp, Color.Black)
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = invoiceData.customerSignatureLabel,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = PoppinsFontFamily
                        )
                    }
                }

                // Authorised Signatory
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_signature),
                        contentDescription = "Suman Mondal Signature",
                        modifier = Modifier
                            .height(40.dp)
                            .fillMaxWidth(0.85f),
                        contentScale = ContentScale.Fit
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .border(1.5.dp, Color.Black)
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Authorised Signatory",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = PoppinsFontFamily
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 5. BOTTOM PAGE FOOTER
        Divider(color = Color(0xFFE5E7EB), thickness = 1.dp)
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "RoboGyaan Invoice Suite • ${invoiceData.invoiceNo}",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = PoppinsFontFamily,
                color = Color.Gray
            )
            Box(
                modifier = Modifier
                    .background(Color.Black, RoundedCornerShape(3.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "PAGE ${pageSlice.pageNumber} OF ${pageSlice.totalPages}",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = PoppinsFontFamily,
                    color = Color.White
                )
            }
        }
    }
}
