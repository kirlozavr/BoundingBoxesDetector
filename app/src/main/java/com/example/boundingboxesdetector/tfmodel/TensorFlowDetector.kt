package com.example.boundingboxesdetector.tfmodel

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.graphics.Bitmap
import android.graphics.RectF
import com.example.boundingboxesdetector.utils.TargetSizeImage.TARGET_HEIGHT
import com.example.boundingboxesdetector.utils.TargetSizeImage.TARGET_WIDTH

import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import javax.inject.Inject

/** Детектор bounding boxes **/
class TensorFlowDetector @Inject constructor(
    private val context: Context
) {

    private val imageProcessor = ImageProcessor.Builder()
        .add(ResizeOp(TARGET_HEIGHT, TARGET_WIDTH, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
        .add(NormalizeOp(0f, 255f))
        .build()

    private val tflite: Interpreter
    private val tensorImage = TensorImage(DataType.FLOAT32)
    private val boxesTensor = TensorBuffer.createFixedSize(intArrayOf(1, 1000, 4), DataType.FLOAT32)
    private val detectionsCountTensor = TensorBuffer.createFixedSize(intArrayOf(4), DataType.UINT8)
    private val labelTensor = TensorBuffer.createFixedSize(intArrayOf(1, 1000), DataType.FLOAT32)
    private val scopesTensor = TensorBuffer.createFixedSize(intArrayOf(1, 1000), DataType.FLOAT32)
    private val outputs = mutableMapOf<Int, Any>(
        0 to boxesTensor.buffer,
        1 to detectionsCountTensor.buffer,
        2 to labelTensor.buffer,
        3 to scopesTensor.buffer
    )

    init {
        val path = "model.tflite"
        val tfliteModel = loadModelFile(path)
        val tfliteOptions = Interpreter.Options()

        tflite = Interpreter(tfliteModel.asReadOnlyBuffer(), tfliteOptions)
        tflite.allocateTensors()

    }

    suspend fun detect(bitmap: Bitmap): List<Detection> {
        for (buffer in outputs.values) {
            (buffer as ByteBuffer).rewind()
        }

        tensorImage.load(bitmap)
        val tensorImage = imageProcessor.process(tensorImage)
        tflite.runForMultipleInputsOutputs(arrayOf(tensorImage.buffer), outputs)
        return convert(bitmap)
    }

    fun close() {
        tflite.close()
    }

    private suspend fun convert(bitmap: Bitmap): List<Detection> {
        var detectionsCount = 0
        detectionsCountTensor.intArray.forEach {
            detectionsCount += it
            if (it < 255) {
                return@forEach
            }
        }
        val boxesTensor = boxesTensor.floatArray
        val scopesTensor = scopesTensor.floatArray
        val detections = ArrayList<Detection>(detectionsCount)

        val srcRatio = 1f * bitmap.width / bitmap.height
        val dstRatio = 1f * TARGET_WIDTH / TARGET_HEIGHT
        var ax = 1f
        var bx = 0f
        var ay = 1f
        var by = 0f
        if (dstRatio >= srcRatio) {
            val notScaledDstWidth = (bitmap.width * dstRatio / srcRatio).toInt()
            ax = 1f * notScaledDstWidth / bitmap.width
            bx = -ax * ((notScaledDstWidth - bitmap.width) / 2) / notScaledDstWidth
        } else {
            val notScaledDstHeight = (bitmap.height * srcRatio / dstRatio).toInt()
            ay = 1f * notScaledDstHeight / bitmap.height
            by = -ay * ((notScaledDstHeight - bitmap.height) / 2) / notScaledDstHeight
        }

        for (k in 0 until detectionsCount) {
            val boundingBox = RectF(
                ax * boxesTensor[k * 4 + 0] + bx,
                ay * boxesTensor[k * 4 + 1] + by,
                ax * boxesTensor[k * 4 + 2] + bx,
                ay * boxesTensor[k * 4 + 3] + by
            )

            val detect = Detection(boundingBox, scopesTensor[k])
            detections.add(detect)
        }
        return detections
    }

    @Throws(IOException::class)
    private fun loadModelFile(model: String): MappedByteBuffer {
        val fileDescriptor: AssetFileDescriptor = context.assets.openFd(model)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

}