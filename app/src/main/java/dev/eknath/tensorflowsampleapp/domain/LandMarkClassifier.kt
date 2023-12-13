package dev.eknath.tensorflowsampleapp.domain

import android.graphics.Bitmap

interface LandMarkClassifier {
    fun classify(bitmap: Bitmap, rotation: Int): List<Classification>
}

