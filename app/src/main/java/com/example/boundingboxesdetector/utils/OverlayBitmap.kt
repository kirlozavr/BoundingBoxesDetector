package com.example.boundingboxesdetector.utils

import android.graphics.*

/** Класс для наложения результата на исходное изображение **/
object OverlayBitmap {

    suspend fun overlay(bitmap: Bitmap, rectFList: MutableList<RectF>): Bitmap {
        val bmp = Bitmap.createBitmap(
            bitmap.width,
            bitmap.height,
            Bitmap.Config.ARGB_8888
        )

        rectFList.stream().forEach {
            it.left *= bitmap.width
            it.top *= bitmap.height
            it.right *= bitmap.width
            it.bottom *= bitmap.height
        }

        val canvas = Canvas(bitmap)
        val paint = Paint()
        paint.color = Color.RED
        paint.strokeWidth = 4F
        paint.style = Paint.Style.STROKE

        canvas.drawBitmap(bmp, Matrix(), null)
        rectFList.stream().forEach { rectF ->
            canvas.drawRect(rectF, paint)
        }
        return bitmap
    }
}