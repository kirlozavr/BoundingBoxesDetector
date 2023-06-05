package com.example.boundingboxesdetector

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.boundingboxesdetector.databinding.ActivityMainBinding
import com.example.boundingboxesdetector.viewmodel.MainViewModel
import com.example.boundingboxesdetector.viewmodel.ViewModelFactory
import javax.inject.Inject
import android.Manifest
import android.os.Build
import android.view.View
import com.example.boundingboxesdetector.utils.CameraUtil
import com.example.boundingboxesdetector.utils.SingleLiveData

class MainActivity : AppCompatActivity() {

    private val PERMISSION_CODE = 200
    private val READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE
    private val READ_MEDIA_IMAGES = Manifest.permission.READ_MEDIA_IMAGES
    private val CAMERA = Manifest.permission.CAMERA

    private lateinit var binding: ActivityMainBinding
    private val visibleButtonsGalleryAndCamera = SingleLiveData<Boolean>()
    private val cameraUtil = CameraUtil(this)

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
    }

    private val component by lazy {
        (application as Application).component
            .mainActivityComponentFactory()
            .create(this, cameraUtil, activityResultRegistry)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        component.inject(this)
        component.inject(cameraUtil)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        visibleButtonsGalleryAndCamera.postValue(true)

        onClick()
        listenerLiveDataView()
    }

    private fun listenerLiveDataView() {
        visibleButtonsGalleryAndCamera.observe(this) {
            if (it) {
                binding.layoutButtonGalleryAndCamera.visibility = View.VISIBLE
                binding.layoutButtonUndoAndSave.visibility = View.GONE
                binding.textView.visibility = View.VISIBLE
                binding.imageView.setImageBitmap(null)
            } else {
                binding.layoutButtonGalleryAndCamera.visibility = View.GONE
                binding.layoutButtonUndoAndSave.visibility = View.VISIBLE
                binding.textView.visibility = View.GONE
            }
        }
    }

    private fun onClick() {
        binding.galleryButton.setOnClickListener {
            if (checkPermissionReadStorage()) {
                clickShowGallery()
            }
        }
        binding.cameraButton.setOnClickListener {
            if (checkPermissionCamera()) {
                clickShowCamera()
            }
        }
        binding.undoButton.setOnClickListener {
            clickUndo()
        }
        binding.saveButton.setOnClickListener {
            clickSave()
        }
    }

    private fun clickSave() {
        visibleButtonsGalleryAndCamera.postValue(true)
    }

    private fun clickUndo() {
        visibleButtonsGalleryAndCamera.postValue(true)
    }

    private fun clickShowCamera() {
        viewModel.showCamera()
        viewModel.getBitmap().observe(this@MainActivity) {
            visibleButtonsGalleryAndCamera.postValue(false)
            binding.imageView.setImageBitmap(it)
        }
    }

    private fun clickShowGallery() {
        viewModel.showGallery()
        viewModel.getBitmap().observe(this@MainActivity) {
            visibleButtonsGalleryAndCamera.postValue(false)
            binding.imageView.setImageBitmap(it)
        }
    }

    private fun checkPermissionReadStorage(): Boolean {
        return if (Build.VERSION.SDK_INT in 23..32) {
            if (checkSelfPermission(READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(READ_EXTERNAL_STORAGE),
                    PERMISSION_CODE
                )
                false
            } else {
                true
            }
        } else if (Build.VERSION.SDK_INT >= 33) {
            if (checkSelfPermission(READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(READ_MEDIA_IMAGES),
                    PERMISSION_CODE
                )
                false
            } else {
                true
            }
        } else {
            false
        }
    }

    private fun checkPermissionCamera(): Boolean {
        return if (checkSelfPermission(CAMERA) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(CAMERA),
                PERMISSION_CODE
            )
            false
        } else {
            true
        }
    }

}