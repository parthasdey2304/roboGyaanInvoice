package com.robogyaan.invoice.util

import java.text.DecimalFormat
import java.util.Locale
import kotlin.math.abs

object NumberToWordsIndian {

    private val units = arrayOf(
        "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine",
        "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen",
        "Seventeen", "Eighteen", "Nineteen"
    )

    private val tens = arrayOf(
        "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"
    )

    private fun convertTwoDigits(n: Long): String {
        if (n == 0L) return ""
        if (n < 20) return units[n.toInt()]
        val t = (n / 10).toInt()
        val u = (n % 10).toInt()
        return (tens[t] + if (u > 0) " " + units[u] else "").trim()
    }

    private fun convertThreeDigits(n: Long): String {
        val h = (n / 100).toInt()
        val r = n % 100
        val parts = mutableListOf<String>()
        if (h > 0) {
            parts.add("${units[h]} Hundred")
        }
        if (r > 0) {
            parts.add(convertTwoDigits(r))
        }
        return parts.joinToString(" ")
    }

    /**
     * Converts integer value up to 99,99,99,999 into Indian English words.
     * Example: 27200 -> "Twenty Seven Thousand Two Hundred Only"
     * Example: 150500 -> "One Lakh Fifty Thousand Five Hundred Only"
     */
    fun convert(amount: Double): String {
        val intVal = abs(amount.toLong())
        if (intVal == 0L) return "Zero Only"

        val crore = intVal / 10000000L
        val lakh = (intVal % 10000000L) / 100000L
        val thousand = (intVal % 100000L) / 1000L
        val remainder = intVal % 1000L

        val parts = mutableListOf<String>()

        if (crore > 0L) {
            parts.add("${convertTwoDigits(crore)} Crore")
        }
        if (lakh > 0L) {
            parts.add("${convertTwoDigits(lakh)} Lakh")
        }
        if (thousand > 0L) {
            parts.add("${convertTwoDigits(thousand)} Thousand")
        }
        if (remainder > 0L) {
            parts.add(convertThreeDigits(remainder))
        }

        return (parts.joinToString(" ") + " Only").trim()
    }

    fun formatINR(amount: Double): String {
        val formatter = DecimalFormat("##,##,##0.00")
        return formatter.format(amount)
    }
}
