package com.example.boundingboxesdetector.tfmodel

import android.graphics.RectF

/** Класс обертка для результатов работы модели **/
data class Detection
constructor(val boundingBox: RectF, val scope: Float) {}