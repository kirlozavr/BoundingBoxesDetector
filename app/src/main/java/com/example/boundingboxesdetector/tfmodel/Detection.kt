package com.example.boundingboxesdetector.tfmodel

import android.graphics.RectF

data class Detection
constructor(val boundingBox: RectF, val scope: Float) {}