package dev.eknath.tensorflowsampleapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import dev.eknath.tensorflowsampleapp.data.TFLiteClassifier
import dev.eknath.tensorflowsampleapp.domain.Classification
import dev.eknath.tensorflowsampleapp.presentation.CameraPreview
import dev.eknath.tensorflowsampleapp.presentation.LandmarkImageAnalyzer
import dev.eknath.tensorflowsampleapp.ui.theme.TensorFlowSampleAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!hasCameraPermissions())
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 0)

        setContent {
            TensorFlowSampleAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var classification by remember {
                        mutableStateOf(emptyList<Classification>())
                    }
                    val analyzer = remember {
                        LandmarkImageAnalyzer(
                            classifier = TFLiteClassifier(context = applicationContext),
                            onResult = { classifications ->
                                classification = classifications
                                Log.e("Test", "UPDATING")
                            }
                        )
                    }

                    val controller = remember {
                        LifecycleCameraController(applicationContext).apply {
                            setEnabledUseCases(CameraController.IMAGE_ANALYSIS)
                            setImageAnalysisAnalyzer(
                                ContextCompat.getMainExecutor(applicationContext),
                                analyzer
                            )
                        }
                    }

                    Box(modifier = Modifier.fillMaxSize()) {
                        CameraPreview(controller = controller, modifier = Modifier.fillMaxSize())
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .height(50.dp)
                                .background(Color.Black.copy(alpha = 0.8f))
                        ) {
                            Text(
                                text = "Name: ${classification.firstOrNull()?.name ?: "-"}  Score: ${classification.firstOrNull()?.score ?: "-"}",
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }

                }
            }
        }
    }

    fun hasCameraPermissions() = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED
}

