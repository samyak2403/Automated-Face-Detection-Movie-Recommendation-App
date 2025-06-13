package com.samyak.automatedfacedetectionmovierecommendationapp

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.*
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
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
import java.util.Locale
import java.util.Queue
import java.util.LinkedList
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraFragment : Fragment(), TextToSpeech.OnInitListener {

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
    
    // Text-to-Speech variables
    private lateinit var textToSpeech: TextToSpeech
    private var isTtsReady = false
    private var lastSpokenMood = ""
    private val SPEECH_INTERVAL = 3000L // Speak mood every 3 seconds
    private val speechQueue: Queue<String> = LinkedList()
    private var isSpeaking = false
    private val speechHandler = Handler(Looper.getMainLooper())

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
        initializeTextToSpeech()
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
    
    private fun initializeTextToSpeech() {
        textToSpeech = TextToSpeech(context, this)
        textToSpeech.setSpeechRate(0.95f)  // Slightly slower speech rate for better clarity
    }
    
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = textToSpeech.setLanguage(Locale.US)
            
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e(TAG, "Language not supported")
            } else {
                isTtsReady = true
                
                // Set up the utterance progress listener
                textToSpeech.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String) {
                        Log.d(TAG, "Speech started: $utteranceId")
                        isSpeaking = true
                    }

                    override fun onDone(utteranceId: String) {
                        Log.d(TAG, "Speech completed: $utteranceId")
                        speechHandler.post {
                            isSpeaking = false
                            processSpeechQueue()
                        }
                    }

                    override fun onError(utteranceId: String) {
                        Log.e(TAG, "Speech error: $utteranceId")
                        speechHandler.post {
                            isSpeaking = false
                            processSpeechQueue()
                        }
                    }
                })
                
                // Add welcome message to queue
                queueSpeech("Welcome to the Movie Recommendation App. I'll analyze your facial expressions to suggest movies that match your mood.")
                
                // Add mode explanation after a delay
                speechHandler.postDelayed({
                    queueSpeech(getDetailedModeExplanation())
                }, 1000)
            }
        } else {
            Log.e(TAG, "TextToSpeech initialization failed")
        }
    }
    
    private fun queueSpeech(text: String) {
        if (!text.isNullOrBlank()) {
            speechQueue.add(text)
            processSpeechQueue()
        }
    }
    
    private fun processSpeechQueue() {
        if (!isSpeaking && speechQueue.isNotEmpty() && isTtsReady) {
            val textToSpeak = speechQueue.poll()
            if (!textToSpeak.isNullOrBlank()) {
                val utteranceId = "speech_${System.currentTimeMillis()}"
                textToSpeech.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
                isSpeaking = true
            }
        }
    }
    
    private fun getDetailedModeExplanation(): String {
        return when (currentMode) {
            DetectionMode.CLOSE_RANGE -> 
                "You are in Close Range Mode. This mode works best when you are within two meters of the camera. " +
                "Your facial expressions will be analyzed in detail to provide accurate movie recommendations."
                
            DetectionMode.FAR_RANGE -> 
                "You are in Far Range Mode. This mode allows detection from a greater distance. " +
                "Make sure your face is clearly visible to the camera for accurate mood detection."
        }
    }
    
    private fun speakMood(mood: String) {
        if (mood == lastSpokenMood) {
            return // Don't repeat the same mood
        }
        
        val speechText = when (mood) {
            "very_happy" -> "I detect you're feeling very happy! Here are some uplifting movies that match your excellent mood."
            "happy" -> "You look happy! I've selected some feel-good movies that complement your positive mood."
            "sleepy" -> "You seem sleepy. I've found some relaxing movies that won't require too much attention."
            "sad" -> "I notice you're feeling a bit down. These movies might help cheer you up."
            "tired" -> "You look tired. Here are some light-hearted movies that might rejuvenate your mood."
            "content" -> "You appear content and relaxed. These movies should maintain your pleasant mood."
            "neutral" -> "I see a neutral expression. Here's a balanced selection of movies for you."
            else -> "Here are some personalized movie recommendations based on your current mood."
        }
        
        queueSpeech(speechText)
        lastSpokenMood = mood
    }

    private fun setupModeToggle() {
        modeToggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                val previousMode = currentMode
                currentMode = when (checkedId) {
                    R.id.btn_close_range -> DetectionMode.CLOSE_RANGE
                    R.id.btn_far_range -> DetectionMode.FAR_RANGE
                    else -> currentMode
                }
                
                // Only announce if mode actually changed
                if (previousMode != currentMode) {
                    updateModeDisplay()
                    
                    // Enhanced mode change announcement
                    val modeText = when (currentMode) {
                        DetectionMode.CLOSE_RANGE -> "Switching to Close Range Mode. Please position your face within two meters of the camera for best results."
                        DetectionMode.FAR_RANGE -> "Switching to Far Range Mode. You can now be detected from a greater distance."
                    }
                    queueSpeech(modeText)
                }
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
                
                // Announce that the camera is ready and explain how the app works
                queueSpeech("Camera is ready. I'll detect your facial expressions and recommend movies based on your mood. Please look at the camera.")
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

        val mood = when {
            smileProbability > 0.8f -> "very_happy"
            smileProbability > 0.5f -> "happy"
            smileProbability < 0.1f && avgEyeOpenProb < 0.3f -> "sleepy"
            smileProbability < 0.2f && avgEyeOpenProb > 0.7f -> "sad"
            avgEyeOpenProb < 0.4f -> "tired"
            smileProbability > 0.3f && smileProbability < 0.6f -> "content"
            else -> "neutral"
        }
        
        // Log the mood detection values for debugging
        Log.d(TAG, "Mood Analysis - Smile: $smileProbability, Eye Open Avg: $avgEyeOpenProb, Mood: $mood")
        
        return mood
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
        
        // Also speak the error
        queueSpeech("Error: $message")
    }

    private inner class ImageAnalyzer : ImageAnalysis.Analyzer {
        private var lastModeAnnouncement = 0L
        private val MODE_ANNOUNCEMENT_INTERVAL = 10000L // 10 seconds between mode announcements
        private var previousMood: String? = null
        private var lastSpeechTime = 0L
        private val MIN_SPEECH_INTERVAL = 1500L // Minimum time between speech outputs
        
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
                
                // Check if mood has changed significantly
                val isMoodChanged = previousMood != null && previousMood != mood
                val isFirstDetection = previousMood == null
                
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
                    
                    // Speak immediately for first detection or mood changes
                    // Otherwise use a reasonable interval to avoid too frequent announcements
                    val currentSpeechTime = System.currentTimeMillis()
                    val timeSinceLastSpeech = currentSpeechTime - lastSpeechTime
                    
                    if (isFirstDetection || isMoodChanged || timeSinceLastSpeech > MIN_SPEECH_INTERVAL) {
                        // Clear the speech queue for immediate feedback
                        if (isMoodChanged || isFirstDetection) {
                            // For important changes, interrupt current speech
                            textToSpeech.stop()
                            speechQueue.clear()
                            isSpeaking = false
                            
                            if (isMoodChanged) {
                                // Announce mood transition
                                val transitionText = "Your mood has changed from ${previousMood?.formatMoodText() ?: "unknown"} to ${mood.formatMoodText()}."
                                queueSpeech(transitionText)
                            }
                        }
                        
                        // Speak the mood with high priority
                        speakMoodImmediately(mood)
                        lastSpeechTime = currentSpeechTime
                    }
                }
                previousMood = mood
                lastMoodUpdateTime = currentTime
                // Reset last mode announcement when a face is detected
                lastModeAnnouncement = 0L
            } else {
                activity?.runOnUiThread {
                    val modeText = when (currentMode) {
                        DetectionMode.CLOSE_RANGE -> "Close Range"
                        DetectionMode.FAR_RANGE -> "Far Range"
                    }
                    moodTextView.text = "$modeText: No valid faces detected"
                    
                    // Hide movie recommendations when no face is detected
                    movieRecommendationsCard.visibility = View.GONE
                    
                    // Reset last spoken mood when no face is detected
                    lastSpokenMood = ""
                    previousMood = null
                    
                    // Periodically remind user about current mode if no faces are detected
                    if (currentTime - lastModeAnnouncement > MODE_ANNOUNCEMENT_INTERVAL) {
                        val reminderText = when (currentMode) {
                            DetectionMode.CLOSE_RANGE -> "No face detected in Close Range Mode. Please position yourself within two meters of the camera."
                            DetectionMode.FAR_RANGE -> "No face detected in Far Range Mode. Please ensure you are visible to the camera."
                        }
                        queueSpeech(reminderText)
                        lastModeAnnouncement = currentTime
                    }
                }
            }
        }
    }

    private fun speakMoodImmediately(mood: String) {
        if (mood == lastSpokenMood) {
            return // Don't repeat the same mood
        }
        
        val speechText = when (mood) {
            "very_happy" -> "I detect you're feeling very happy! Here are some uplifting movies that match your excellent mood."
            "happy" -> "You look happy! I've selected some feel-good movies that complement your positive mood."
            "sleepy" -> "You seem sleepy. I've found some relaxing movies that won't require too much attention."
            "sad" -> "I notice you're feeling a bit down. These movies might help cheer you up."
            "tired" -> "You look tired. Here are some light-hearted movies that might rejuvenate your mood."
            "content" -> "You appear content and relaxed. These movies should maintain your pleasant mood."
            "neutral" -> "I see a neutral expression. Here's a balanced selection of movies for you."
            else -> "Here are some personalized movie recommendations based on your current mood."
        }
        
        // Speak immediately with high priority
        if (isTtsReady) {
            textToSpeech.speak(speechText, TextToSpeech.QUEUE_FLUSH, null, "mood_immediate_${System.currentTimeMillis()}")
            isSpeaking = true
        }
        
        lastSpokenMood = mood
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            // Announce app closing
            queueSpeech("Thank you for using the Movie Recommendation App. Goodbye!")
            
            // Add a small delay to allow the speech to complete before shutdown
            Thread.sleep(1500)
            
            // Shutdown TTS
            if (::textToSpeech.isInitialized) {
                textToSpeech.stop()
                textToSpeech.shutdown()
            }
            
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