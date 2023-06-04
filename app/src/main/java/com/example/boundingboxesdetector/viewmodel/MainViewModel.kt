package com.example.boundingboxesdetector.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.boundingboxesdetector.utils.CameraUtil
import com.example.boundingboxesdetector.utils.CompressImage
import com.example.boundingboxesdetector.utils.ImageGalleryUtil
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val cameraUtil: CameraUtil
) : ViewModel() {

    @Inject
    lateinit var imageGalleryUtil: ImageGalleryUtil

    @Inject
    lateinit var compressImage: CompressImage

    fun showGallery() {
        imageGalleryUtil.launch(compressImage)
    }

    fun showCamera(){
        cameraUtil.launch(compressImage)
    }

    fun getBitmap(): LiveData<Bitmap> = compressImage.getBitmap()

}