package com.example.boundingboxesdetector.utils

import android.graphics.Bitmap
import android.os.Environment
import java.io.File
import java.io.FileOutputStream

/** Класс для сохранения изображения в галереи **/
object SaveImageUtil {

    suspend fun save(bitmap: Bitmap): Boolean {
        return try {
            val filePath =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val dir = File(filePath.absolutePath + "/BoundingBoxes/")
            dir.mkdirs()
            val file = File(dir, "${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            bitmap.recycle()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}