package com.samyak.automatedfacedetectionmovierecommendationapp

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class MovieRepository(private val context: Context? = null) {
    private val api: OmdbApiService
    
    // Cache for movie results to reduce API calls
    private val movieCache = mutableMapOf<String, List<Movie>>()

    init {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.omdbapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        api = retrofit.create(OmdbApiService::class.java)
    }

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
    
    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}