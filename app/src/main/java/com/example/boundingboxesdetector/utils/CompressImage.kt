package com.example.boundingboxesdetector.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.lifecycle.LiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.boundingboxesdetector.utils.TargetSizeImage.TARGET_HEIGHT
import com.example.boundingboxesdetector.utils.TargetSizeImage.TARGET_WIDTH
import javax.inject.Inject

/** Класс для изменения размеров картинки до 640/480 с сохранением соотношения сторон **/
class CompressImage @Inject constructor() {

    private val bitmapLiveData = SingleLiveData<Bitmap>()

    fun getBitmap(): LiveData<Bitmap> = bitmapLiveData

    suspend fun compress(context: Context, uri: Uri) {
        Glide
            .with(context)
            .asBitmap()
            .load(uri)
            .apply(RequestOptions().override(TARGET_WIDTH, TARGET_HEIGHT).format(DecodeFormat.PREFER_ARGB_8888))
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    bitmapLiveData.postValue(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }

            })
    }
}