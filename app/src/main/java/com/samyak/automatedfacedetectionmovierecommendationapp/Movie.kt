package com.samyak.automatedfacedetectionmovierecommendationapp

import com.google.gson.annotations.SerializedName

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


//Create the Movie data model and API service:
//```kotlin
//package com.samyak.automatedfacedetectionmovierecommendationapp
//
//import com.google.gson.annotations.SerializedName
//
//data class Movie(
//    @SerializedName("imdbID") val imdbID: String,
//    @SerializedName("Title") val title: String,
//    @SerializedName("Year") val year: String,
//    @SerializedName("Type") val type: String,
//    @SerializedName("Poster") val poster: String
//)