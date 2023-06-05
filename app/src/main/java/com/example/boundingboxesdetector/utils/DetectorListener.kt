package com.example.boundingboxesdetector.utils

import org.tensorflow.lite.task.vision.detector.Detection

interface DetectorListener {

    fun onError(error: String)
    fun onResults(results: MutableList<Detection>?)
}