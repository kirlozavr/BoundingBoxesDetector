package com.example.boundingboxesdetector.utils

import android.content.Context
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import javax.inject.Inject

class ImageGalleryUtil @Inject constructor(
    private val context: Context,
    private val resultRegistry: ActivityResultRegistry
){

    private lateinit var compress: CompressImage

    private val KEY = "key"
    private val resultLauncher: ActivityResultLauncher<String> = resultRegistry
        .register(
            KEY,
            ActivityResultContracts.GetContent(),
            ActivityResultCallback {
                if (it != null) {
                    compress.compress(context, it)
                }
            }
        )

    fun launch(compress: CompressImage){
        this.compress = compress
        resultLauncher.launch("image/*")
    }
}