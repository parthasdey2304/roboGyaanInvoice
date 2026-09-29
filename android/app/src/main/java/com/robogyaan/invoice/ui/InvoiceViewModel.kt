package com.robogyaan.invoice.ui

import androidx.lifecycle.ViewModel
import com.robogyaan.invoice.data.BilledParty
import com.robogyaan.invoice.data.InvoiceData
import com.robogyaan.invoice.data.InvoiceItem
import com.robogyaan.invoice.data.PaymentMethod
import com.robogyaan.invoice.data.SenderParty
import com.robogyaan.invoice.util.NumberToWordsIndian
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class InvoiceViewModel : ViewModel() {

    private val defaultData = InvoiceData()

    private val _invoiceState = MutableStateFlow(defaultData)
    val invoiceState: StateFlow<InvoiceData> = _invoiceState.asStateFlow()

    fun updateInvoiceNo(no: String) {
        _invoiceState.update { it.copy(invoiceNo = no) }
    }

    fun updateRegistrationNo(reg: String) {
        _invoiceState.update { it.copy(registrationNo = reg) }
    }

    fun updateIssueDate(date: String) {
        _invoiceState.update { it.copy(issueDate = date) }
    }

    fun updateDueDate(date: String) {
        _invoiceState.update { it.copy(dueDate = date) }
    }

    fun updateBilledTo(name: String, address: String, pinState: String) {
        _invoiceState.update {
            it.copy(
                billedTo = BilledParty(name = name, address = address, pinState = pinState)
            )
        }
    }

    fun updateSender(company: String, address: String, cityPinState: String) {
        _invoiceState.update {
            it.copy(
                from = SenderParty(company = company, address = address, cityPinState = cityPinState)
            )
        }
    }

    fun updatePaymentMethod(method: PaymentMethod) {
        _invoiceState.update { it.copy(paymentMethod = method) }
    }

    fun toggleWatermark(show: Boolean) {
        _invoiceState.update { it.copy(showWatermark = show) }
    }

    fun addItem() {
        val newItem = InvoiceItem(
            id = UUID.randomUUID().toString(),
            description = "Robogyaan Robotics & AI Workshop",
            amountPerHead = 250.0,
            studentCount = 100
        )
        _invoiceState.update {
            it.copy(items = it.items + newItem)
        }
    }

    fun removeItem(id: String) {
        _invoiceState.update { current ->
            if (current.items.size <= 1) current
            else current.copy(items = current.items.filterNot { it.id == id })
        }
    }

    fun updateItem(id: String, description: String, amountPerHead: Double, studentCount: Int) {
        _invoiceState.update { current ->
            current.copy(
                items = current.items.map { item ->
                    if (item.id == id) {
                        item.copy(
                            description = description,
                            amountPerHead = amountPerHead,
                            studentCount = studentCount
                        )
                    } else {
                        item
                    }
                }
            )
        }
    }

    fun resetToDefaults() {
        _invoiceState.value = defaultData
    }

    fun getAmountInWords(): String {
        return NumberToWordsIndian.convert(_invoiceState.value.totalAmount)
    }
}
