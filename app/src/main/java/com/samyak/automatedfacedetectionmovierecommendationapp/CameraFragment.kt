package com.samyak.automatedfacedetectionmovierecommendationapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraFragment : Fragment() {

    private val viewModel: MoodViewModel by activityViewModels()
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var previewView: PreviewView
    private lateinit var moodTextView: TextView
    private lateinit var faceDetector: FaceDetector
    
    private var lastMoodUpdateTime = 0L
    private val MOOD_UPDATE_INTERVAL = 1000 // Update mood every 1 second

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startCamera()
        } else {
            Toast.makeText(context, "Camera permission is required", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        previewView = view.findViewById(R.id.preview_view)
        moodTextView = view.findViewById(R.id.mood_text)

        // Initialize face detector
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .enableTracking()
            .build()
        
        faceDetector = FaceDetection.getClient(options)
        cameraExecutor = Executors.newSingleThreadExecutor()

        // Request camera permission
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // Set up the preview use case
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            // Set up the image analyzer
            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            imageAnalysis.setAnalyzer(cameraExecutor, ImageAnalyzer())

            try {
                // Unbind all use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera - front camera
                cameraProvider.bindToLifecycle(
                    this,
                    CameraSelector.DEFAULT_FRONT_CAMERA,
                    preview,
                    imageAnalysis
                )
            } catch (e: Exception) {
                Log.e(TAG, "Use case binding failed", e)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }
    
    private inner class ImageAnalyzer : ImageAnalysis.Analyzer {
        @OptIn(ExperimentalGetImage::class)
        override fun analyze(imageProxy: ImageProxy) {
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image = InputImage.fromMediaImage(
                    mediaImage,
                    imageProxy.imageInfo.rotationDegrees
                )
                
                // Only update mood at specified intervals to avoid overwhelming the UI
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastMoodUpdateTime > MOOD_UPDATE_INTERVAL) {
                    // Process the image
                    faceDetector.process(image)
                        .addOnSuccessListener { faces ->
                            if (faces.isNotEmpty()) {
                                val face = faces[0] // Get the first detected face

                                // Analyze facial expressions for mood
                                val smileProbability = face.smilingProbability ?: 0f
                                val rightEyeOpenProbability = face.rightEyeOpenProbability ?: 0f
                                val leftEyeOpenProbability = face.leftEyeOpenProbability ?: 0f

                                // Determine mood based on facial expressions
                                val mood = when {
                                    smileProbability > 0.7 -> "happy"
                                    smileProbability < 0.2 -> "sad"
                                    (rightEyeOpenProbability + leftEyeOpenProbability) / 2 < 0.5 -> "tired"
                                    else -> "neutral"
                                }

                                // Update UI with detected mood
                                activity?.runOnUiThread {
                                    moodTextView.text = "Detected Mood: ${mood.capitalize()}"
                                    // Update ViewModel with the detected mood
                                    viewModel.updateMood(mood)
                                }
                                
                                lastMoodUpdateTime = currentTime
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e(TAG, "Face detection failed", e)
                        }
                        .addOnCompleteListener {
                            imageProxy.close()
                        }
                } else {
                    imageProxy.close()
                }
            } else {
                imageProxy.close()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraFragment"
    }
}

// Extension function to capitalize first letter
fun String.capitalize(): String {
    return this.replaceFirstChar { it.uppercase() }
}

// Enhance the processFaceDetection method to detect more emotions
private fun processFaceDetection(faces: List<Face>) {
    if (faces.isEmpty()) {
//        moodTextView.text = "No face detected"
        return
    }

    val face = faces[0] // Get the first face
    
    // Enhanced emotion detection
    val smileProb = face.smilingProbability ?: 0f
    val rightEyeOpenProb = face.rightEyeOpenProbability ?: 0f
    val leftEyeOpenProb = face.leftEyeOpenProbability ?: 0f
    
    val mood = when {
        smileProb > 0.8 -> "Very Happy"
        smileProb > 0.5 -> "Happy"
        smileProb < 0.1 && (rightEyeOpenProb < 0.5 || leftEyeOpenProb < 0.5) -> "Tired"
        smileProb < 0.2 -> "Sad"
        else -> "Neutral"
    }
    
//    moodTextView.text = "Detected Mood: $mood"
//    viewModel.setCurrentMood(mood)
}