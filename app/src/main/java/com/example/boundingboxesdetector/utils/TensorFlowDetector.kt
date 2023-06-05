package com.example.boundingboxesdetector.utils

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.vision.detector.ObjectDetector
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.channels.FileChannel

/** Детектор, пока что наработки **/
class TensorFlowDetector constructor(
    private val context: Context,
    private val detectorListener: DetectorListener
) {

    private val MODEL_SKU_BASE = "sku-base-640-480-fp16.tflite"
    private val threshold = 0.5F
    private val maxResult = 3
    private val numThreads = 2
    private val currentDelegate = 0

    private lateinit var objectDetector: ObjectDetector
    private lateinit var tfLite: Interpreter
    private lateinit var tfLiteModel: ByteBuffer

    init {
        initModel()
    }

    private fun initModel() {
        val optionsBuilder = ObjectDetector.ObjectDetectorOptions.builder()
            .setScoreThreshold(threshold)
            .setMaxResults(maxResult)
        val baseOptionsBuilder = BaseOptions.builder().setNumThreads(numThreads)

        tfLiteModel = getModelByteBuffer(context.assets, MODEL_SKU_BASE)
        tfLite = Interpreter(tfLiteModel)

        when (currentDelegate) {
            DELEGATE_CPU -> {

            }
            DELEGATE_GPU -> {
                if (CompatibilityList().isDelegateSupportedOnThisDevice) {
                    baseOptionsBuilder.useGpu()
                } else {

                }
            }
            DELEGATE_NNAPI -> {
                baseOptionsBuilder.useNnapi()
            }
        }

        optionsBuilder.setBaseOptions(baseOptionsBuilder.build())
        objectDetector =
            ObjectDetector.createFromFileAndOptions(context, MODEL_SKU_BASE, optionsBuilder.build())

    }

    fun detect(bitmap: Bitmap) {
        val imageProcessor = ImageProcessor.Builder().build()
        val tensorImage = imageProcessor.process(TensorImage.fromBitmap(bitmap))
        val results = objectDetector.detect(tensorImage)
        detectorListener.onResults(results)
    }

    private fun getModelByteBuffer(assetManager: AssetManager, modelPath: String): ByteBuffer {
        val fileDescriptor = assetManager.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    companion object {
        const val DELEGATE_CPU = 0
        const val DELEGATE_GPU = 1
        const val DELEGATE_NNAPI = 2
    }
}