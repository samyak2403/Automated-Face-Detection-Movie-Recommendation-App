# 🎬 CineMood - AI-Powered Movie Recommendation App

## 📖 Project Overview

CineMood is an innovative Android application that combines artificial intelligence with entertainment to provide personalized movie recommendations based on the user's current mood. Using Google's ML Kit for real-time face detection and emotion analysis, the app intelligently suggests movies that match how the user is feeling.

## 🏗️ Technical Architecture

### 📁 Project Structure

```
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/samyak/automatedfacedetectionmovierecommendationapp/
│   │   │   │   ├── adapter/
│   │   │   │   ├── utils/
│   │   │   │   ├── MainActivity.kt
│   │   │   │   ├── SplashActivity.kt
│   │   │   │   ├── CameraFragment.kt
│   │   │   │   ├── RecommendationsFragment.kt
│   │   │   │   ├── ProfileFragment.kt
│   │   │   │   ├── MovieDetailFragment.kt
│   │   │   │   ├── FaceMeshOverlayView.kt
│   │   │   │   ├── Movie.kt
│   │   │   │   ├── MovieAdapter.kt
│   │   │   │   ├── MovieRepository.kt
│   │   │   │   ├── OmdbApiService.kt
│   │   │   │   ├── MoodViewModel.kt
│   │   │   │   ├── MoodHistoryManager.kt
│   │   │   │   └── MoodHistoryAdapter.kt
│   │   │   ├── res/
│   │   │   └── AndroidManifest.xml
```

### 🏛️ Architecture Pattern

The app follows the MVVM (Model-View-ViewModel) architecture pattern:

- **Model**: Movie.kt, MovieRepository.kt, MoodHistoryManager.kt
- **View**: Activity and Fragment classes, custom views
- **ViewModel**: MoodViewModel.kt

## 🧩 Core Components

### 1. 😀 Face Detection & Mood Analysis

**Key Files:**
- `CameraFragment.kt`: Handles camera preview and face detection
- `FaceMeshOverlayView.kt`: Custom view for displaying face mesh
- `MoodViewModel.kt`: Manages mood detection state

The app uses Google ML Kit's Face Detection API to analyze facial expressions and determine the user's mood. It can detect 7 different emotional states:
- 😄 Very Happy
- 😊 Happy
- 😴 Sleepy
- 😢 Sad
- 😵 Tired
- 😌 Content
- 😐 Neutral

#### 🔍 Face Detection Implementation Details

The face detection system uses Google's ML Kit with two detection modes:

1. **📱 Close Range Mode**:
   - Optimized for faces within 2 meters of the camera
   - Uses high-accuracy face detection options
   - Provides detailed facial expression analysis

2. **👨‍👩‍👧‍👦 Far Range Mode**:
   - Detects faces at greater distances
   - Requires minimum face size of 100x100 pixels
   - Optimized for group settings

```kotlin
// Face detector initialization code from CameraFragment.kt
private fun initializeFaceDetector() {
    val options = when (currentMode) {
        DetectionMode.CLOSE_RANGE -> {
            FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .setMinFaceSize(0.15f)  // For close-up detection
                .build()
        }
        DetectionMode.FAR_RANGE -> {
            FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .setMinFaceSize(0.1f)  // For far distance detection
                .build()
        }
    }
    
    faceDetector = FaceDetection.getClient(options)
}
```

#### 🧠 Mood Analysis Algorithm

The mood detection algorithm analyzes multiple facial features to determine the user's emotional state:

1. **😊 Smile Probability**: Detects various levels of happiness
2. **👁️ Eye Openness**: Identifies tiredness or sleepiness
3. **👤 Facial Landmarks**: Uses 8 key points for accurate analysis
4. **📊 Confidence Scoring**: Ensures reliable results

```kotlin
// Example of mood determination logic
private fun determineMood(face: Face): String {
    val smileProbability = face.smilingProbability ?: 0f
    val leftEyeOpenProbability = face.leftEyeOpenProbability ?: 0f
    val rightEyeOpenProbability = face.rightEyeOpenProbability ?: 0f
    
    val eyeOpenness = (leftEyeOpenProbability + rightEyeOpenProbability) / 2
    
    return when {
        smileProbability > 0.8 -> "very happy"
        smileProbability > 0.6 -> "happy"
        eyeOpenness < 0.3 -> "sleepy"
        eyeOpenness < 0.5 && smileProbability < 0.2 -> "sad"
        eyeOpenness < 0.7 -> "tired"
        smileProbability > 0.3 -> "content"
        else -> "neutral"
    }
}
```

### 2. 🎥 Movie Recommendations

**Key Files:**
- `MovieRepository.kt`: Handles movie data retrieval
- `OmdbApiService.kt`: Interface for the OMDB API
- `RecommendationsFragment.kt`: Displays movie recommendations
- `MovieDetailFragment.kt`: Shows detailed movie information

The app integrates with movie APIs to provide recommendations based on the detected mood. Movies are filtered and categorized according to the user's emotional state.

#### 📋 Movie Data Model

The `Movie` data class represents movie information retrieved from the OMDB API:

```kotlin
data class Movie(
    @SerializedName("imdbID") val imdbID: String,
    @SerializedName("Title") val title: String,
    @SerializedName("Year") val year: String,
    @SerializedName("Type") val type: String,
    @SerializedName("Poster") val poster: String,
    @SerializedName("Plot") val plot: String? = null,
    @SerializedName("Rated") val rated: String? = null,
    @SerializedName("Runtime") val runtime: String? = null,
    @SerializedName("Genre") val genre: String? = null,
    @SerializedName("imdbRating") val imdbRating: String? = null,
    @SerializedName("Director") val director: String? = null,
    @SerializedName("Writer") val writer: String? = null,
    @SerializedName("Actors") val actors: String? = null,
    @SerializedName("Language") val language: String? = null,
    @SerializedName("Country") val country: String? = null,
    @SerializedName("Awards") val awards: String? = null
)
```

#### 🎯 Movie Recommendation Engine

The recommendation engine maps detected moods to appropriate movie genres:

```kotlin
// From MoodViewModel.kt
private fun fetchMovieRecommendations(mood: String) {
    _isLoading.value = true
    _error.value = null

    viewModelScope.launch {
        try {
            // Map mood to search query
            val searchQuery = when (mood.lowercase()) {
                "happy" -> "comedy"
                "sad" -> "drama"
                "tired" -> "family"
                else -> "action" // Default fallback
            }

            val movies = movieRepository.searchMovies(searchQuery)
            _movieRecommendations.value = movies
        } catch (e: Exception) {
            _error.value = "Failed to load recommendations: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }
}
```

#### 🌐 API Integration

The app uses Retrofit to communicate with the OMDB API:

```kotlin
interface OmdbApiService {
    @GET("/")
    suspend fun searchMovies(
        @Query("s") searchTerm: String,
        @Query("type") type: String = "movie",
        @Query("apikey") apiKey: String = "1065bd81" // Free API key
    ): SearchResponse
    
    @GET("/")
    suspend fun getMovieDetail(
        @Query("i") imdbId: String,
        @Query("plot") plot: String = "full",
        @Query("apikey") apiKey: String = "1065bd81" // Free API key
    ): Response<Movie>
}
```

#### 📵 Offline Support

The `MovieRepository` implements caching to support offline functionality:

```kotlin
suspend fun searchMovies(searchTerm: String): List<Movie> {
    // Check cache first
    movieCache[searchTerm]?.let { cachedMovies ->
        return cachedMovies
    }
    
    // Check network connectivity
    if (context != null && !isNetworkAvailable(context)) {
        // Return any cached movies for any mood if offline
        val anyCachedMovies = movieCache.values.firstOrNull()
        if (!anyCachedMovies.isNullOrEmpty()) {
            return anyCachedMovies
        }
        throw Exception("No internet connection and no cached movies available")
    }
    
    // If not in cache, make API call
    val response = api.searchMovies(searchTerm)
    if (response.response == "True") {
        val movies = response.search ?: emptyList()
        // Cache the results
        movieCache[searchTerm] = movies
        return movies
    } else {
        throw Exception(response.error ?: "Unknown error")
    }
}
```

### 3. 🎨 User Interface

**Key Files:**
- `SplashActivity.kt`: Entry point with animations
- `MainActivity.kt`: Main navigation host
- `MovieAdapter.kt`: RecyclerView adapter for movie lists
- `MoodHistoryAdapter.kt`: RecyclerView adapter for mood history

The UI is built using Material Design 3 principles with responsive layouts, beautiful animations, and support for both light and dark themes.

#### 🔄 User Experience Flow

1. **✨ Splash Screen**: The app begins with an animated splash screen that introduces key features.
2. **📷 Camera View**: Users can see themselves with a face mesh overlay that provides real-time feedback.
3. **🧐 Mood Detection**: The app analyzes facial expressions and displays the detected mood.
4. **🍿 Movie Recommendations**: Based on the detected mood, the app suggests appropriate movies.
5. **📝 Movie Details**: Users can tap on any movie to see detailed information.
6. **👤 Profile & History**: Users can view their mood history and app settings.

#### ♿ Accessibility Features

- **🔊 Text-to-Speech**: Provides audio feedback about detected moods
- **🎨 High Contrast UI**: Ensures readability in various lighting conditions
- **📱 Responsive Layout**: Adapts to different screen sizes and orientations

## ⭐ Key Features

1. **🤖 AI-Powered Mood Detection**
   - Real-time face detection
   - Emotion analysis with 7 different states
   - Dual detection modes (Close Range and Far Range)

2. **🎬 Smart Movie Recommendations**
   - Mood-based filtering
   - Comprehensive movie database integration
   - Detailed movie information

3. **✨ Modern UI/UX Design**
   - Material Design 3 implementation
   - Beautiful animations and transitions
   - Dark/Light theme support
   - Responsive layout

4. **🚀 Advanced Features**
   - Face mesh overlay with quality indicators
   - Detection confidence metrics
   - Mood history tracking
   - Profile management
   - Movie favorites
   - Offline capability

## 💻 Technology Stack

### 🛠️ Core Technologies
- **📱 Language**: Kotlin
- **🏛️ Architecture**: MVVM
- **🎨 UI Framework**: Android Jetpack + Material Design 3
- **📷 Camera**: CameraX API
- **🧠 AI/ML**: Google ML Kit Face Detection

### 📚 Key Dependencies

```gradle
dependencies {
    // Core Android
    implementation 'androidx.core:core-ktx:1.12.0'
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'androidx.fragment:fragment-ktx:1.6.2'
    
    // UI & Material Design
    implementation 'com.google.android.material:material:1.10.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    implementation 'androidx.cardview:cardview:1.0.0'
    
    // Camera & Image Processing
    implementation 'androidx.camera:camera-camera2:1.3.0'
    implementation 'androidx.camera:camera-lifecycle:1.3.0'
    implementation 'androidx.camera:camera-view:1.3.0'
    
    // Machine Learning
    implementation 'com.google.mlkit:face-detection:16.1.5'
    
    // Navigation
    implementation 'androidx.navigation:navigation-fragment-ktx:2.7.5'
    implementation 'androidx.navigation:navigation-ui-ktx:2.7.5'
    
    // Lifecycle & ViewModel
    implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0'
    implementation 'androidx.lifecycle:lifecycle-livedata-ktx:2.7.0'
    
    // Networking (for movie data)
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    
    // Image Loading
    implementation 'com.github.bumptech.glide:glide:4.16.0'
}
```

### ⚡ Performance Optimizations

1. **🔍 Efficient Face Detection**:
   - Optimized ML Kit options based on detection mode
   - Throttled processing to reduce CPU usage

2. **⚡ Responsive UI**:
   - Background processing for heavy tasks
   - Efficient RecyclerView implementations
   - Glide for image loading and caching

3. **🔋 Battery Optimization**:
   - Camera resources properly released when not in use
   - Efficient network calls with caching
   - Reduced unnecessary UI updates

4. **💾 Memory Management**:
   - Proper lifecycle handling
   - Efficient bitmap processing
   - Memory leak prevention

## 📲 Installation Requirements

### 📋 Prerequisites
- Android Studio Arctic Fox (2020.3.1) or higher
- Android SDK API level 21 (Android 5.0) or higher
- Kotlin version 1.5.0 or higher
- Camera permission required for face detection functionality

### 🚀 Setup Instructions

1. Clone the repository
   ```bash
   git clone https://github.com/samyak2403/Automated-Face-Detection-Movie-Recommendation-App.git
   cd Automated-Face-Detection-Movie-Recommendation-App
   ```

2. Open in Android Studio
   - Launch Android Studio
   - Select "Open an existing project"
   - Navigate to the cloned directory and select it

3. Configure dependencies as specified in build.gradle.kts
   - Ensure all required dependencies are properly synced

4. Add movie database API key in local.properties (if needed)
   ```properties
   MOVIE_API_KEY="your_api_key_here"
   ```

5. Build and run on a device or emulator
   - Connect your Android device or start an emulator
   - Click "Run" or press `Shift+F10`

## 📱 Usage Guide

### 🏁 Getting Started

1. Launch the app and enjoy the animated splash screen
2. Grant camera permissions when prompted

### 🧩 Core Functionality

#### 📷 Camera & Mood Detection
- Navigate to the Camera tab
- Position your face within the camera viewfinder
- Choose between Close Range and Far Range detection modes
- Observe real-time mood analysis with confidence percentages

#### 🎬 Movie Recommendations
- View personalized movie suggestions based on detected mood
- Browse through mood-matched movie cards
- Tap any movie for detailed information

#### 👤 Profile Management
- Track mood history over time
- View app information and developer details

### 🔍 Advanced Usage

#### 🎯 Detection Mode Selection

Choose the appropriate detection mode based on your usage scenario:

| Mode | Best For | Detection Range | Face Size Requirement |
|------|----------|----------------|----------------------|
| **📱 Close Range** | Selfies, personal use | Within 2 meters | Face ratio > 15% of image |
| **👨‍👩‍👧‍👦 Far Range** | Group photos, distant detection | Any distance | Minimum 100x100 pixels |

#### 🎭 Mood-to-Genre Mapping

The app maps detected moods to movie genres as follows:

| Detected Mood | Movie Genre |
|---------------|-------------|
| 😄 Very Happy | Comedy      |
| 😊 Happy      | Comedy      |
| 😢 Sad        | Drama       |
| 😵 Tired      | Family      |
| 😴 Sleepy     | Family      |
| 😌 Content    | Adventure   |
| 😐 Neutral    | Action      |

## 🔧 Troubleshooting

### 🚨 Common Issues and Solutions

1. **📷 Camera Not Working**
   - Ensure camera permissions are granted
   - Check if another app is using the camera
   - Restart the app

2. **👤 Face Not Detected**
   - Ensure adequate lighting
   - Position face within camera view
   - Try switching detection modes

3. **🎬 Movie Recommendations Not Loading**
   - Check internet connection
   - Verify API key configuration
   - Try restarting the app

4. **⚡ App Performance Issues**
   - Close background apps
   - Ensure device meets minimum requirements
   - Restart the device

## 🤝 Contributing Guidelines

Contributions to CineMood are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch
   ```bash
   git checkout -b feature/your-feature-name
   ```
3. Make your changes
4. Test thoroughly
5. Submit a pull request
   - Provide a clear description of your changes
   - Reference any related issues

### 📝 Code Style Guidelines

- Follow Kotlin coding conventions
- Use meaningful variable and function names
- Document complex algorithms and functions
- Write unit tests for new features

## 📄 License

This project is licensed under the MIT License.

## 📞 Contact Information

Developer: Samyak
GitHub: https://github.com/samyak2403
Email: samyakjain@example.com (replace with actual email)
