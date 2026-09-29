package com.robogyaan.invoice.ui.preview

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.robogyaan.invoice.ui.neoBrutal
import com.robogyaan.invoice.ui.theme.NeoBlack
import com.robogyaan.invoice.ui.theme.NeoOrange
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

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFE5E7EB))
            .padding(12.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        // A4 Sheet container
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .neoBrutal(
                    backgroundColor = Color.White,
                    shadowOffset = 6.dp,
                    cornerRadius = 4.dp
                )
                .padding(16.dp)
        ) {
            // 1. TOP HEADER SECTION
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
                    // Black Polygon Header
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
                            fontFamily = VirgilFontFamily,
                            textAlign = TextAlign.End,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    // Orange Registration Badge
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
                            fontFamily = VirgilFontFamily,
                            textAlign = TextAlign.End,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = Color(0xFFD1D5DB), thickness = 1.dp)
            Spacer(modifier = Modifier.height(10.dp))

            // 2. SENDER & RECIPIENT (BILL TO / FROM)
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
                            text = "BILL To :",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = VirgilFontFamily,
                            color = Color.Black
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = invoiceData.billedTo.name,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = VirgilFontFamily,
                        color = NeoBlack
                    )
                    Text(
                        text = invoiceData.billedTo.address,
                        fontSize = 11.sp,
                        fontFamily = VirgilFontFamily,
                        color = Color.DarkGray
                    )
                    Text(
                        text = invoiceData.billedTo.pinState,
                        fontSize = 11.sp,
                        fontFamily = VirgilFontFamily,
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
                            text = "From :",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = VirgilFontFamily,
                            color = Color.Black
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = invoiceData.from.company,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = VirgilFontFamily,
                        color = NeoBlack
                    )
                    Text(
                        text = invoiceData.from.address,
                        fontSize = 11.sp,
                        fontFamily = VirgilFontFamily,
                        color = Color.DarkGray
                    )
                    Text(
                        text = invoiceData.from.cityPinState,
                        fontSize = 11.sp,
                        fontFamily = VirgilFontFamily,
                        color = Color.DarkGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3. META SUMMARY BAR (4 Columns)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Invoice No
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFFFFB800), RoundedCornerShape(2.dp))
                        .padding(vertical = 4.dp, horizontal = 2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Invoice No.", color = Color.White, fontSize = 9.sp, fontFamily = VirgilFontFamily)
                    Text(invoiceData.invoiceNo, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = VirgilFontFamily, maxLines = 1)
                }

                // Issue Date
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFFFFB800), RoundedCornerShape(2.dp))
                        .padding(vertical = 4.dp, horizontal = 2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Issue Date:", color = Color.White, fontSize = 9.sp, fontFamily = VirgilFontFamily)
                    Text(invoiceData.issueDate, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = VirgilFontFamily)
                }

                // Due Date
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFFFFB800), RoundedCornerShape(2.dp))
                        .padding(vertical = 4.dp, horizontal = 2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Due Date:", color = Color.White, fontSize = 9.sp, fontFamily = VirgilFontFamily)
                    Text(invoiceData.dueDate, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = VirgilFontFamily)
                }

                // Total Due
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFF505050), RoundedCornerShape(2.dp))
                        .padding(vertical = 4.dp, horizontal = 2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Total Due :", color = Color.White, fontSize = 9.sp, fontFamily = VirgilFontFamily)
                    Text("₹ ${NumberToWordsIndian.formatINR(totalAmount)}/-", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = VirgilFontFamily)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4. ITEMIZED BILLING TABLE
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, Color.Black)
            ) {
                // Optional Watermark in background
                if (invoiceData.showWatermark) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_robogyaan_symbol),
                        contentDescription = "Watermark",
                        modifier = Modifier
                            .size(140.dp)
                            .align(Alignment.Center)
                            .rotate(-25f)
                            .graphicsLayer(alpha = 0.1f)
                    )
                }

                Column(modifier = Modifier.fillMaxWidth()) {
                    // Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(width = 1.dp, color = Color.Black)
                            .padding(vertical = 6.dp)
                    ) {
                        Text("Item", modifier = Modifier.weight(2f).padding(horizontal = 4.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = VirgilFontFamily, textAlign = TextAlign.Center)
                        Text("Amount/\nStudent head", modifier = Modifier.weight(1.3f).padding(horizontal = 2.dp), fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = VirgilFontFamily, textAlign = TextAlign.Center)
                        Text("No. of\nStudents", modifier = Modifier.weight(1.1f).padding(horizontal = 2.dp), fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = VirgilFontFamily, textAlign = TextAlign.Center)
                        Text("Total Amount", modifier = Modifier.weight(1.4f).padding(horizontal = 4.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = VirgilFontFamily, textAlign = TextAlign.Center)
                    }

                    Divider(color = Color.Black, thickness = 1.5.dp)

                    // Rows
                    invoiceData.items.forEachIndexed { idx, item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                        ) {
                            Text("${idx + 1}. ${item.description}", modifier = Modifier.weight(2f).padding(horizontal = 4.dp), fontSize = 10.sp, fontFamily = VirgilFontFamily)
                            Text("₹${NumberToWordsIndian.formatINR(item.amountPerHead)}", modifier = Modifier.weight(1.3f).padding(horizontal = 2.dp), fontSize = 10.sp, fontFamily = VirgilFontFamily, textAlign = TextAlign.Center)
                            Text("${item.studentCount}", modifier = Modifier.weight(1.1f).padding(horizontal = 2.dp), fontSize = 10.sp, fontFamily = VirgilFontFamily, textAlign = TextAlign.Center)
                            Text("₹${NumberToWordsIndian.formatINR(item.totalAmount)}", modifier = Modifier.weight(1.4f).padding(horizontal = 4.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = VirgilFontFamily, textAlign = TextAlign.End)
                        }
                        Divider(color = Color(0xFFE5E7EB), thickness = 0.5.dp)
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    // Table Footer Subtotal
                    Divider(color = Color.Black, thickness = 1.5.dp)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp, horizontal = 8.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total Amount : ₹${NumberToWordsIndian.formatINR(totalAmount)}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = VirgilFontFamily
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 5. PAYMENT & LEGAL DETAILS
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, Color.Black)
                    .padding(6.dp)
            ) {
                Text(
                    text = "Amount (In words) : $amountInWords",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = VirgilFontFamily
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
                    fontWeight = FontWeight.Bold,
                    fontFamily = VirgilFontFamily
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 6. SIGNATURE FOOTER
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
                            .width(150.dp)
                            .border(1.5.dp, Color.Black)
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = invoiceData.customerSignatureLabel,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = VirgilFontFamily
                        )
                    }
                }

                // Authorised Signatory with Signature Image
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_signature),
                        contentDescription = "Suman Mondal Signature",
                        modifier = Modifier
                            .height(44.dp)
                            .width(140.dp),
                        contentScale = ContentScale.Fit
                    )
                    Box(
                        modifier = Modifier
                            .width(150.dp)
                            .border(1.5.dp, Color.Black)
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Authorised Signatory",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = VirgilFontFamily
                        )
                    }
                }
            }
        }
    }
}
