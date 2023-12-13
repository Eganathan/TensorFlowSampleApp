package dev.eknath.tensorflowsampleapp.data

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.Surface
import dev.eknath.tensorflowsampleapp.domain.Classification
import dev.eknath.tensorflowsampleapp.domain.LandMarkClassifier
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.core.vision.ImageProcessingOptions
import org.tensorflow.lite.task.vision.classifier.ImageClassifier
import java.lang.Exception

class TFLiteClassifier(
    val context: Context,
    val thresholds: Float = 0.7f,
    private val maxResult: Int = 1,
) : LandMarkClassifier {

    private var imageClassifier: ImageClassifier? = null

    private fun setupClassifier() {
        val baseOptions = BaseOptions.builder()
            .setNumThreads(2)
            .build()

        val options = ImageClassifier.ImageClassifierOptions.builder()
            .setBaseOptions(baseOptions)
            .setMaxResults(maxResult)
            .setScoreThreshold(thresholds)
            .build()

        try {
            imageClassifier =
                ImageClassifier.createFromFileAndOptions(context, "asia-imagecx.tflite", options)
        } catch (e: Exception) {
            Log.e("Error", "imageClassifier Failed: ${e.localizedMessage}")
        }
    }

    override fun classify(bitmap: Bitmap, rotation: Int): List<Classification> {
        if (imageClassifier == null)
            setupClassifier()

        val imageProcessor = ImageProcessor.Builder().build()
        val tensorImage = imageProcessor.process(TensorImage.fromBitmap(bitmap))

        val imageProcessingOptions = ImageProcessingOptions.builder()
            .setOrientation(getOrientationFromRotation(rotation))
            .build()

        val result = imageClassifier?.classify(tensorImage, imageProcessingOptions)

        return result?.flatMap { classifications ->
            classifications.categories.map {
                Classification(
                    name = it.displayName,
                    score = it.score,
                )
            }
        }?.distinctBy { it.name }?.toList() ?: emptyList()
    }

    private fun getOrientationFromRotation(rotation: Int): ImageProcessingOptions.Orientation {
        return when (rotation) {
            Surface.ROTATION_270 -> ImageProcessingOptions.Orientation.BOTTOM_RIGHT
            Surface.ROTATION_90 -> ImageProcessingOptions.Orientation.TOP_LEFT
            Surface.ROTATION_180 -> ImageProcessingOptions.Orientation.RIGHT_BOTTOM
            else -> ImageProcessingOptions.Orientation.RIGHT_TOP
        }
    }

}