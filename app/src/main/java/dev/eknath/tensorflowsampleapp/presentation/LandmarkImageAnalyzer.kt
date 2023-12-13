package dev.eknath.tensorflowsampleapp.presentation

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import dev.eknath.tensorflowsampleapp.domain.Classification
import dev.eknath.tensorflowsampleapp.domain.LandMarkClassifier
import org.tensorflow.lite.task.vision.classifier.ImageClassifier

class LandmarkImageAnalyzer(
    private val classifier: LandMarkClassifier,
    private val onResult: (List<Classification>) -> Unit

) : ImageAnalysis.Analyzer {

    var frameSkipCounter = 0
    override fun analyze(image: ImageProxy) {

        if (frameSkipCounter % 60 == 0) {
            val rotationDegrees = image.imageInfo.rotationDegrees
            val bitmap = image.toBitmap().centerCrop(231, 321)

            val result = classifier.classify(bitmap, rotationDegrees)
            onResult(result)
        }
        frameSkipCounter++
        image.close()

    }
}