package com.robogyaan.invoice.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.robogyaan.invoice.data.InvoiceData
import com.robogyaan.invoice.data.PaymentMethod
import com.robogyaan.invoice.ui.InvoiceViewModel
import com.robogyaan.invoice.ui.neoBrutal
import com.robogyaan.invoice.ui.theme.NeoBlack
import com.robogyaan.invoice.ui.theme.NeoDanger
import com.robogyaan.invoice.ui.theme.NeoYellow
import com.robogyaan.invoice.ui.theme.VirgilFontFamily
import com.robogyaan.invoice.util.NumberToWordsIndian

@Composable
fun InvoiceEditorScreen(
    invoiceData: InvoiceData,
    viewModel: InvoiceViewModel,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. INVOICE META
        item {
            NeoBrutalCard(
                title = "Invoice Meta",
                badge = "Header",
                backgroundColor = NeoYellow
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    NeoBrutalTextField(
                        value = invoiceData.invoiceNo,
                        onValueChange = { viewModel.updateInvoiceNo(it) },
                        label = "Invoice No.",
                        placeholder = "RG-JPS-2608-001",
                        modifier = Modifier.weight(1f)
                    )
                    NeoBrutalTextField(
                        value = invoiceData.registrationNo,
                        onValueChange = { viewModel.updateRegistrationNo(it) },
                        label = "Reg. No.",
                        placeholder = "273744",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    NeoBrutalTextField(
                        value = invoiceData.issueDate,
                        onValueChange = { viewModel.updateIssueDate(it) },
                        label = "Issue Date",
                        placeholder = "01/09/2026",
                        modifier = Modifier.weight(1f)
                    )
                    NeoBrutalTextField(
                        value = invoiceData.dueDate,
                        onValueChange = { viewModel.updateDueDate(it) },
                        label = "Due Date",
                        placeholder = "08/09/2026",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 2. BILLED TO (CLIENT)
        item {
            NeoBrutalCard(
                title = "BILL To (Client)",
                badge = "Recipient",
                backgroundColor = Color.White
            ) {
                NeoBrutalTextField(
                    value = invoiceData.billedTo.name,
                    onValueChange = {
                        viewModel.updateBilledTo(it, invoiceData.billedTo.address, invoiceData.billedTo.pinState)
                    },
                    label = "School / Client Name",
                    placeholder = "JYOTIRMOY PUBLIC SCHOOL",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                NeoBrutalTextField(
                    value = invoiceData.billedTo.address,
                    onValueChange = {
                        viewModel.updateBilledTo(invoiceData.billedTo.name, it, invoiceData.billedTo.pinState)
                    },
                    label = "Address Line",
                    placeholder = "Tematha , Sonarpur",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                NeoBrutalTextField(
                    value = invoiceData.billedTo.pinState,
                    onValueChange = {
                        viewModel.updateBilledTo(invoiceData.billedTo.name, invoiceData.billedTo.address, it)
                    },
                    label = "Pin & State",
                    placeholder = "Pin:743330 , West Bengal",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // 3. FROM (SENDER)
        item {
            NeoBrutalCard(
                title = "From (Robogyaan)",
                badge = "Sender",
                backgroundColor = Color.White
            ) {
                NeoBrutalTextField(
                    value = invoiceData.from.company,
                    onValueChange = {
                        viewModel.updateSender(it, invoiceData.from.address, invoiceData.from.cityPinState)
                    },
                    label = "Company Name",
                    placeholder = "ROBOGYAAN",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                NeoBrutalTextField(
                    value = invoiceData.from.address,
                    onValueChange = {
                        viewModel.updateSender(invoiceData.from.company, it, invoiceData.from.cityPinState)
                    },
                    label = "Address Line",
                    placeholder = "Dakshin Gobindopur , Sonarpur",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                NeoBrutalTextField(
                    value = invoiceData.from.cityPinState,
                    onValueChange = {
                        viewModel.updateSender(invoiceData.from.company, invoiceData.from.address, it)
                    },
                    label = "City, Pin & State",
                    placeholder = "Kolkata - 700145 , West Bengal",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // 4. ITEMIZED BILLING TABLE
        item {
            NeoBrutalCard(
                title = "Itemized Billing",
                badge = "Items",
                backgroundColor = Color.White,
                headerAction = {
                    NeoBrutalButton(
                        text = "Add",
                        onClick = { viewModel.addItem() },
                        backgroundColor = NeoYellow,
                        icon = { Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.Black, modifier = Modifier.size(16.dp)) }
                    )
                }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    invoiceData.items.forEachIndexed { index, item ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .neoBrutal(
                                    backgroundColor = Color(0xFFF9FAFB),
                                    shadowOffset = 2.dp,
                                    cornerRadius = 6.dp
                                )
                                .padding(10.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "#${index + 1} Line Item",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 12.sp,
                                        fontFamily = VirgilFontFamily
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "₹${NumberToWordsIndian.formatINR(item.totalAmount)}",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 13.sp,
                                            fontFamily = VirgilFontFamily,
                                            modifier = Modifier
                                                .background(Color.Black, RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp),
                                            color = Color.White
                                        )
                                        if (invoiceData.items.size > 1) {
                                            IconButton(
                                                onClick = { viewModel.removeItem(item.id) },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.Delete,
                                                    contentDescription = "Delete Item",
                                                    tint = NeoDanger,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                NeoBrutalTextField(
                                    value = item.description,
                                    onValueChange = {
                                        viewModel.updateItem(item.id, it, item.amountPerHead, item.studentCount)
                                    },
                                    label = "Program / Item Description",
                                    placeholder = "Robogyaan ECA Programme"
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    NeoBrutalTextField(
                                        value = if (item.amountPerHead == 0.0) "" else item.amountPerHead.toString(),
                                        onValueChange = {
                                            val rate = it.toDoubleOrNull() ?: 0.0
                                            viewModel.updateItem(item.id, item.description, rate, item.studentCount)
                                        },
                                        label = "Rate / Student (₹)",
                                        placeholder = "200.00",
                                        keyboardType = KeyboardType.Decimal,
                                        modifier = Modifier.weight(1f)
                                    )
                                    NeoBrutalTextField(
                                        value = if (item.studentCount == 0) "" else item.studentCount.toString(),
                                        onValueChange = {
                                            val count = it.toIntOrNull() ?: 0
                                            viewModel.updateItem(item.id, item.description, item.amountPerHead, count)
                                        },
                                        label = "No. Students",
                                        placeholder = "136",
                                        keyboardType = KeyboardType.Number,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 5. PAYMENT METHOD & SETTINGS
        item {
            NeoBrutalCard(
                title = "Payment & Settings",
                badge = "Final",
                backgroundColor = Color.White
            ) {
                Text(
                    text = "PAYMENT METHOD",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = VirgilFontFamily,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PaymentMethod.values().forEach { method ->
                        val isSelected = invoiceData.paymentMethod == method
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.updatePaymentMethod(method) }
                                .neoBrutal(
                                    backgroundColor = if (isSelected) NeoYellow else Color.White,
                                    shadowOffset = if (isSelected) 3.dp else 1.5.dp,
                                    cornerRadius = 6.dp
                                )
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = method.displayName,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = VirgilFontFamily,
                                color = NeoBlack
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = invoiceData.showWatermark,
                        onCheckedChange = { viewModel.toggleWatermark(it) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color.Black,
                            checkmarkColor = NeoYellow
                        )
                    )
                    Text(
                        text = "Show Robogyaan Watermark in table",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = VirgilFontFamily,
                        color = NeoBlack
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    NeoBrutalButton(
                        text = "Reset to Defaults",
                        onClick = { viewModel.resetToDefaults() },
                        backgroundColor = Color.White,
                        icon = { Icon(Icons.Default.Refresh, contentDescription = "Reset", modifier = Modifier.size(16.dp)) }
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
