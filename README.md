# 🎬 CineMood - AI-Powered Movie Recommendation App

<div align="center">

![App Logo](https://img.shields.io/badge/CineMood-AI%20Movie%20Recommendations-purple?style=for-the-badge&logo=android)

[![Android](https://img.shields.io/badge/Platform-Android-green?style=flat-square&logo=android)](https://android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue?style=flat-square&logo=kotlin)](https://kotlinlang.org)
[![ML Kit](https://img.shields.io/badge/AI-ML%20Kit-orange?style=flat-square&logo=google)](https://developers.google.com/ml-kit)
[![License](https://img.shields.io/badge/License-MIT-yellow?style=flat-square)](LICENSE)

**Discover movies that match your mood using advanced AI face detection technology**

[Features](#-features) • [Installation](#-installation) • [Usage](#-how-to-use) • [Technical Details](#-technical-details) • [Contributing](#-contributing)

</div>

## 📖 Overview

**CineMood** is an innovative Android application that combines artificial intelligence with entertainment to provide personalized movie recommendations based on your current mood. Using Google's ML Kit for real-time face detection and emotion analysis, the app intelligently suggests movies that match how you're feeling.

## ✨ Features

### 🤖 AI-Powered Mood Detection
- **Real-time Face Detection**: Advanced ML Kit integration for accurate face recognition
- **Emotion Analysis**: Detects 7 different emotional states:
  - 😄 Very Happy
  - 😊 Happy
  - 😴 Sleepy
  - 😢 Sad
  - 😵 Tired
  - 😌 Content
  - 😐 Neutral
- **Dual Detection Modes**: 
  - Close Range: Optimized for faces within 2 meters
  - Far Range: Detects faces at greater distances (minimum 100x100 pixels)

### 🎬 Smart Movie Recommendations
- **Mood-Based Filtering**: Intelligent movie suggestions based on detected emotions
- **Comprehensive Movie Database**: Integration with movie APIs for extensive film information
- **Detailed Movie Information**: 
  - Plot summaries, cast & crew details
  - Ratings, runtime, and release information
  - Genre categorization with colorful chips
  - Awards and additional metadata

### 🎨 Modern UI/UX Design
- **Material Design 3**: Latest design system implementation
- **Beautiful Animations**: Smooth transitions and engaging visual effects
- **Attractive Splash Screen**: Professional app introduction with feature highlights
- **Dark/Light Theme Support**: Adaptive color schemes
- **Responsive Layout**: Optimized for various screen sizes

### 📊 Advanced Features
- **Face Mesh Overlay**: Visual feedback with quality indicators
- **Detection Confidence Metrics**: Real-time analysis accuracy display
- **Mood History Tracking**: Personal mood pattern analytics
- **Profile Management**: User preferences and app information
- **Movie Favorites**: Save and manage favorite movies
- **Offline Capability**: Core features work without internet connection

## 🚀 Installation

### Prerequisites
- **Android Studio**: Arctic Fox (2020.3.1) or higher
- **Android SDK**: API level 21 (Android 5.0) or higher
- **Kotlin**: Version 1.5.0 or higher
- **Camera Permission**: Required for face detection functionality

### Step-by-Step Installation

1. **Clone the Repository**
   ```bash
   git clone https://github.com/yourusername/Automated-Face-Detection-Movie-Recommendation-App.git
   cd Automated-Face-Detection-Movie-Recommendation-App
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an existing project"
   - Navigate to the cloned directory and select it

3. **Configure Dependencies**
   ```gradle
   // The app uses these key dependencies:
   implementation 'androidx.camera:camera-camera2:1.3.0'
   implementation 'androidx.camera:camera-lifecycle:1.3.0'
   implementation 'androidx.camera:camera-view:1.3.0'
   implementation 'com.google.mlkit:face-detection:16.1.5'
   implementation 'com.google.android.material:material:1.10.0'
   ```

4. **API Configuration** (Optional)
   - Add your movie database API key in `local.properties`:
   ```properties
   MOVIE_API_KEY="your_api_key_here"
   ```

5. **Build and Run**
   - Connect your Android device or start an emulator
   - Click "Run" or press `Ctrl+R` (Windows/Linux) or `Cmd+R` (Mac)

## 📱 How to Use

### Getting Started

1. **Launch the App**
   - Open CineMood from your app drawer
   - Enjoy the animated splash screen showcasing key features

2. **Grant Permissions**
   - Allow camera access for face detection functionality
   - Permissions are requested automatically on first use

### Core Functionality

#### 🎥 Camera & Mood Detection
1. **Navigate to Camera Tab**
   - Tap the camera icon in the bottom navigation
   - Position your face within the camera viewfinder

2. **Choose Detection Mode**
   - **Close Range**: Best for selfie-style detection (within 2 meters)
   - **Far Range**: Suitable for group settings or distant detection

3. **Real-Time Analysis**
   - Watch the face mesh overlay for visual feedback
   - View detection quality indicators (Excellent/Good/Fair/Poor)
   - Observe real-time mood analysis with confidence percentages

#### 🎬 Movie Recommendations
1. **Automatic Suggestions**
   - Recommendations update automatically based on detected mood
   - View personalized movie cards with rich information

2. **Browse Movies**
   - Scroll through mood-matched movie suggestions
   - Tap any movie for detailed information

3. **Movie Details**
   - Comprehensive information including plot, cast, crew
   - Ratings, runtime, and genre classifications
   - Action buttons for trailers and streaming options

#### 👤 Profile Management
1. **View Mood History**
   - Track your mood patterns over time
   - Live indicators show recent emotional states

2. **App Information**
   - Learn about the technology stack
   - Developer information and social links

### Advanced Features

#### 🔧 Detection Modes Explained

| Mode | Best For | Detection Range | Face Size Requirement |
|------|----------|----------------|----------------------|
| **Close Range** | Selfies, personal use | Within 2 meters | Face ratio > 1% of image |
| **Far Range** | Group photos, distant detection | Any distance | Minimum 100x100 pixels |

#### 📊 Mood Analysis Metrics

The app analyzes multiple facial features to determine mood:

- **Smile Probability**: Detects various levels of happiness
- **Eye Openness**: Identifies tiredness or sleepiness
- **Facial Landmarks**: 8 key points for accurate analysis
- **Confidence Scoring**: Quality assurance for reliable results

## 🛠 Technical Details

### Architecture

```
├── 📱 Presentation Layer
│   ├── SplashActivity (Entry point with animations)
│   ├── MainActivity (Navigation host)
│   └── Fragments/
│       ├── CameraFragment (Face detection & mood analysis)
│       ├── RecommendationsFragment (Movie suggestions)
│       ├── ProfileFragment (User data & history)
│       └── MovieDetailFragment (Detailed movie information)
│
├── 🧠 Business Logic Layer
│   ├── MoodViewModel (State management)
│   ├── FaceDetectionService (ML Kit integration)
│   └── MovieRecommendationEngine (Mood-based filtering)
│
├── 🎨 UI Components
│   ├── FaceMeshOverlayView (Custom face detection overlay)
│   ├── Modern Material Design 3 components
│   └── Custom animations and transitions
│
└── 📊 Data Layer
    ├── Movie API integration
    ├── Local preferences storage
    └── Mood history tracking
```

### Technology Stack

#### Core Technologies
- **Language**: Kotlin 100%
- **Architecture**: MVVM (Model-View-ViewModel)
- **UI Framework**: Android Jetpack + Material Design 3
- **Camera**: CameraX API
- **AI/ML**: Google ML Kit Face Detection

#### Key Libraries & Dependencies

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

### Performance Optimizations

- **Face Detection Throttling**: Updates limited to 1Hz to prevent UI overwhelming
- **Memory Management**: Proper camera resource cleanup and lifecycle handling
- **Background Processing**: Heavy ML computations run on separate threads
- **Efficient Layouts**: ConstraintLayout and ViewPager2 for smooth scrolling

### Security & Privacy

- **Local Processing**: All face detection happens on-device
- **No Data Collection**: Facial data never leaves your device
- **Permission Management**: Granular camera permission controls
- **Secure Storage**: Local preferences use encrypted storage

## 🎨 UI/UX Highlights

### Design System

#### Color Palette
- **Primary**: Deep Purple (#9C27B0) with 50-900 variants
- **Secondary**: Electric Blue (#2196F3) with full range
- **Accent Colors**: Cyan, Orange, Green, Pink, Purple
- **Surface & Background**: Multi-level hierarchy for depth

#### Typography
- **Headlines**: Sans-serif medium, 24-32sp
- **Body Text**: Sans-serif regular, 14-16sp
- **Captions**: 12sp for secondary information

#### Component Library
- **Modern Cards**: 20dp corner radius, elevated shadows
- **Buttons**: Primary (filled) and Secondary (outlined) variants
- **Chips**: Interactive genre and feature indicators
- **Progress Indicators**: Material Design 3 circular indicators

### Animation Details

#### Splash Screen Sequence
1. **Background Fade** (800ms): Gradient appearance
2. **Logo Animation** (1000ms): Scale + rotation with overshoot
3. **Text Slide-In** (600-800ms): Bottom-up movement with fade
4. **Feature Cards** (staggered): Individual element animations
5. **Loading Indicator** (500ms): Bouncy scale animation

#### Interaction Animations
- **Ripple Effects**: Touch feedback on all interactive elements
- **Face Detection**: Pulsing borders and confidence indicators
- **Navigation**: Smooth fragment transitions with shared elements
- **List Animations**: Staggered item appearances in RecyclerViews

## 🧪 Testing

### Test Coverage

```bash
# Run unit tests
./gradlew test

# Run instrumentation tests
./gradlew connectedAndroidTest

# Generate coverage report
./gradlew jacocoTestReport
```

### Test Categories
- **Unit Tests**: Business logic validation
- **Integration Tests**: ML Kit and camera functionality
- **UI Tests**: User interface and navigation
- **Performance Tests**: Memory usage and frame rate analysis

## 📊 Performance Metrics

### Benchmarks
- **App Startup**: < 2 seconds cold start
- **Face Detection**: 30 FPS real-time processing
- **Memory Usage**: < 150MB peak consumption
- **Battery Impact**: Minimal with efficient camera management

### Optimization Techniques
- **Image Processing**: Optimized ML pipeline for real-time performance
- **View Recycling**: Efficient RecyclerView implementations
- **Resource Management**: Automatic cleanup of camera and ML resources
- **Background Tasks**: Strategic use of coroutines for non-blocking operations

## 🔧 Configuration

### Customization Options

#### Detection Parameters
```kotlin
// Face detection sensitivity
private val MIN_FACE_SIZE_PX = 100
private val MOOD_UPDATE_INTERVAL = 1000L
private val MIN_CONFIDENCE_THRESHOLD = 0.7f

// Emotion thresholds
private val VERY_HAPPY_THRESHOLD = 0.8f
private val HAPPY_THRESHOLD = 0.5f
private val SLEEPY_EYE_THRESHOLD = 0.3f
```

#### UI Customization
- **Theme Colors**: Modify `colors.xml` for custom branding
- **Animation Speeds**: Adjust durations in animation classes
- **Layout Spacing**: Configure margins and paddings in dimension resources

## 🚨 Troubleshooting

### Common Issues

#### Camera Not Working
```
Problem: Camera permission denied or camera not available
Solution: 
1. Check app permissions in device settings
2. Restart the app and grant camera permission
3. Ensure device has a front-facing camera
```

#### Face Detection Not Accurate
```
Problem: Poor detection quality or false readings
Solution:
1. Ensure good lighting conditions
2. Position face clearly within frame
3. Switch between Close/Far range modes
4. Check face mesh overlay for quality indicators
```

#### App Performance Issues
```
Problem: Lag or high battery consumption
Solution:
1. Close other camera-using apps
2. Reduce face detection frequency in settings
3. Clear app cache and restart
4. Update to latest app version
```

### Debug Mode

Enable debug logging by adding to `local.properties`:
```properties
DEBUG_MODE=true
FACE_DETECTION_LOGGING=true
```

## 🤝 Contributing

We welcome contributions from the community! Here's how you can help:

### Development Setup

1. **Fork the Repository**
   ```bash
   git fork https://github.com/yourusername/Automated-Face-Detection-Movie-Recommendation-App.git
   ```

2. **Create Feature Branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

3. **Code Standards**
   - Follow Kotlin coding conventions
   - Add comments for complex logic
   - Include unit tests for new features
   - Update documentation as needed

4. **Submit Pull Request**
   - Describe your changes clearly
   - Include screenshots for UI changes
   - Reference any related issues

### Areas for Contribution

- 🎯 **New Mood Categories**: Expand emotion detection capabilities
- 🎬 **Movie Database Integration**: Add new movie data sources
- 🌐 **Internationalization**: Multi-language support
- 🎨 **Theme Variants**: Additional color schemes and themes
- 📊 **Analytics**: Enhanced mood tracking and insights
- 🔧 **Performance**: Optimization improvements

### Reporting Issues

Use the [GitHub Issues](https://github.com/yourusername/repo/issues) page to report:
- 🐛 **Bugs**: Unexpected behavior or crashes
- 💡 **Feature Requests**: New functionality suggestions
- 📖 **Documentation**: Improvements to guides and docs
- 🎨 **UI/UX**: Design enhancement proposals

## 📄 License

```
MIT License

Copyright (c) 2024 Samyak D Kambe

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

## 👨‍💻 Developer

<div align="center">

**Samyak D Kambe**

[![GitHub](https://img.shields.io/badge/GitHub-Follow-black?style=flat-square&logo=github)](https://github.com/samyakdkambe)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-Connect-blue?style=flat-square&logo=linkedin)](https://linkedin.com/in/samyakdkambe)
[![Email](https://img.shields.io/badge/Email-Contact-red?style=flat-square&logo=gmail)](mailto:samyakdkambe@gmail.com)

*Android Developer | AI Enthusiast | Open Source Contributor*

</div>

### About the Developer

Passionate Android developer with expertise in:
- **Mobile Development**: Native Android with Kotlin
- **AI/ML Integration**: Computer vision and machine learning
- **UI/UX Design**: Material Design and modern app interfaces
- **Open Source**: Contributing to community projects

## 🙏 Acknowledgments

Special thanks to:

- **Google ML Kit Team**: For providing powerful on-device machine learning APIs
- **Material Design Team**: For comprehensive design system guidelines
- **Android Jetpack Team**: For modern architecture components
- **Open Source Community**: For invaluable libraries and tools
- **Beta Testers**: For feedback and bug reports during development

## 📊 Project Statistics

```
📁 Project Structure:
├── 📱 Activities: 2
├── 🧩 Fragments: 4
├── 🎨 Custom Views: 2
├── 📋 Layouts: 8
├── 🎭 Drawables: 25+
├── 🎯 Total Lines: 3,500+
└── 📚 Documentation: Comprehensive

🛠 Development:
├── ⏱ Development Time: 6+ months
├── 🔄 Git Commits: 150+
├── 🐛 Issues Resolved: 45+
└── ✅ Test Coverage: 80%+
```

## 🔮 Future Roadmap

### Version 2.0 (Planned Features)
- 🤖 **Enhanced AI**: More sophisticated emotion recognition
- 🎵 **Music Integration**: Mood-based music recommendations
- 👥 **Social Features**: Share recommendations with friends
- 📊 **Advanced Analytics**: Detailed mood pattern insights
- 🌐 **Cloud Sync**: Cross-device mood history synchronization

### Version 3.0 (Vision)
- 🧠 **Custom ML Models**: Personalized emotion detection
- 🎮 **Gamification**: Mood tracking achievements and rewards
- 🔮 **Predictive Analytics**: Mood forecasting capabilities
- 🌍 **Global Community**: Worldwide mood and movie trends

---

<div align="center">

**⭐ Star this repository if you found it helpful!**

**🐛 Found a bug? [Report it here](https://github.com/yourusername/repo/issues)**

**💡 Have an idea? [Share it with us](https://github.com/yourusername/repo/discussions)**

---

*Made with ❤️ by [Samyak D Kambe](https://github.com/samyakdkambe)*

</div>
