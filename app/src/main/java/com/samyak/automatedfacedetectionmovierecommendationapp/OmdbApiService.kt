package com.samyak.automatedfacedetectionmovierecommendationapp

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

interface OmdbApiService {
    @GET("/")
    suspend fun searchMovies(
        @Query("s") searchTerm: String,
        @Query("type") type: String = "movie",
        @Query("apikey") apiKey: String = "1065bd81" // Free API key (limited to 1000 requests/day)
//        @Query("apikey") apiKey: String = "3e974fca" // Free API key (limited to 1000 requests/day)
    ): SearchResponse
}

data class SearchResponse(
    @SerializedName("Search") val search: List<Movie>?,
    @SerializedName("totalResults") val totalResults: String?,
    @SerializedName("Response") val response: String,
    @SerializedName("Error") val error: String?
)