package com.ges.vehiclegate.feature_ocr.ocr

import android.graphics.BitmapFactory
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.tasks.await
import com.ges.vehiclegate.feature_ocr.util.ImageUtils
import com.ges.vehiclegate.feature_ocr.util.ImageUtils.cropCenter

class PlateOcrService {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    /**
     * Retourne le texte OCR brut.
     */
    suspend fun recognizeTextFromFile(photoPath: String): String {
        var bitmap = BitmapFactory.decodeFile(photoPath)
        bitmap = ImageUtils.cropCenter(bitmap, 0.5f)
            ?: throw IllegalArgumentException("Impossible de lire l'image: $photoPath")

        val image = InputImage.fromBitmap(bitmap, 0)
        val result = recognizer.process(image).await()
        return result.text ?: ""
    }

    fun close() {
        recognizer.close()
    }
}
