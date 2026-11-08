package com.becetti.tools.core.utils

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import java.io.ByteArrayOutputStream
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/** Date helpers shared by projects, invoices, quotations and the date tools. */
object DateUtils {

    private val iso: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val zone: ZoneId get() = ZoneId.systemDefault()

    /** Parses "yyyy-MM-dd" to epoch millis (local noon), or null when invalid. */
    fun parseIsoDate(text: String): Long? = runCatching {
        val clean = text.trim()
        if (clean.isEmpty()) return null
        val date = LocalDate.parse(clean, iso)
        date.atTime(12, 0).atZone(zone).toInstant().toEpochMilli()
    }.getOrNull()

    fun toIsoDate(millis: Long): String =
        runCatching {
            Instant.ofEpochMilli(millis).atZone(zone).toLocalDate().format(iso)
        }.getOrDefault("")

    fun todayIso(): String = LocalDate.now().format(iso)

    fun humanDate(millis: Long): String = if (millis <= 0L) "" else FileExporter.humanDate(millis, withTime = false)

    /** Whole days from today until [millis] (negative when overdue). */
    fun daysUntil(millis: Long): Long = runCatching {
        val target = Instant.ofEpochMilli(millis).atZone(zone).toLocalDate()
        ChronoUnit.DAYS.between(LocalDate.now(), target)
    }.getOrDefault(0L)

    fun addDays(base: Long, days: Long): Long =
        Instant.ofEpochMilli(base).atZone(zone).plusDays(days).toInstant().toEpochMilli()
}

/**
 * Real PDF report generation (A4, wrapped body text, automatic pagination) used by the project,
 * invoice and sales modules. Built on android.graphics.pdf — no external service involved.
 */
object PdfReport {

    private const val PAGE_W = 595
    private const val PAGE_H = 842
    private const val MARGIN = 42f

    fun exportTextPdf(context: Context, title: String, body: List<String>, fileName: String): String {
        val doc = PdfDocument()
        val titlePaint = Paint().apply {
            isAntiAlias = true
            textSize = 19f
            color = Color.rgb(20, 24, 40)
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val headingPaint = Paint().apply {
            isAntiAlias = true
            textSize = 12f
            color = Color.rgb(60, 70, 95)
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val bodyPaint = Paint().apply {
            isAntiAlias = true
            textSize = 10.5f
            color = Color.rgb(45, 52, 70)
        }
        val footerPaint = Paint().apply {
            isAntiAlias = true
            textSize = 8.5f
            color = Color.rgb(130, 140, 160)
        }
        val maxWidth = PAGE_W - MARGIN * 2

        var pageNumber = 1
        var page = doc.startPage(PdfDocument.PageInfo.Builder(PAGE_W, PAGE_H, pageNumber).create())
        var canvas = page.canvas
        var y = MARGIN + 12f

        canvas.drawText(title, MARGIN, y, titlePaint)
        y += 10f
        canvas.drawLine(MARGIN, y, PAGE_W - MARGIN, y, bodyPaint)
        y += 22f

        body.forEach { rawLine ->
            val paint = if (rawLine.startsWith("#")) headingPaint else bodyPaint
            val text = if (rawLine.startsWith("#")) rawLine.removePrefix("#").trim() else rawLine
            if (text.isBlank()) {
                y += 8f
                return@forEach
            }
            wrap(text, paint, maxWidth).forEach { line ->
                if (y > PAGE_H - MARGIN - 14f) {
                    canvas.drawText("BECETTI TOOLS", MARGIN, PAGE_H - 22f, footerPaint)
                    doc.finishPage(page)
                    pageNumber++
                    page = doc.startPage(PdfDocument.PageInfo.Builder(PAGE_W, PAGE_H, pageNumber).create())
                    canvas = page.canvas
                    y = MARGIN + 12f
                }
                canvas.drawText(line, MARGIN, y, paint)
                y += if (paint === headingPaint) 19f else 15f
            }
        }
        canvas.drawText("BECETTI TOOLS · page $pageNumber", MARGIN, PAGE_H - 22f, footerPaint)
        doc.finishPage(page)

        val bytes = ByteArrayOutputStream().use { out ->
            doc.writeTo(out)
            out.toByteArray()
        }
        doc.close()
        return FileExporter.saveBytes(context, fileName, "application/pdf", bytes)
    }

    private fun wrap(text: String, paint: Paint, maxWidth: Float): List<String> {
        val words = text.split(' ')
        val lines = mutableListOf<String>()
        val current = StringBuilder()
        words.forEach { word ->
            val candidate = if (current.isEmpty()) word else "${current} $word"
            if (paint.measureText(candidate) <= maxWidth) {
                current.setLength(0)
                current.append(candidate)
            } else {
                if (current.isNotEmpty()) lines.add(current.toString())
                current.setLength(0)
                current.append(word)
            }
        }
        if (current.isNotEmpty()) lines.add(current.toString())
        return lines.ifEmpty { listOf("") }
    }
}

/** Minimal RFC-4180 style CSV writer. */
object Csv {
    fun line(vararg cells: Any?): String = cells.joinToString(",") { cell ->
        val raw = cell?.toString().orEmpty()
        if (raw.contains(',') || raw.contains('"') || raw.contains('\n')) {
            "\"" + raw.replace("\"", "\"\"") + "\""
        } else raw
    }

    fun document(rows: List<List<Any?>>): String = buildString {
        rows.forEach { row -> append(line(*row.toTypedArray())).append('\n') }
    }
}
