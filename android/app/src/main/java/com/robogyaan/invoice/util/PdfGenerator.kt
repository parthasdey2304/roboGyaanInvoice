package com.robogyaan.invoice.util

import android.content.Context
import android.graphics.*
import android.graphics.pdf.PdfDocument
import androidx.core.content.res.ResourcesCompat
import com.robogyaan.invoice.R
import com.robogyaan.invoice.data.InvoiceData
import java.io.File
import java.io.FileOutputStream

object PdfGenerator {

    // Standard A4 dimensions in points (72 points per inch): 595 x 842
    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842

    fun generateA4Pdf(context: Context, data: InvoiceData): File {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        // Load Virgil custom Typeface or fallback to cursive/sans-serif
        val virgilTypeface = try {
            ResourcesCompat.getFont(context, R.font.virgil) ?: Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        } catch (e: Exception) {
            Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        }

        val totalAmount = data.totalAmount
        val amountInWords = NumberToWordsIndian.convert(totalAmount)

        // Base Paints
        val fillWhite = Paint().apply {
            color = Color.WHITE
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, 0f, PAGE_WIDTH.toFloat(), PAGE_HEIGHT.toFloat(), fillWhite)

        val blackPaint = Paint().apply {
            color = Color.BLACK
            typeface = virgilTypeface
            isAntiAlias = true
        }

        val strokePaint = Paint().apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 1.5f
            isAntiAlias = true
        }

        val margin = 32f
        var currentY = 32f

        // 1. TOP HEADER SECTION
        // Robogyaan Logo on left
        val logoBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_robogyaan_logo)
        if (logoBitmap != null) {
            val logoWidth = 130f
            val logoHeight = (logoBitmap.height * logoWidth) / logoBitmap.width
            val dstRect = RectF(margin, currentY, margin + logoWidth, currentY + logoHeight)
            canvas.drawBitmap(logoBitmap, null, dstRect, blackPaint)
        }

        // Top Right Geometric Blocks
        val rightX = PAGE_WIDTH - margin
        val blockWidth = 230f

        // Black Polygon
        val blackPath = Path().apply {
            moveTo(rightX - blockWidth + 24f, currentY + 5f)
            lineTo(rightX, currentY + 5f)
            lineTo(rightX, currentY + 32f)
            lineTo(rightX - blockWidth, currentY + 32f)
            close()
        }
        val blackFill = Paint().apply { color = Color.parseColor("#121212"); style = Paint.Style.FILL }
        canvas.drawPath(blackPath, blackFill)

        val whiteText = Paint().apply {
            color = Color.WHITE
            typeface = virgilTypeface
            textSize = 10f
            isFakeBoldText = true
            isAntiAlias = true
            textAlign = Paint.Align.RIGHT
        }
        canvas.drawText("Invoice No. : ${data.invoiceNo}", rightX - 12f, currentY + 23f, whiteText)

        // Orange Badge
        val orangePath = Path().apply {
            moveTo(rightX - blockWidth + 40f, currentY + 36f)
            lineTo(rightX, currentY + 36f)
            lineTo(rightX, currentY + 58f)
            lineTo(rightX - blockWidth + 20f, currentY + 58f)
            close()
        }
        val orangeFill = Paint().apply { color = Color.parseColor("#FFA500"); style = Paint.Style.FILL }
        canvas.drawPath(orangePath, orangeFill)

        val badgeText = Paint().apply {
            color = Color.BLACK
            typeface = virgilTypeface
            textSize = 9.5f
            isFakeBoldText = true
            isAntiAlias = true
            textAlign = Paint.Align.RIGHT
        }
        canvas.drawText("Registration No. - ${data.registrationNo}", rightX - 12f, currentY + 51f, badgeText)

        currentY += 76f

        // Divider
        val linePaint = Paint().apply { color = Color.parseColor("#D1D5DB"); strokeWidth = 1f }
        canvas.drawLine(margin, currentY, rightX, currentY, linePaint)

        currentY += 16f

        // 2. SENDER & RECIPIENT (BILL TO / FROM)
        val colWidth = (rightX - margin - 20f) / 2f
        val pillPaint = Paint().apply { color = Color.parseColor("#E5E7EB"); style = Paint.Style.FILL }

        // BILL To
        val billToPill = RectF(margin, currentY, margin + 74f, currentY + 18f)
        canvas.drawRoundRect(billToPill, 9f, 9f, pillPaint)
        blackPaint.textSize = 10f
        blackPaint.isFakeBoldText = true
        canvas.drawText("BILL To :", margin + 14f, currentY + 13f, blackPaint)

        blackPaint.textSize = 11f
        canvas.drawText(data.billedTo.name, margin, currentY + 34f, blackPaint)
        blackPaint.textSize = 10f
        blackPaint.isFakeBoldText = false
        canvas.drawText(data.billedTo.address, margin, currentY + 48f, blackPaint)
        canvas.drawText(data.billedTo.pinState, margin, currentY + 62f, blackPaint)

        // From
        val fromX = margin + colWidth + 20f
        val fromPill = RectF(fromX, currentY, fromX + 64f, currentY + 18f)
        canvas.drawRoundRect(fromPill, 9f, 9f, pillPaint)
        blackPaint.textSize = 10f
        blackPaint.isFakeBoldText = true
        canvas.drawText("From :", fromX + 14f, currentY + 13f, blackPaint)

        blackPaint.textSize = 11f
        canvas.drawText(data.from.company, fromX, currentY + 34f, blackPaint)
        blackPaint.textSize = 10f
        blackPaint.isFakeBoldText = false
        canvas.drawText(data.from.address, fromX, currentY + 48f, blackPaint)
        canvas.drawText(data.from.cityPinState, fromX, currentY + 62f, blackPaint)

        currentY += 80f

        // 3. META SUMMARY BAR (4 Columns)
        val metaBoxWidth = (rightX - margin - 12f) / 4f
        val yellowPaint = Paint().apply { color = Color.parseColor("#FFB800"); style = Paint.Style.FILL }
        val darkGrayPaint = Paint().apply { color = Color.parseColor("#505050"); style = Paint.Style.FILL }

        val metaTitles = listOf("Invoice No.", "Issue Date:", "Due Date:", "Total Due :")
        val metaValues = listOf(data.invoiceNo, data.issueDate, data.dueDate, "₹ ${NumberToWordsIndian.formatINR(totalAmount)}/-")

        for (i in 0..3) {
            val boxLeft = margin + i * (metaBoxWidth + 4f)
            val boxRect = RectF(boxLeft, currentY, boxLeft + metaBoxWidth, currentY + 38f)
            canvas.drawRoundRect(boxRect, 2f, 2f, if (i == 3) darkGrayPaint else yellowPaint)

            whiteText.textAlign = Paint.Align.CENTER
            whiteText.textSize = 8.5f
            whiteText.isFakeBoldText = false
            canvas.drawText(metaTitles[i], boxLeft + metaBoxWidth / 2f, currentY + 15f, whiteText)

            whiteText.textSize = 9.5f
            whiteText.isFakeBoldText = true
            canvas.drawText(metaValues[i], boxLeft + metaBoxWidth / 2f, currentY + 30f, whiteText)
        }

        currentY += 54f

        // 4. ITEMIZED BILLING TABLE
        val tableTop = currentY
        val tableBottom = currentY + 220f
        val tableRect = RectF(margin, tableTop, rightX, tableBottom)
        canvas.drawRect(tableRect, strokePaint)

        // Watermark if enabled
        if (data.showWatermark) {
            val symbolBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_robogyaan_symbol)
            if (symbolBitmap != null) {
                canvas.save()
                canvas.rotate(-25f, PAGE_WIDTH / 2f, tableTop + 110f)
                val wmPaint = Paint().apply { alpha = 25 }
                val wmRect = RectF(PAGE_WIDTH / 2f - 70f, tableTop + 40f, PAGE_WIDTH / 2f + 70f, tableTop + 180f)
                canvas.drawBitmap(symbolBitmap, null, wmRect, wmPaint)
                canvas.restore()
            }
        }

        // Table Header
        val headerHeight = 28f
        val headerBottom = tableTop + headerHeight
        canvas.drawLine(margin, headerBottom, rightX, headerBottom, strokePaint)

        val col1Width = (rightX - margin) * 0.44f
        val col2Width = (rightX - margin) * 0.20f
        val col3Width = (rightX - margin) * 0.16f
        val col4Width = (rightX - margin) * 0.20f

        val c1X = margin + col1Width
        val c2X = c1X + col2Width
        val c3X = c2X + col3Width

        canvas.drawLine(c1X, tableTop, c1X, tableBottom, strokePaint)
        canvas.drawLine(c2X, tableTop, c2X, tableBottom, strokePaint)
        canvas.drawLine(c3X, tableTop, c3X, tableBottom, strokePaint)

        blackPaint.textSize = 9.5f
        blackPaint.isFakeBoldText = true
        blackPaint.textAlign = Paint.Align.CENTER
        canvas.drawText("Item", margin + col1Width / 2f, tableTop + 18f, blackPaint)
        canvas.drawText("Amount/ Student head", c1X + col2Width / 2f, tableTop + 18f, blackPaint)
        canvas.drawText("No. of Students", c2X + col3Width / 2f, tableTop + 18f, blackPaint)
        canvas.drawText("Total Amount", c3X + col4Width / 2f, tableTop + 18f, blackPaint)

        // Table Rows
        var rowY = headerBottom + 20f
        data.items.forEachIndexed { index, item ->
            blackPaint.textAlign = Paint.Align.LEFT
            blackPaint.textSize = 9.5f
            blackPaint.isFakeBoldText = false
            canvas.drawText("${index + 1}. ${item.description}", margin + 8f, rowY, blackPaint)

            blackPaint.textAlign = Paint.Align.CENTER
            canvas.drawText("₹${NumberToWordsIndian.formatINR(item.amountPerHead)}", c1X + col2Width / 2f, rowY, blackPaint)
            canvas.drawText("${item.studentCount}", c2X + col3Width / 2f, rowY, blackPaint)

            blackPaint.textAlign = Paint.Align.RIGHT
            blackPaint.isFakeBoldText = true
            canvas.drawText("₹${NumberToWordsIndian.formatINR(item.totalAmount)}", rightX - 8f, rowY, blackPaint)

            rowY += 22f
        }

        // Table Total Row at table bottom
        val totalRowTop = tableBottom - 26f
        canvas.drawLine(c2X, totalRowTop, rightX, totalRowTop, strokePaint)

        blackPaint.textAlign = Paint.Align.RIGHT
        blackPaint.textSize = 10.5f
        blackPaint.isFakeBoldText = true
        canvas.drawText("Total Amount : ₹${NumberToWordsIndian.formatINR(totalAmount)}", rightX - 10f, tableBottom - 8f, blackPaint)

        currentY = tableBottom + 12f

        // 5. PAYMENT & LEGAL DETAILS
        val wordsBox = RectF(margin, currentY, rightX, currentY + 24f)
        canvas.drawRect(wordsBox, strokePaint)
        blackPaint.textAlign = Paint.Align.LEFT
        blackPaint.textSize = 9.5f
        blackPaint.isFakeBoldText = true
        canvas.drawText("Amount (In words) : $amountInWords", margin + 8f, currentY + 16f, blackPaint)

        currentY += 28f

        val payBox = RectF(margin, currentY, rightX, currentY + 24f)
        canvas.drawRect(payBox, strokePaint)
        canvas.drawText("Payment Method : ${data.paymentMethod.displayName}", margin + 8f, currentY + 16f, blackPaint)

        currentY += 46f

        // 6. SIGNATURE FOOTER
        val sigBoxWidth = 170f
        val sigBoxHeight = 24f

        // Customer Signature Box (Left)
        val custSigRect = RectF(margin + 20f, currentY + 40f, margin + 20f + sigBoxWidth, currentY + 40f + sigBoxHeight)
        canvas.drawRect(custSigRect, strokePaint)
        blackPaint.textAlign = Paint.Align.CENTER
        blackPaint.textSize = 9.5f
        blackPaint.isFakeBoldText = true
        canvas.drawText(data.customerSignatureLabel, custSigRect.centerX(), custSigRect.centerY() + 4f, blackPaint)

        // Authorised Signatory with Signature Image (Right)
        val authX = rightX - 20f - sigBoxWidth
        val sigBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_signature)
        if (sigBitmap != null) {
            val sigW = 150f
            val sigH = (sigBitmap.height * sigW) / sigBitmap.width
            val sigDst = RectF(authX + 10f, currentY + 40f - sigH + 6f, authX + 10f + sigW, currentY + 46f)
            canvas.drawBitmap(sigBitmap, null, sigDst, blackPaint)
        }

        val authRect = RectF(authX, currentY + 40f, authX + sigBoxWidth, currentY + 40f + sigBoxHeight)
        canvas.drawRect(authRect, strokePaint)
        canvas.drawText("Authorised Signatory", authRect.centerX(), authRect.centerY() + 4f, blackPaint)

        // Finish page
        document.finishPage(page)

        // Save to cache / output directory
        val outputDir = File(context.getExternalFilesDir(null) ?: context.filesDir, "Invoices")
        if (!outputDir.exists()) outputDir.mkdirs()

        val fileName = "Robogyaan_Invoice_${data.invoiceNo.replace("[^a-zA-Z0-9_-]".toRegex(), "_")}.pdf"
        val pdfFile = File(outputDir, fileName)

        val fos = FileOutputStream(pdfFile)
        document.writeTo(fos)
        fos.flush()
        fos.close()
        document.close()

        return pdfFile
    }
}
