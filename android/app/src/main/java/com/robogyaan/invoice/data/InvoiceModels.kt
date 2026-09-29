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

data class InvoicePageSlice(
    val pageNumber: Int,
    var totalPages: Int,
    val items: List<InvoiceItem>,
    val itemStartIndex: Int,
    val isFirstPage: Boolean,
    var isLastPage: Boolean,
    val showSummaryAndSignatures: Boolean
)

const val FIRST_PAGE_SINGLE_CAPACITY = 7
const val FIRST_PAGE_MULTI_CAPACITY = 8
const val CONTINUATION_PAGE_MAX_ITEMS = 15
const val FINAL_PAGE_WITH_FOOTER_MAX = 11

fun paginateInvoiceItems(items: List<InvoiceItem>): List<InvoicePageSlice> {
    val total = items.size
    if (total <= FIRST_PAGE_SINGLE_CAPACITY) {
        return listOf(
            InvoicePageSlice(
                pageNumber = 1,
                totalPages = 1,
                items = items,
                itemStartIndex = 0,
                isFirstPage = true,
                isLastPage = true,
                showSummaryAndSignatures = true
            )
        )
    }

    val pages = mutableListOf<InvoicePageSlice>()
    var currentIndex = 0

    // Page 1
    val p1Count = minOf(total, FIRST_PAGE_MULTI_CAPACITY)
    pages.add(
        InvoicePageSlice(
            pageNumber = 1,
            totalPages = 1,
            items = items.subList(0, p1Count),
            itemStartIndex = 0,
            isFirstPage = true,
            isLastPage = false,
            showSummaryAndSignatures = false
        )
    )
    currentIndex += p1Count

    var pageNum = 2
    while (currentIndex < total) {
        val remaining = total - currentIndex
        if (remaining <= FINAL_PAGE_WITH_FOOTER_MAX) {
            pages.add(
                InvoicePageSlice(
                    pageNumber = pageNum,
                    totalPages = pageNum,
                    items = items.subList(currentIndex, currentIndex + remaining),
                    itemStartIndex = currentIndex,
                    isFirstPage = false,
                    isLastPage = true,
                    showSummaryAndSignatures = true
                )
            )
            currentIndex += remaining
            pageNum++
        } else if (remaining <= CONTINUATION_PAGE_MAX_ITEMS) {
            val thisPageCount = maxOf(1, remaining - FINAL_PAGE_WITH_FOOTER_MAX)
            pages.add(
                InvoicePageSlice(
                    pageNumber = pageNum,
                    totalPages = pageNum,
                    items = items.subList(currentIndex, currentIndex + thisPageCount),
                    itemStartIndex = currentIndex,
                    isFirstPage = false,
                    isLastPage = false,
                    showSummaryAndSignatures = false
                )
            )
            currentIndex += thisPageCount
            pageNum++
        } else {
            pages.add(
                InvoicePageSlice(
                    pageNumber = pageNum,
                    totalPages = pageNum,
                    items = items.subList(currentIndex, currentIndex + CONTINUATION_PAGE_MAX_ITEMS),
                    itemStartIndex = currentIndex,
                    isFirstPage = false,
                    isLastPage = false,
                    showSummaryAndSignatures = false
                )
            )
            currentIndex += CONTINUATION_PAGE_MAX_ITEMS
            pageNum++
        }
    }

    val totalPages = pages.size
    pages.forEachIndexed { idx, page ->
        page.totalPages = totalPages
        page.isLastPage = (idx == totalPages - 1)
    }

    return pages
}

