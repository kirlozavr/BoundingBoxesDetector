package com.example.boundingboxesdetector.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.boundingboxesdetector.tfmodel.TensorFlowDetector
import com.example.boundingboxesdetector.utils.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val cameraUtil: CameraUtil,
    private val imageGalleryUtil: ImageGalleryUtil,
    private val compressImage: CompressImage,
    private val model: TensorFlowDetector
) : ViewModel() {

    private val bitmapLiveData: SingleLiveData<Bitmap> = SingleLiveData()
    private val isSaveLiveData: SingleLiveData<Boolean> = SingleLiveData()

    override fun onCleared() {
        super.onCleared()
        model.close()
    }

    fun showGallery() {
        imageGalleryUtil.launch(compressImage)
    }

    fun showCamera(){
        cameraUtil.launch(compressImage)
    }

    fun saveImage(bitmap: Bitmap){
        viewModelScope.launch {
            val isSave = SaveImageUtil.save(bitmap)
            isSaveLiveData.postValue(isSave)
        }
    }

    fun overlayResults(bmp: Bitmap) {
        viewModelScope.launch {
            val result = model.detect(bmp)
            val rectFs = result.map { it.boundingBox }.toMutableList()
            val bitmap = OverlayBitmap.overlay(bmp, rectFs)
            bitmapLiveData.postValue(bitmap)
        }
    }

    fun getIsSaveResult(): LiveData<Boolean> = isSaveLiveData

    fun getBitmapResult(): LiveData<Bitmap> = bitmapLiveData

    fun getBitmapFromCompress(): LiveData<Bitmap> = compressImage.getBitmap()

}