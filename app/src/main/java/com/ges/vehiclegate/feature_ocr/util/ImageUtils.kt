package com.ges.vehiclegate.feature_ocr.util

import android.graphics.Bitmap

object ImageUtils {

    /**
     * Recadre le centre d’une image pour réduire le bruit visuel autour.
     * Utile pour OCR plaques — augmente la précision.
     *
     * @param bitmap image originale
     * @param widthPercent pourcentage largeur (0.0 — 1.0)
     * @return nouvelle image recadrée
     */
    fun cropCenter(bitmap: Bitmap?, widthPercent: Float = 0.5f): Bitmap {
        require(bitmap != null) { "Bitmap null" }

        val w = bitmap.width
        val h = bitmap.height

        val cropWidth = (w * widthPercent).toInt()
        val cropHeight = (h * 0.25f).toInt() // les plaques sont horizontales

        val startX = (w - cropWidth) / 2
        val startY = (h - cropHeight) / 2

        return Bitmap.createBitmap(bitmap, startX, startY, cropWidth, cropHeight)
    }
}
