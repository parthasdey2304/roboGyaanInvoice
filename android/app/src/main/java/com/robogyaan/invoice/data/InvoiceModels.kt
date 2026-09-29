package com.robogyaan.invoice.data

import java.util.UUID

enum class PaymentMethod(val displayName: String) {
    CASH("CASH"),
    UPI("UPI"),
    CHEQUE("CHEQUE"),
    BANK_DRAFT("BANK DRAFT"),
    NEFT("NEFT")
}

data class InvoiceItem(
    val id: String = UUID.randomUUID().toString(),
    val description: String = "",
    val amountPerHead: Double = 0.0,
    val studentCount: Int = 0
) {
    val totalAmount: Double
        get() = amountPerHead * studentCount
}

data class BilledParty(
    val name: String = "",
    val address: String = "",
    val pinState: String = ""
)

data class SenderParty(
    val company: String = "ROBOGYAAN",
    val address: String = "Dakshin Gobindopur , Sonarpur",
    val cityPinState: String = "Kolkata - 700145 , West Bengal"
)

data class InvoiceData(
    val invoiceNo: String = "RG-JPS-2608-001",
    val registrationNo: String = "273744",
    val issueDate: String = "01/09/2026",
    val dueDate: String = "08/09/2026",
    val billedTo: BilledParty = BilledParty(
        name = "JYOTIRMOY PUBLIC SCHOOL",
        address = "Tematha , Sonarpur",
        pinState = "Pin:743330 , West Bengal"
    ),
    val from: SenderParty = SenderParty(),
    val items: List<InvoiceItem> = listOf(
        InvoiceItem(
            id = "default-item-1",
            description = "Robogyaan ECA Programme (Premium)",
            amountPerHead = 200.0,
            studentCount = 136
        )
    ),
    val paymentMethod: PaymentMethod = PaymentMethod.CASH,
    val customerSignatureLabel: String = "Customer Signature",
    val authoriserName: String = "Suman Mondal",
    val showWatermark: Boolean = true
) {
    val totalAmount: Double
        get() = items.sumOf { it.totalAmount }
}
