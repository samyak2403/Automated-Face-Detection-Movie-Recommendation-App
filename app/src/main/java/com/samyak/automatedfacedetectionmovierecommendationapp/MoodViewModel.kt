package com.samyak.automatedfacedetectionmovierecommendationapp

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MoodViewModel : ViewModel() {

    // Mood states
    private val _currentMood = MutableLiveData<String>("neutral")
    val currentMood: LiveData<String> = _currentMood

    // Movie recommendations
    private val _movieRecommendations = MutableLiveData<List<Movie>>()
    val movieRecommendations: LiveData<List<Movie>> = _movieRecommendations

    // Loading state
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    // Error state
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // Repository for API calls
    private val movieRepository = MovieRepository()
    
    // Track the last mood to avoid unnecessary API calls
    private var lastProcessedMood: String? = null

    fun updateMood(mood: String) {
        _currentMood.value = mood
        
        // Only fetch new recommendations if the mood has changed
        if (mood != lastProcessedMood) {
            fetchMovieRecommendations(mood)
            lastProcessedMood = mood
        }
    }

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

    private lateinit var moodHistoryManager: MoodHistoryManager
    private val _moodHistory = MutableLiveData<List<MoodHistoryManager.MoodEntry>>()
    val moodHistory: LiveData<List<MoodHistoryManager.MoodEntry>> = _moodHistory
    
    fun initMoodHistoryManager(context: Context) {
        moodHistoryManager = MoodHistoryManager(context)
        _moodHistory.value = moodHistoryManager.getMoodHistory()
    }
    
    fun setCurrentMood(mood: String) {
        if (::moodHistoryManager.isInitialized) {
            moodHistoryManager.addMoodEntry(mood)
            _moodHistory.value = moodHistoryManager.getMoodHistory()
        }
        
        // Existing mood setting code
        _currentMood.value = mood
        fetchMovieRecommendations(mood)
    }

    fun clearMoodHistory() {
        if (::moodHistoryManager.isInitialized) {
            moodHistoryManager.clearHistory()
            _moodHistory.value = emptyList()
        }
    }
}