package com.robogyaan.invoice.util

import android.content.Context
import android.graphics.*
import android.graphics.pdf.PdfDocument
import androidx.core.content.res.ResourcesCompat
import com.robogyaan.invoice.R
import com.robogyaan.invoice.data.InvoiceData
import com.robogyaan.invoice.data.InvoicePageSlice
import com.robogyaan.invoice.data.paginateInvoiceItems
import java.io.File
import java.io.FileOutputStream

object PdfGenerator {

    // Standard A4 dimensions in points (72 points per inch): 595 x 842
    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842

    fun generateA4Pdf(context: Context, data: InvoiceData): File {
        val document = PdfDocument()

        // Load custom Typefaces
        val poppinsTypeface = try {
            ResourcesCompat.getFont(context, R.font.poppins_regular) ?: Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        } catch (e: Exception) {
            Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        }

        val poppinsSemiBold = try {
            ResourcesCompat.getFont(context, R.font.poppins_semibold) ?: Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        } catch (e: Exception) {
            Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        }

        val totalAmount = data.totalAmount
        val amountInWords = NumberToWordsIndian.convert(totalAmount)

        // Base Paints
        val fillWhite = Paint().apply {
            color = Color.WHITE
            style = Paint.Style.FILL
        }

        val blackPaint = Paint().apply {
            color = Color.BLACK
            typeface = poppinsTypeface
            isAntiAlias = true
        }

        val strokePaint = Paint().apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 1.5f
            isAntiAlias = true
        }

        val pages = paginateInvoiceItems(data.items)

        pages.forEach { pageSlice ->
            val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageSlice.pageNumber).create()
            val page = document.startPage(pageInfo)
            val canvas = page.canvas

            // Fill canvas with white background
            canvas.drawRect(0f, 0f, PAGE_WIDTH.toFloat(), PAGE_HEIGHT.toFloat(), fillWhite)

            val margin = 32f
            val rightX = PAGE_WIDTH - margin
            var currentY = 32f

            if (pageSlice.isFirstPage) {
                // Robogyaan Logo on left
                val logoBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_robogyaan_logo)
                if (logoBitmap != null) {
                    val logoWidth = 130f
                    val logoHeight = (logoBitmap.height * logoWidth) / logoBitmap.width
                    val dstRect = RectF(margin, currentY, margin + logoWidth, currentY + logoHeight)
                    canvas.drawBitmap(logoBitmap, null, dstRect, blackPaint)
                }

                // Top Right Geometric Blocks
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
                    typeface = poppinsSemiBold
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
                    typeface = poppinsSemiBold
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

                // SENDER & RECIPIENT (BILL TO / FROM)
                val colWidth = (rightX - margin - 20f) / 2f
                val pillPaint = Paint().apply { color = Color.parseColor("#E5E7EB"); style = Paint.Style.FILL }

                // BILL To
                val billToPill = RectF(margin, currentY, margin + 74f, currentY + 18f)
                canvas.drawRoundRect(billToPill, 9f, 9f, pillPaint)
                blackPaint.textSize = 10f
                blackPaint.isFakeBoldText = true
                canvas.drawText("BILL To:", margin + 14f, currentY + 13f, blackPaint)

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
                canvas.drawText("From:", fromX + 14f, currentY + 13f, blackPaint)

                blackPaint.textSize = 11f
                canvas.drawText(data.from.company, fromX, currentY + 34f, blackPaint)
                blackPaint.textSize = 10f
                blackPaint.isFakeBoldText = false
                canvas.drawText(data.from.address, fromX, currentY + 48f, blackPaint)
                canvas.drawText(data.from.cityPinState, fromX, currentY + 62f, blackPaint)

                currentY += 80f

                // META SUMMARY BAR (4 Columns)
                val metaBoxWidth = (rightX - margin - 12f) / 4f
                val yellowPaint = Paint().apply { color = Color.parseColor("#FFB800"); style = Paint.Style.FILL }
                val darkGrayPaint = Paint().apply { color = Color.parseColor("#505050"); style = Paint.Style.FILL }

                val metaTitles = listOf("Invoice No.", "Issue Date:", "Due Date:", "Total Due :")
                val metaValues = listOf(data.invoiceNo, data.issueDate, data.dueDate, "₹ ${NumberToWordsIndian.formatINR(totalAmount)}/-")

                for (i in 0..3) {
                    val boxLeft = margin + i * (metaBoxWidth + 4f)
                    val boxRect = RectF(boxLeft, currentY, boxLeft + metaBoxWidth, currentY + 38f)
                    canvas.drawRoundRect(boxRect, 2f, 2f, if (i == 3) darkGrayPaint else yellowPaint)

                    val whiteTextMeta = Paint().apply {
                        color = Color.WHITE
                        typeface = poppinsSemiBold
                        textSize = 8.5f
                        isFakeBoldText = false
                        isAntiAlias = true
                        textAlign = Paint.Align.CENTER
                    }
                    canvas.drawText(metaTitles[i], boxLeft + metaBoxWidth / 2f, currentY + 15f, whiteTextMeta)

                    whiteTextMeta.textSize = 9.5f
                    whiteTextMeta.isFakeBoldText = true
                    canvas.drawText(metaValues[i], boxLeft + metaBoxWidth / 2f, currentY + 30f, whiteTextMeta)
                }

                currentY += 54f
            } else {
                // Continuation Header (Matching Page 1 geometry and user's sketch)
                val logoBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_robogyaan_logo)
                if (logoBitmap != null) {
                    val logoWidth = 130f
                    val logoHeight = (logoBitmap.height * logoWidth) / logoBitmap.width
                    val dstRect = RectF(margin, currentY, margin + logoWidth, currentY + logoHeight)
                    canvas.drawBitmap(logoBitmap, null, dstRect, blackPaint)
                }

                // Top Right Geometric Blocks
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

                val whiteTextCont = Paint().apply {
                    color = Color.WHITE
                    typeface = poppinsSemiBold
                    textSize = 10f
                    isFakeBoldText = true
                    isAntiAlias = true
                    textAlign = Paint.Align.RIGHT
                }
                canvas.drawText("Invoice No. : ${data.invoiceNo}", rightX - 12f, currentY + 23f, whiteTextCont)

                // Orange Badge for PAGE X OF Y
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
                    typeface = poppinsSemiBold
                    textSize = 9.5f
                    isFakeBoldText = true
                    isAntiAlias = true
                    textAlign = Paint.Align.RIGHT
                }
                canvas.drawText("PAGE ${pageSlice.pageNumber} OF ${pageSlice.totalPages}", rightX - 12f, currentY + 51f, badgeText)

                // Client name below badge
                val clientText = Paint().apply {
                    color = Color.parseColor("#4B5563")
                    typeface = poppinsSemiBold
                    textSize = 9f
                    textAlign = Paint.Align.RIGHT
                    isAntiAlias = true
                }
                canvas.drawText(data.billedTo.name, rightX, currentY + 70f, clientText)

                currentY += 76f

                val linePaint = Paint().apply { color = Color.parseColor("#D1D5DB"); strokeWidth = 1f }
                canvas.drawLine(margin, currentY, rightX, currentY, linePaint)

                currentY += 16f
            }

            // ITEMIZED BILLING TABLE
            val tableTop = currentY
            val tableHeight = if (pageSlice.isFirstPage) {
                if (pageSlice.isLastPage) 220f else 280f
            } else {
                if (pageSlice.isLastPage) 320f else 460f
            }
            val tableBottom = tableTop + tableHeight
            val tableRect = RectF(margin, tableTop, rightX, tableBottom)
            canvas.drawRect(tableRect, strokePaint)

            // Watermark if enabled
            if (data.showWatermark) {
                val symbolBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_robogyaan_symbol)
                if (symbolBitmap != null) {
                    canvas.save()
                    canvas.rotate(-25f, PAGE_WIDTH / 2f, tableTop + tableHeight / 2f)
                    val wmPaint = Paint().apply { alpha = 20 }
                    val wmRect = RectF(PAGE_WIDTH / 2f - 70f, tableTop + tableHeight / 2f - 70f, PAGE_WIDTH / 2f + 70f, tableTop + tableHeight / 2f + 70f)
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
            pageSlice.items.forEachIndexed { index, item ->
                val absoluteIndex = pageSlice.itemStartIndex + index + 1
                blackPaint.textAlign = Paint.Align.LEFT
                blackPaint.textSize = 9.5f
                blackPaint.isFakeBoldText = false
                canvas.drawText("$absoluteIndex. ${item.description}", margin + 8f, rowY, blackPaint)

                blackPaint.textAlign = Paint.Align.CENTER
                canvas.drawText("₹${NumberToWordsIndian.formatINR(item.amountPerHead)}", c1X + col2Width / 2f, rowY, blackPaint)
                canvas.drawText("${item.studentCount}", c2X + col3Width / 2f, rowY, blackPaint)

                blackPaint.textAlign = Paint.Align.RIGHT
                blackPaint.isFakeBoldText = true
                canvas.drawText("₹${NumberToWordsIndian.formatINR(item.totalAmount)}", rightX - 8f, rowY, blackPaint)

                rowY += 22f
            }

            // Table Footer (Total amount or Continued line)
            val totalRowTop = tableBottom - 26f
            canvas.drawLine(margin, totalRowTop, rightX, totalRowTop, strokePaint)

            if (pageSlice.showSummaryAndSignatures) {
                blackPaint.textAlign = Paint.Align.RIGHT
                blackPaint.textSize = 10.5f
                blackPaint.isFakeBoldText = true
                canvas.drawText("Total Amount : ₹${NumberToWordsIndian.formatINR(totalAmount)}", rightX - 10f, tableBottom - 8f, blackPaint)
            } else {
                blackPaint.textAlign = Paint.Align.LEFT
                blackPaint.textSize = 8.5f
                blackPaint.isFakeBoldText = false
                canvas.drawText("Items continued on next page...", margin + 10f, tableBottom - 9f, blackPaint)

                blackPaint.textAlign = Paint.Align.RIGHT
                blackPaint.isFakeBoldText = true
                canvas.drawText("Continued on Page ${pageSlice.pageNumber + 1} →", rightX - 10f, tableBottom - 9f, blackPaint)
            }

            currentY = tableBottom + 12f

            if (pageSlice.showSummaryAndSignatures) {
                // PAYMENT & LEGAL DETAILS
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

                currentY += 40f

                // SIGNATURE FOOTER
                val sigBoxWidth = 170f
                val sigBoxHeight = 24f

                // Customer Signature Box (Left)
                val custSigRect = RectF(margin + 20f, currentY + 36f, margin + 20f + sigBoxWidth, currentY + 36f + sigBoxHeight)
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
                    val sigDst = RectF(authX + 10f, currentY + 36f - sigH + 6f, authX + 10f + sigW, currentY + 42f)
                    canvas.drawBitmap(sigBitmap, null, sigDst, blackPaint)
                }

                val authRect = RectF(authX, currentY + 36f, authX + sigBoxWidth, currentY + 36f + sigBoxHeight)
                canvas.drawRect(authRect, strokePaint)
                canvas.drawText("Authorised Signatory", authRect.centerX(), authRect.centerY() + 4f, blackPaint)
            }

            // Bottom Footer on each page (at y = PAGE_HEIGHT - 22f)
            val footerY = PAGE_HEIGHT - 22f
            val footerLinePaint = Paint().apply { color = Color.parseColor("#E5E7EB"); strokeWidth = 1f }
            canvas.drawLine(margin, footerY - 10f, rightX, footerY - 10f, footerLinePaint)

            val footerTextPaint = Paint().apply {
                color = Color.parseColor("#6B7280")
                typeface = poppinsTypeface
                textSize = 8.5f
                isAntiAlias = true
            }
            canvas.drawText("RoboGyaan Invoice Suite • ${data.invoiceNo}", margin, footerY, footerTextPaint)

            val pageBadgeFooter = RectF(rightX - 70f, footerY - 10f, rightX, footerY + 4f)
            val badgeBg = Paint().apply { color = Color.BLACK; style = Paint.Style.FILL }
            canvas.drawRoundRect(pageBadgeFooter, 3f, 3f, badgeBg)

            val pageTextPaint = Paint().apply {
                color = Color.WHITE
                typeface = poppinsSemiBold
                textSize = 8f
                isFakeBoldText = true
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }
            canvas.drawText("PAGE ${pageSlice.pageNumber} OF ${pageSlice.totalPages}", pageBadgeFooter.centerX(), footerY, pageTextPaint)

            // Finish page
            document.finishPage(page)
        }

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
