package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.data.local.GameSessionEntity
import com.example.data.model.GameRound
import com.example.data.model.Player
import com.example.viewmodel.PlayerStats
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfReportGenerator {

    fun generateAndSharePdf(
        context: Context,
        session: GameSessionEntity,
        players: List<Player>,
        rounds: List<GameRound>,
        stats: List<PlayerStats>
    ) {
        val pdfFile = createPdf(context, session, players, rounds, stats) ?: return

        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            pdfFile
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Doppelkopf Spielbericht - ${session.title}")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooser = Intent.createChooser(intent, "Spielbericht als PDF teilen").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    }

    private fun createPdf(
        context: Context,
        session: GameSessionEntity,
        players: List<Player>,
        rounds: List<GameRound>,
        stats: List<PlayerStats>
    ): File? {
        val document = PdfDocument()
        val pageWidth = 595
        val pageHeight = 842

        val titlePaint = Paint().apply {
            color = Color.rgb(13, 40, 24) // Deep felt green
            textSize = 20f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val subTitlePaint = Paint().apply {
            color = Color.DKGRAY
            textSize = 10f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
        }

        val headerPaint = Paint().apply {
            color = Color.rgb(180, 140, 30) // Gold
            textSize = 10f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val textPaint = Paint().apply {
            color = Color.BLACK
            textSize = 9.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
        }

        val boldTextPaint = Paint().apply {
            color = Color.BLACK
            textSize = 9.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val greenPaint = Paint().apply {
            color = Color.rgb(34, 139, 34) // Forest green for winners
            textSize = 9.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val redPaint = Paint().apply {
            color = Color.rgb(205, 38, 38) // Crimson red for losers
            textSize = 9.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val linePaint = Paint().apply {
            color = Color.LTGRAY
            strokeWidth = 0.8f
        }

        val bgHeaderPaint = Paint().apply {
            color = Color.rgb(245, 245, 240)
            style = Paint.Style.FILL
        }

        var pageNumber = 1
        var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
        var page = document.startPage(pageInfo)
        var canvas = page.canvas

        var y = 40f

        // Document Title
        canvas.drawText("♠ ♥ Doppelkopf Spielbericht ♣ ♦", 40f, y, titlePaint)
        y += 18f

        val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.GERMANY)
        val dateStr = dateFormat.format(Date(session.updatedAt))
        canvas.drawText("Runde: ${session.title}  |  Stand: $dateStr  |  Gespielte Spiele: ${rounds.size}", 40f, y, subTitlePaint)
        y += 24f

        // RANKING / STATS SUMMARY
        canvas.drawText("AKTUELLE RANGLISTE", 40f, y, headerPaint)
        y += 14f

        stats.sortedBy { it.rank }.forEach { s ->
            val sign = if (s.currentScore > 0) "+" else ""
            val scoreStr = "$sign${s.currentScore} Pkt."
            val p = if (s.currentScore > 0) greenPaint else if (s.currentScore < 0) redPaint else textPaint

            canvas.drawText("${s.rank}. ${s.player.name}:", 45f, y, boldTextPaint)
            canvas.drawText(scoreStr, 160f, y, p)
            canvas.drawText("(${s.gamesWon} Siege, ${s.gamesLost} Niederlagen, ${s.soliWon}/${s.soliPlayed} Soli)", 220f, y, subTitlePaint)
            y += 14f
        }

        y += 16f

        // TABLE SECTION
        canvas.drawText("SPIELHISTORIE (TABELLE)", 40f, y, headerPaint)
        y += 12f

        val colX = floatArrayOf(40f, 75f, 130f, 215f, 305f, 395f, 485f)
        val rowHeight = 18f

        fun drawTableHeader() {
            canvas.drawRect(40f, y - 10f, 555f, y + 8f, bgHeaderPaint)
            canvas.drawText("#", colX[0], y, boldTextPaint)
            canvas.drawText("Geber", colX[1], y, boldTextPaint)
            canvas.drawText("Spielwert", colX[2], y, boldTextPaint)
            players.forEachIndexed { i, p ->
                canvas.drawText(p.name, colX[3 + i], y, boldTextPaint)
            }
            y += 6f
            canvas.drawLine(40f, y, 555f, y, linePaint)
            y += 12f
        }

        drawTableHeader()

        // List rounds (chronological or antichronological - user requested antichronological list)
        rounds.reversed().forEach { r ->
            // Check if page full
            if (y > pageHeight - 50f) {
                document.finishPage(page)
                pageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                page = document.startPage(pageInfo)
                canvas = page.canvas
                y = 40f
                canvas.drawText("Doppelkopf Spielbericht - ${session.title} (Seite $pageNumber)", 40f, y, subTitlePaint)
                y += 20f
                drawTableHeader()
            }

            val dealer = players.getOrNull(r.dealerIndex)?.name ?: "Sp ${r.dealerIndex + 1}"
            val bock = if (r.bockMultiplier > 1) " (x${r.bockMultiplier})" else ""
            val valStr = "${r.totalRoundValue} P$bock"

            canvas.drawText("${r.roundNumber}", colX[0], y, textPaint)
            canvas.drawText(dealer, colX[1], y, textPaint)
            canvas.drawText(valStr, colX[2], y, if (r.bockMultiplier > 1) boldTextPaint else textPaint)

            // Players cumulative score colored green if won, red if lost
            r.playerRunningScores.forEachIndexed { idx, score ->
                val delta = r.playerScoreDeltas.getOrElse(idx) { 0 }
                val paint = when {
                    delta > 0 -> greenPaint
                    delta < 0 -> redPaint
                    else -> textPaint
                }
                canvas.drawText("$score", colX[3 + idx], y, paint)
            }

            y += 4f
            canvas.drawLine(40f, y, 555f, y, linePaint)
            y += rowHeight
        }

        document.finishPage(page)

        val file = File(context.cacheDir, "doppelkopf_${System.currentTimeMillis()}.pdf")
        try {
            FileOutputStream(file).use { out ->
                document.writeTo(out)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        } finally {
            document.close()
        }
        return file
    }
}
