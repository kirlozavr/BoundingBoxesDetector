package com.example.boundingboxesdetector.utils

import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.boundingboxesdetector.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class CameraUtil constructor(private val activity: AppCompatActivity) {

    private lateinit var compress: CompressImage

    private lateinit var uri: Uri
    private val activityResultLauncher = activity.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback {
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                CoroutineScope(Dispatchers.IO).launch {
                    compress.compress(activity.applicationContext, uri)
                }
            }
        })

    fun launch(compress: CompressImage) {
        this.compress = compress
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        uri = FileProvider.getUriForFile(
            activity,
            BuildConfig.APPLICATION_ID + ".provider",
            createImageFile()
        )
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        activityResultLauncher.launch(intent)
    }

    private fun createImageFile(): File {
        val storageDir = activity.getExternalFilesDir(null)

        return File.createTempFile(
            "picture",
            ".jpg",
            storageDir
        )
    }
}