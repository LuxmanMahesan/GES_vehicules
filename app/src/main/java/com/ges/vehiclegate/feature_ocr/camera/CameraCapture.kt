package com.ges.vehiclegate.feature_ocr.camera

import android.content.Context
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.ges.vehiclegate.feature_ocr.ocr.PlateOcrService
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.ges.vehiclegate.feature_ocr.ocr.PlateTextParser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraCapture(
    onClose: () -> Unit,
    onImageCaptured: (String) -> Unit,
    onError: (Throwable) -> Unit,
    onPlateDetected: (String) -> Unit

) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    val ocrService = remember { PlateOcrService() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Caméra") },
                navigationIcon = {
                    IconButton(onClick = onClose) { Text("←") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                factory = { ctx ->
                    val previewView = PreviewView(ctx)

                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()

                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }

                        val capture = ImageCapture.Builder()
                            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                            .build()


                        val executor = ContextCompat.getMainExecutor(ctx)

                        val analyzer = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                            .also {
                                it.setAnalyzer(executor) { imageProxy ->
                                    processFrame(imageProxy, onPlateDetected)
                                }
                            }


                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                capture,
                                analyzer
                            )
                            imageCapture = capture
                        } catch (t: Throwable) {
                            onError(t)
                        }
                    }, ContextCompat.getMainExecutor(ctx))

                    previewView
                }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = onClose
                ) { Text("Annuler") }

                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val cap = imageCapture ?: return@Button
                        takePhoto(
                            context = context,
                            imageCapture = cap,
                            onImageCaptured = onImageCaptured,
                            onError = onError
                        )
                    }
                ) { Text("Capturer") }
            }
        }
    }
}

private fun takePhoto(
    context: Context,
    imageCapture: ImageCapture,
    onImageCaptured: (String) -> Unit,
    onError: (Throwable) -> Unit
) {
    val outputDir = File(context.filesDir, "vehicle_photos").apply { mkdirs() }
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val photoFile = File(outputDir, "IMG_$timeStamp.jpg")

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
    val executor = ContextCompat.getMainExecutor(context)

    imageCapture.takePicture(
        outputOptions,
        executor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                onImageCaptured(photoFile.absolutePath)
            }

            override fun onError(exception: ImageCaptureException) {
                onError(exception)
            }
        }
    )
}

@androidx.annotation.OptIn(ExperimentalGetImage::class)
private fun processFrame(
    imageProxy: ImageProxy,
    onPlateDetected: (String) -> Unit
) {
    val mediaImage = imageProxy.image
    if (mediaImage != null) {
        val rotation = imageProxy.imageInfo.rotationDegrees
        val image = InputImage.fromMediaImage(mediaImage, rotation)

        // OCR ML Kit
        val recognizer = com.google.mlkit.vision.text.TextRecognition.getClient(
            com.google.mlkit.vision.text.latin.TextRecognizerOptions.DEFAULT_OPTIONS
        )

        recognizer.process(image)
            .addOnSuccessListener { result ->
                val rawText = result.text
                val plate = PlateTextParser.extractBestPlate(rawText)
                if (plate != null) {
                    onPlateDetected(plate)
                }
            }
            .addOnCompleteListener {
                imageProxy.close()
                recognizer.close()
            }
    } else {
        imageProxy.close()
    }
}

