package com.samyak.automatedfacedetectionmovierecommendationapp

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButtonToggleGroup
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
import com.samyak.automatedfacedetectionmovierecommendationapp.adapter.MovieCircleAdapter
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraFragment : Fragment() {

    private val viewModel: MoodViewModel by activityViewModels()
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var previewView: PreviewView
    private lateinit var moodTextView: TextView
    private lateinit var faceDetector: FaceDetector
    private lateinit var faceMeshOverlay: FaceMeshOverlayView
    private lateinit var modeToggleGroup: MaterialButtonToggleGroup
    private lateinit var movieRecommendationsCard: CardView
    private lateinit var movieRecommendationsRecycler: RecyclerView
    private lateinit var movieCircleAdapter: MovieCircleAdapter

    private var lastMoodUpdateTime = 0L
    private val MOOD_UPDATE_INTERVAL = 1000L // Update mood every 1 second
    
    // Face detection modes
    private enum class DetectionMode {
        CLOSE_RANGE,  // Face must be within ~2 meters
        FAR_RANGE     // Face must be at least 100x100 pixels
    }
    
    private var currentMode = DetectionMode.CLOSE_RANGE
    private val MIN_FACE_SIZE_PX = 100 // Minimum face size in pixels for far range mode
    private val MAX_DISTANCE_ESTIMATE_CM = 200 // ~2 meters in cm for close range mode
    private val MIN_CONFIDENCE_THRESHOLD = 0.7f // Minimum confidence for classifications

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startCamera()
        } else {
            Toast.makeText(context, "Camera permission is required for face detection", Toast.LENGTH_LONG).show()
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

        initializeViews(view)
        setupModeToggle()
        setupMovieRecommendations()
        initializeFaceDetector()
        initializeCameraExecutor()
        requestCameraPermission()
        
        // Observe movie recommendations
        viewModel.movieRecommendations.observe(viewLifecycleOwner) { movies ->
            movieCircleAdapter.updateMovies(movies)
        }
    }

    private fun initializeViews(view: View) {
        previewView = view.findViewById(R.id.preview_view)
        moodTextView = view.findViewById(R.id.mood_text)
        faceMeshOverlay = view.findViewById(R.id.face_mesh_overlay)
        modeToggleGroup = view.findViewById(R.id.mode_toggle_group)
        movieRecommendationsCard = view.findViewById(R.id.movie_recommendations_card)
        movieRecommendationsRecycler = view.findViewById(R.id.movie_recommendations_recycler)
    }

    private fun setupModeToggle() {
        modeToggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                currentMode = when (checkedId) {
                    R.id.btn_close_range -> DetectionMode.CLOSE_RANGE
                    R.id.btn_far_range -> DetectionMode.FAR_RANGE
                    else -> currentMode
                }
                updateModeDisplay()
            }
        }
    }

    private fun setupMovieRecommendations() {
        // Initialize the adapter with a click handler that navigates to MovieDetailFragment
        // When a movie is clicked, we use the newInstance method to pass all movie data
        // This ensures all movie details are properly displayed in the detail view
        movieCircleAdapter = MovieCircleAdapter(emptyList()) { movie ->
            // Navigate to movie details when clicked using the newInstance method
            val movieDetailFragment = MovieDetailFragment.newInstance(movie)
            
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, movieDetailFragment)
                .addToBackStack(null)
                .commit()
        }
        movieRecommendationsRecycler.adapter = movieCircleAdapter
    }

    private fun initializeFaceDetector() {
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .setMinFaceSize(0.1f) // Minimum face size relative to image
            .enableTracking()
            .build()

        faceDetector = FaceDetection.getClient(options)
    }

    private fun initializeCameraExecutor() {
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun requestCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) 
                == PackageManager.PERMISSION_GRANTED -> {
                startCamera()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()
                bindCameraUseCases(cameraProvider)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to start camera", e)
                showErrorMessage("Failed to start camera: ${e.message}")
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun bindCameraUseCases(cameraProvider: ProcessCameraProvider) {
        try {
            // Set up the preview use case
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            // Set up the image analyzer
            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, ImageAnalyzer())
                }

            // Unbind all use cases before rebinding
            cameraProvider.unbindAll()

            // Bind use cases to camera - front camera for selfie mode
            cameraProvider.bindToLifecycle(
                this,
                CameraSelector.DEFAULT_FRONT_CAMERA,
                preview,
                imageAnalysis
            )

        } catch (e: Exception) {
            Log.e(TAG, "Use case binding failed", e)
            showErrorMessage("Camera setup failed: ${e.message}")
        }
    }

    private fun isFaceValid(face: Face, imageWidth: Int, imageHeight: Int): Boolean {
        val bounds = face.boundingBox
        val faceWidth = bounds.width()
        val faceHeight = bounds.height()
        
        // Ensure face is within image bounds
        if (bounds.left < 0 || bounds.top < 0 || 
            bounds.right > imageWidth || bounds.bottom > imageHeight) {
            return false
        }
        
        return when (currentMode) {
            DetectionMode.CLOSE_RANGE -> {
                // For close range, estimate distance based on face size
                val avgFaceSize = (faceWidth + faceHeight) / 2f
                val faceArea = faceWidth * faceHeight
                val imageArea = imageWidth * imageHeight
                val faceRatio = faceArea.toFloat() / imageArea.toFloat()
                
                // Face should be reasonably sized (not too far)
                faceRatio > 0.01f && avgFaceSize > 50
            }
            DetectionMode.FAR_RANGE -> {
                // For far range, check minimum face size
                faceWidth >= MIN_FACE_SIZE_PX && faceHeight >= MIN_FACE_SIZE_PX
            }
        }
    }

    private fun analyzeMood(face: Face): String {
        val smileProbability = face.smilingProbability ?: 0f
        val rightEyeOpenProbability = face.rightEyeOpenProbability ?: 0f
        val leftEyeOpenProbability = face.leftEyeOpenProbability ?: 0f
        val avgEyeOpenProb = (rightEyeOpenProbability + leftEyeOpenProbability) / 2f

        return when {
            smileProbability > 0.8f -> "very_happy"
            smileProbability > 0.5f -> "happy"
            smileProbability < 0.1f && avgEyeOpenProb < 0.3f -> "sleepy"
            smileProbability < 0.2f && avgEyeOpenProb > 0.7f -> "sad"
            avgEyeOpenProb < 0.4f -> "tired"
            smileProbability > 0.3f && smileProbability < 0.6f -> "content"
            else -> "neutral"
        }
    }

    private fun updateModeDisplay() {
        val modeText = when (currentMode) {
            DetectionMode.CLOSE_RANGE -> "Close Range Mode"
            DetectionMode.FAR_RANGE -> "Far Range Mode"
        }
        
        // Update the face mesh overlay with the current detection mode
        faceMeshOverlay.setDetectionMode(currentMode.name)
        
        activity?.runOnUiThread {
            moodTextView.text = "$modeText\nSearching for faces..."
        }
    }

    private fun showErrorMessage(message: String) {
        activity?.runOnUiThread {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            moodTextView.text = "Error: $message"
        }
    }

    private inner class ImageAnalyzer : ImageAnalysis.Analyzer {
        @OptIn(ExperimentalGetImage::class)
        override fun analyze(imageProxy: ImageProxy) {
            val mediaImage = imageProxy.image
            if (mediaImage == null) {
                imageProxy.close()
                return
            }

            try {
                val image = InputImage.fromMediaImage(
                    mediaImage,
                    imageProxy.imageInfo.rotationDegrees
                )

                // Only update mood at specified intervals to avoid overwhelming the UI
                val currentTime = System.currentTimeMillis()
                val shouldUpdateMood = currentTime - lastMoodUpdateTime > MOOD_UPDATE_INTERVAL

                if (shouldUpdateMood) {
                    processFaceDetection(image, imageProxy, currentTime)
                } else {
                    imageProxy.close()
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error processing image", e)
                imageProxy.close()
            }
        }

        private fun processFaceDetection(image: InputImage, imageProxy: ImageProxy, currentTime: Long) {
            faceDetector.process(image)
                .addOnSuccessListener { faces ->
                    handleFaceDetectionSuccess(faces, image, currentTime)
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Face detection failed", e)
                    activity?.runOnUiThread {
                        moodTextView.text = "Face detection error"
                    }
                }
                .addOnCompleteListener {
                    // Always close the image proxy
                    imageProxy.close()
                }
        }

        private fun handleFaceDetectionSuccess(faces: List<Face>, image: InputImage, currentTime: Long) {
            val validFaces = faces.filter { 
                isFaceValid(it, image.width, image.height) 
            }
            
            // Update face mesh overlay on UI thread
            activity?.runOnUiThread {
                faceMeshOverlay.setFaces(validFaces, image.width, image.height)
            }
            
            if (validFaces.isNotEmpty()) {
                val primaryFace = validFaces[0] // Get the first valid face
                val mood = analyzeMood(primaryFace)
                
                // Update UI with detected mood and face count
                activity?.runOnUiThread {
                    val modeText = when (currentMode) {
                        DetectionMode.CLOSE_RANGE -> "Close Range"
                        DetectionMode.FAR_RANGE -> "Far Range"
                    }
                    moodTextView.text = "$modeText: ${validFaces.size} face(s) detected\nMood: ${mood.formatMoodText()}"
                    viewModel.updateMood(mood)
                    
                    // Show movie recommendations when face is detected
                    movieRecommendationsCard.visibility = View.VISIBLE
                }
                lastMoodUpdateTime = currentTime
            } else {
                activity?.runOnUiThread {
                    val modeText = when (currentMode) {
                        DetectionMode.CLOSE_RANGE -> "Close Range"
                        DetectionMode.FAR_RANGE -> "Far Range"
                    }
                    moodTextView.text = "$modeText: No valid faces detected"
                    
                    // Hide movie recommendations when no face is detected
                    movieRecommendationsCard.visibility = View.GONE
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            cameraExecutor.shutdown()
            faceDetector.close()
        } catch (e: Exception) {
            Log.e(TAG, "Error during cleanup", e)
        }
    }

    companion object {
        private const val TAG = "CameraFragment"
    }
}

// Extension function to format mood text for display
private fun String.formatMoodText(): String {
    return this.replace("_", " ").split(" ").joinToString(" ") { word ->
        word.replaceFirstChar { it.uppercase() }
    }
}

// Extension function to capitalize first letter (keeping original functionality)
fun String.capitalize(): String {
    return this.replaceFirstChar { it.uppercase() }
}