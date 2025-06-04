package com.samyak.automatedfacedetectionmovierecommendationapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MovieDetailFragment : Fragment() {
    
    private lateinit var retrofit: Retrofit
    private lateinit var omdbApiService: OmdbApiService
    private var isFavorite = false

    companion object {
        private const val ARG_MOVIE_ID = "movie_id"
        private const val ARG_MOVIE_TITLE = "movie_title"
        private const val ARG_MOVIE_YEAR = "movie_year"
        private const val ARG_MOVIE_POSTER = "movie_poster"
        private const val ARG_MOVIE_TYPE = "movie_type"
        private const val ARG_MOVIE_PLOT = "movie_plot"
        private const val ARG_MOVIE_RATED = "movie_rated"
        private const val ARG_MOVIE_RUNTIME = "movie_runtime"
        private const val ARG_MOVIE_GENRE = "movie_genre"
        private const val ARG_MOVIE_RATING = "movie_rating"
        private const val ARG_MOVIE_DIRECTOR = "movie_director"
        private const val ARG_MOVIE_WRITER = "movie_writer"
        private const val ARG_MOVIE_ACTORS = "movie_actors"
        private const val ARG_MOVIE_LANGUAGE = "movie_language"
        private const val ARG_MOVIE_COUNTRY = "movie_country"
        private const val ARG_MOVIE_AWARDS = "movie_awards"

        fun newInstance(movie: Movie): MovieDetailFragment {
            val fragment = MovieDetailFragment()
            val args = Bundle()
            args.putString(ARG_MOVIE_ID, movie.imdbID)
            args.putString(ARG_MOVIE_TITLE, movie.title)
            args.putString(ARG_MOVIE_YEAR, movie.year)
            args.putString(ARG_MOVIE_POSTER, movie.poster)
            args.putString(ARG_MOVIE_TYPE, movie.type)
            args.putString(ARG_MOVIE_PLOT, movie.plot)
            args.putString(ARG_MOVIE_RATED, movie.rated)
            args.putString(ARG_MOVIE_RUNTIME, movie.runtime)
            args.putString(ARG_MOVIE_GENRE, movie.genre)
            args.putString(ARG_MOVIE_RATING, movie.imdbRating)
            args.putString(ARG_MOVIE_DIRECTOR, movie.director)
            args.putString(ARG_MOVIE_WRITER, movie.writer)
            args.putString(ARG_MOVIE_ACTORS, movie.actors)
            args.putString(ARG_MOVIE_LANGUAGE, movie.language)
            args.putString(ARG_MOVIE_COUNTRY, movie.country)
            args.putString(ARG_MOVIE_AWARDS, movie.awards)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_movie_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Initialize Retrofit
        retrofit = Retrofit.Builder()
            .baseUrl("https://www.omdbapi.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            
        omdbApiService = retrofit.create(OmdbApiService::class.java)

        // Get movie data from arguments
        val movieId = arguments?.getString(ARG_MOVIE_ID) ?: ""
        val movieTitle = arguments?.getString(ARG_MOVIE_TITLE) ?: ""
        val movieYear = arguments?.getString(ARG_MOVIE_YEAR) ?: ""
        val moviePoster = arguments?.getString(ARG_MOVIE_POSTER) ?: ""
        val movieType = arguments?.getString(ARG_MOVIE_TYPE) ?: ""
        val moviePlot = arguments?.getString(ARG_MOVIE_PLOT) ?: ""
        val movieRated = arguments?.getString(ARG_MOVIE_RATED)
        val movieRuntime = arguments?.getString(ARG_MOVIE_RUNTIME)
        val movieGenre = arguments?.getString(ARG_MOVIE_GENRE)
        val movieRating = arguments?.getString(ARG_MOVIE_RATING)
        val movieDirector = arguments?.getString(ARG_MOVIE_DIRECTOR)
        val movieWriter = arguments?.getString(ARG_MOVIE_WRITER)
        val movieActors = arguments?.getString(ARG_MOVIE_ACTORS)
        val movieLanguage = arguments?.getString(ARG_MOVIE_LANGUAGE)
        val movieCountry = arguments?.getString(ARG_MOVIE_COUNTRY)
        val movieAwards = arguments?.getString(ARG_MOVIE_AWARDS)

        // Find views
        val posterImageView: ImageView = view.findViewById(R.id.detail_movie_poster)
        val titleTextView: TextView = view.findViewById(R.id.detail_movie_title)
        val yearTextView: TextView = view.findViewById(R.id.detail_movie_year)
        val typeTextView: TextView = view.findViewById(R.id.detail_movie_type)
        val imdbIdTextView: TextView = view.findViewById(R.id.detail_movie_imdb_id)
        val plotTextView: TextView = view.findViewById(R.id.detail_movie_plot)
        val toolbar: androidx.appcompat.widget.Toolbar = view.findViewById(R.id.toolbar)
        val watchButton: Button = view.findViewById(R.id.watch_button)
        val fabFavorite: FloatingActionButton = view.findViewById(R.id.fab_favorite)
        
        // Find new text views for additional movie details
        val ratingTextView: TextView = view.findViewById(R.id.detail_movie_rating)
        val runtimeTextView: TextView = view.findViewById(R.id.detail_movie_runtime)
        val ratedTextView: TextView = view.findViewById(R.id.detail_movie_rated)
        val genreTextView: TextView = view.findViewById(R.id.detail_movie_genre)
        val directorTextView: TextView = view.findViewById(R.id.detail_movie_director)
        val writerTextView: TextView = view.findViewById(R.id.detail_movie_writer)
        val actorsTextView: TextView = view.findViewById(R.id.detail_movie_actors)
        val languageTextView: TextView = view.findViewById(R.id.detail_movie_language)
        val countryTextView: TextView = view.findViewById(R.id.detail_movie_country)
        val awardsTextView: TextView = view.findViewById(R.id.detail_movie_awards)

        // Apply animations
        val fadeIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_in)
        fadeIn.duration = 1000
        
        val slideUp = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left)
        slideUp.duration = 500

        // Load poster image with Glide with better error handling
        if (moviePoster.isNotEmpty() && moviePoster != "N/A") {
            Glide.with(this)
                .load(moviePoster)
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(R.drawable.ic_launcher_foreground) // Add error placeholder
                .into(posterImageView)
        } else {
            // Load a placeholder if no poster is available
            posterImageView.setImageResource(R.drawable.ic_launcher_foreground)
        }

        watchButton.startAnimation(fadeIn)
        
        // Set basic text values
        titleTextView.text = movieTitle
        yearTextView.text = movieYear
        typeTextView.text = movieType
        imdbIdTextView.text = movieId

        // Set additional text values with improved null checks (matching JavaScript function)
        // Only show if not null and not "N/A"
        if (!movieRating.isNullOrEmpty() && movieRating != "N/A") {
            ratingTextView.text = "$movieRating/10"
            ratingTextView.visibility = View.VISIBLE
        } else {
            ratingTextView.visibility = View.GONE
        }
        
        if (!movieRuntime.isNullOrEmpty() && movieRuntime != "N/A") {
            runtimeTextView.text = movieRuntime
            runtimeTextView.visibility = View.VISIBLE
        } else {
            runtimeTextView.visibility = View.GONE
        }
        
        if (!movieRated.isNullOrEmpty() && movieRated != "N/A") {
            ratedTextView.text = movieRated
            ratedTextView.visibility = View.VISIBLE
        } else {
            ratedTextView.visibility = View.GONE
        }
        
        // Apply the same pattern to other fields
        if (!movieGenre.isNullOrEmpty() && movieGenre != "N/A") {
            genreTextView.text = movieGenre
        }
        
        // Director, Writer, Actors
        if (!movieDirector.isNullOrEmpty() && movieDirector != "N/A") {
            directorTextView.text = movieDirector
            directorTextView.visibility = View.VISIBLE
        }
        
        // Apply similar pattern to other fields
        writerTextView.text = movieWriter ?: "N/A"
        actorsTextView.text = movieActors ?: "N/A"
        languageTextView.text = movieLanguage ?: "N/A"
        countryTextView.text = movieCountry ?: "N/A"
        awardsTextView.text = movieAwards ?: "N/A"
        plotTextView.text = moviePlot

        // Set click listeners
        toolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        
        watchButton.setOnClickListener {
            // Open IMDB page for this movie if ID is available
            if (movieId.isNotEmpty()) {
                val imdbUrl = "https://www.imdb.com/title/$movieId/"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(imdbUrl))
                startActivity(intent)
            } else {
                Toast.makeText(context, "Watch feature coming soon!", Toast.LENGTH_SHORT).show()
            }
        }
        
        fabFavorite.setOnClickListener {
            isFavorite = !isFavorite
            if (isFavorite) {
                fabFavorite.setImageResource(R.drawable.ic_favorite_filled)
                Toast.makeText(context, "Added to favorites!", Toast.LENGTH_SHORT).show()
            } else {
                fabFavorite.setImageResource(R.drawable.ic_favorite_border)
                Toast.makeText(context, "Removed from favorites!", Toast.LENGTH_SHORT).show()
            }
        }
        
        // If we're missing detailed data, fetch it from the API
        if (movieId.isNotEmpty() && (moviePlot.isEmpty() || movieGenre.isNullOrEmpty() || movieDirector.isNullOrEmpty())) {
            fetchMovieDetails(movieId, view)
        }
    }
    
    private fun fetchMovieDetails(imdbId: String, view: View) {
        val loadingIndicator = view.findViewById<CircularProgressIndicator>(R.id.loading_indicator) ?: return
        
        // Show loading indicator
        loadingIndicator.visibility = View.VISIBLE
        
        // Use lifecycleScope to launch coroutine
        lifecycleScope.launch {
            try {
                val response = omdbApiService.getMovieDetail(imdbId)
                if (response.isSuccessful && response.body() != null) {
                    val movie = response.body()!!
                    updateUI(movie, view)
                } else {
                    Log.e("MovieDetailFragment", "Error fetching movie details: ${response.errorBody()?.string()}")
                    Toast.makeText(context, "Failed to load movie details", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("MovieDetailFragment", "Exception fetching movie details", e)
                Toast.makeText(context, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                // Hide loading indicator
                loadingIndicator.visibility = View.GONE
            }
        }
    }
    
    private fun updateUI(movie: Movie, view: View) {
        // Find views
        val titleTextView: TextView = view.findViewById(R.id.detail_movie_title)
        val yearTextView: TextView = view.findViewById(R.id.detail_movie_year)
        val typeTextView: TextView = view.findViewById(R.id.detail_movie_type)
        val imdbIdTextView: TextView = view.findViewById(R.id.detail_movie_imdb_id)
        val plotTextView: TextView = view.findViewById(R.id.detail_movie_plot)
        val posterImageView: ImageView = view.findViewById(R.id.detail_movie_poster)
        
        val toolbar: androidx.appcompat.widget.Toolbar = view.findViewById(R.id.toolbar)
        
        // Find text views for additional movie details
        val ratingTextView: TextView = view.findViewById(R.id.detail_movie_rating)
        val runtimeTextView: TextView = view.findViewById(R.id.detail_movie_runtime)
        val ratedTextView: TextView = view.findViewById(R.id.detail_movie_rated)
        val genreTextView: TextView = view.findViewById(R.id.detail_movie_genre)
        val directorTextView: TextView = view.findViewById(R.id.detail_movie_director)
        val writerTextView: TextView = view.findViewById(R.id.detail_movie_writer)
        val actorsTextView: TextView = view.findViewById(R.id.detail_movie_actors)
        val languageTextView: TextView = view.findViewById(R.id.detail_movie_language)
        val countryTextView: TextView = view.findViewById(R.id.detail_movie_country)
        val awardsTextView: TextView = view.findViewById(R.id.detail_movie_awards)
        
        // Set basic text values
        titleTextView.text = movie.title
        yearTextView.text = movie.year
        typeTextView.text = movie.type
        imdbIdTextView.text = movie.imdbID
        
        // Load poster image with Glide
        if (movie.poster.isNotEmpty() && movie.poster != "N/A") {
            Glide.with(this)
                .load(movie.poster)
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(R.drawable.ic_launcher_foreground)
                .into(posterImageView)
        }
        
        // Set additional text values with null checks
        if (!movie.imdbRating.isNullOrEmpty() && movie.imdbRating != "N/A") {
            ratingTextView.text = "${movie.imdbRating}/10"
            ratingTextView.visibility = View.VISIBLE
        } else {
            ratingTextView.visibility = View.GONE
        }
        
        if (!movie.runtime.isNullOrEmpty() && movie.runtime != "N/A") {
            runtimeTextView.text = movie.runtime
            runtimeTextView.visibility = View.VISIBLE
        } else {
            runtimeTextView.visibility = View.GONE
        }
        
        if (!movie.rated.isNullOrEmpty() && movie.rated != "N/A") {
            ratedTextView.text = movie.rated
            ratedTextView.visibility = View.VISIBLE
        } else {
            ratedTextView.visibility = View.GONE
        }
        
        // Genre
        if (!movie.genre.isNullOrEmpty() && movie.genre != "N/A") {
            genreTextView.text = movie.genre
        }
        
        // Credits section
        var hasCredits = false
        
        // Director
        if (!movie.director.isNullOrEmpty() && movie.director != "N/A") {
            directorTextView.text = movie.director
            directorTextView.visibility = View.VISIBLE
            hasCredits = true
        }
        
        // Writer
        if (!movie.writer.isNullOrEmpty() && movie.writer != "N/A") {
            writerTextView.text = movie.writer
            writerTextView.visibility = View.VISIBLE
            hasCredits = true
        }
        
        // Actors
        if (!movie.actors.isNullOrEmpty() && movie.actors != "N/A") {
            actorsTextView.text = movie.actors
            actorsTextView.visibility = View.VISIBLE
            hasCredits = true
        }
        
        // Credits section is always visible in the new layout
        
        // Additional info section
        var hasAdditionalInfo = false
        
        // Language
        if (!movie.language.isNullOrEmpty() && movie.language != "N/A") {
            languageTextView.text = movie.language
            languageTextView.visibility = View.VISIBLE
            hasAdditionalInfo = true
        } else {
            languageTextView.visibility = View.GONE
        }
        
        // Country
        if (!movie.country.isNullOrEmpty() && movie.country != "N/A") {
            countryTextView.text = movie.country
            countryTextView.visibility = View.VISIBLE
            hasAdditionalInfo = true
        } else {
            countryTextView.visibility = View.GONE
        }
        
        // Awards
        if (!movie.awards.isNullOrEmpty() && movie.awards != "N/A") {
            awardsTextView.text = movie.awards
            awardsTextView.visibility = View.VISIBLE
            hasAdditionalInfo = true
        } else {
            awardsTextView.visibility = View.GONE
        }
        
        // Additional info section is always visible in the new layout
        
        // Plot
        if (!movie.plot.isNullOrEmpty() && movie.plot != "N/A") {
            plotTextView.text = movie.plot
        }
    }
}