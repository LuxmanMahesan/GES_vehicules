package com.ges.vehiclegate.util.pdf

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Environment
import com.ges.vehiclegate.domain.model.VehicleEntry
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class PdfReportGenerator {

    private val dateFormat = SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault())

    fun generer(
        context: Context,
        vehicules: List<VehicleEntry>
    ): File {

        val pdf = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdf.startPage(pageInfo)

        val canvas = page.canvas

        val titrePaint = Paint().apply {
            textSize = 22f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val labelPaint = Paint().apply {
            textSize = 14f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val valuePaint = Paint().apply {
            textSize = 14f
        }

        val separatorPaint = Paint().apply {
            textSize = 14f
        }

        var y = 60

        // 🔵 Titre du document
        canvas.drawText("RAPPORT JOURNALIER DES VÉHICULES", 40f, y.toFloat(), titrePaint)
        y += 40

        vehicules.forEach { v ->

            // 🔹 Ligne séparatrice
            canvas.drawText("--------------------------------------------", 40f, y.toFloat(), separatorPaint)
            y += 30

            // 🔹 PLAQUE
            canvas.drawText("PLAQUE :", 40f, y.toFloat(), labelPaint)
            canvas.drawText(v.plate, 180f, y.toFloat(), valuePaint)
            y += 22

            // 🔹 SOCIÉTÉ
            canvas.drawText("SOCIÉTÉ :", 40f, y.toFloat(), labelPaint)
            canvas.drawText(v.companyName, 180f, y.toFloat(), valuePaint)
            y += 22

            // 🔹 DESTINATION
            canvas.drawText("DESTINATION :", 40f, y.toFloat(), labelPaint)
            canvas.drawText(v.destination.label, 180f, y.toFloat(), valuePaint)
            y += 22

            // 🔹 TELEPHONE
            if (!v.driverPhone.isNullOrBlank()) {
                canvas.drawText("TEL CHAUFFEUR :", 40f, y.toFloat(), labelPaint)
                canvas.drawText(v.driverPhone, 180f, y.toFloat(), valuePaint)
                y += 22
            }

            // 🔹 ARRIVÉE
            canvas.drawText("ARRIVÉE :", 40f, y.toFloat(), labelPaint)
            canvas.drawText(dateFormat.format(Date(v.arrivalAt)), 180f, y.toFloat(), valuePaint)
            y += 22

            // 🔹 SORTIE
            canvas.drawText("SORTIE :", 40f, y.toFloat(), labelPaint)
            canvas.drawText(
                v.exitAt?.let { dateFormat.format(Date(it)) } ?: "-",
                180f,
                y.toFloat(),
                valuePaint
            )
            y += 22

            // 🔹 NOTES
            if (!v.notes.isNullOrBlank()) {
                canvas.drawText("NOTES :", 40f, y.toFloat(), labelPaint)
                y += 22
                canvas.drawText(v.notes!!, 60f, y.toFloat(), valuePaint)
                y += 22
            }

            y += 20
        }

        pdf.finishPage(page)

        val date = SimpleDateFormat("dd-MM-yyyy_HH'h'mm", Locale.getDefault()).format(Date())

        val file = File(
            context.getExternalFilesDir(null),
            "${date} - liste_vehicules.pdf"
        )



        pdf.writeTo(FileOutputStream(file))
        pdf.close()

        return file
    }
}
