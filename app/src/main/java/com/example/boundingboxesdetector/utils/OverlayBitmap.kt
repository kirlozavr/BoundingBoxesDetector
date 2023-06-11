package com.example.boundingboxesdetector.utils

import android.graphics.*

object OverlayBitmap {

    suspend fun overlay(bitmap: Bitmap, rectFList: MutableList<RectF>): Bitmap {
        val bmp = Bitmap.createBitmap(
            bitmap.width,
            bitmap.height,
            Bitmap.Config.ARGB_8888
        )

        rectFList.stream().forEach {
            it.top *= bitmap.height
            it.left *= bitmap.width
            it.right *= bitmap.width
            it.bottom *= bitmap.height
        }

        val canvas = Canvas(bitmap)
        val paint = Paint()
        paint.color = Color.RED
        paint.style = Paint.Style.STROKE

        canvas.drawBitmap(bmp, Matrix(), null)
        rectFList.stream().forEach { rectF ->
            canvas.drawRect(rectF, paint)
        }
        return bitmap
    }
}