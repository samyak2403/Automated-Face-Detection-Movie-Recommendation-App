# CineMood - AI-Powered Movie Recommendation App

## Project Overview

CineMood is an innovative Android application that combines artificial intelligence with entertainment to provide personalized movie recommendations based on the user's current mood. Using Google's ML Kit for real-time face detection and emotion analysis, the app intelligently suggests movies that match how the user is feeling.

## Technical Architecture

### Project Structure

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

### Architecture Pattern

The app follows the MVVM (Model-View-ViewModel) architecture pattern:

- **Model**: Movie.kt, MovieRepository.kt, MoodHistoryManager.kt
- **View**: Activity and Fragment classes, custom views
- **ViewModel**: MoodViewModel.kt

## Core Components

### 1. Face Detection & Mood Analysis

**Key Files:**
- `CameraFragment.kt`: Handles camera preview and face detection
- `FaceMeshOverlayView.kt`: Custom view for displaying face mesh
- `MoodViewModel.kt`: Manages mood detection state

The app uses Google ML Kit's Face Detection API to analyze facial expressions and determine the user's mood. It can detect 7 different emotional states:
- Very Happy
- Happy
- Sleepy
- Sad
- Tired
- Content
- Neutral

### 2. Movie Recommendations

**Key Files:**
- `MovieRepository.kt`: Handles movie data retrieval
- `OmdbApiService.kt`: Interface for the OMDB API
- `RecommendationsFragment.kt`: Displays movie recommendations
- `MovieDetailFragment.kt`: Shows detailed movie information

The app integrates with movie APIs to provide recommendations based on the detected mood. Movies are filtered and categorized according to the user's emotional state.

### 3. User Interface

**Key Files:**
- `SplashActivity.kt`: Entry point with animations
- `MainActivity.kt`: Main navigation host
- `MovieAdapter.kt`: RecyclerView adapter for movie lists
- `MoodHistoryAdapter.kt`: RecyclerView adapter for mood history

The UI is built using Material Design 3 principles with responsive layouts, beautiful animations, and support for both light and dark themes.

## Key Features

1. **AI-Powered Mood Detection**
   - Real-time face detection
   - Emotion analysis with 7 different states
   - Dual detection modes (Close Range and Far Range)

2. **Smart Movie Recommendations**
   - Mood-based filtering
   - Comprehensive movie database integration
   - Detailed movie information

3. **Modern UI/UX Design**
   - Material Design 3 implementation
   - Beautiful animations and transitions
   - Dark/Light theme support
   - Responsive layout

4. **Advanced Features**
   - Face mesh overlay with quality indicators
   - Detection confidence metrics
   - Mood history tracking
   - Profile management
   - Movie favorites
   - Offline capability

## Technology Stack

### Core Technologies
- **Language**: Kotlin
- **Architecture**: MVVM
- **UI Framework**: Android Jetpack + Material Design 3
- **Camera**: CameraX API
- **AI/ML**: Google ML Kit Face Detection

### Key Dependencies

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

## Installation Requirements

### Prerequisites
- Android Studio Arctic Fox (2020.3.1) or higher
- Android SDK API level 21 (Android 5.0) or higher
- Kotlin version 1.5.0 or higher
- Camera permission required for face detection functionality

### Setup Instructions

1. Clone the repository
2. Open in Android Studio
3. Configure dependencies as specified in build.gradle.kts
4. Add movie database API key in local.properties (if needed)
5. Build and run on a device or emulator

## Usage Guide

### Getting Started

1. Launch the app and enjoy the animated splash screen
2. Grant camera permissions when prompted

### Core Functionality

#### Camera & Mood Detection
- Navigate to the Camera tab
- Position your face within the camera viewfinder
- Choose between Close Range and Far Range detection modes
- Observe real-time mood analysis with confidence percentages

#### Movie Recommendations
- View personalized movie suggestions based on detected mood
- Browse through mood-matched movie cards
- Tap any movie for detailed information

#### Profile Management
- Track mood history over time
- View app information and developer details

## Contributing Guidelines

Contributions to CineMood are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Submit a pull request

## License

This project is licensed under the MIT License.

## Contact Information

Developer: Samyak
GitHub: https://github.com/samyak2403 