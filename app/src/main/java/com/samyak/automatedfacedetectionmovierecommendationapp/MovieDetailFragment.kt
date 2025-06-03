package com.samyak.automatedfacedetectionmovierecommendationapp

import android.os.Bundle
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
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MovieDetailFragment : Fragment() {

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
        val backButton: ImageButton = view.findViewById(R.id.back_button)
        val watchButton: Button = view.findViewById(R.id.watch_button)
        val fabFavorite: FloatingActionButton = view.findViewById(R.id.fab_favorite)
        
        val genreCard: CardView = view.findViewById(R.id.genre_card)
        val creditsCard: CardView = view.findViewById(R.id.credits_card)
        val additionalInfoCard: CardView = view.findViewById(R.id.additional_info_card)
        val plotCard: CardView = view.findViewById(R.id.plot_card)
        
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

        genreCard.startAnimation(slideUp)
        creditsCard.startAnimation(slideUp)
        additionalInfoCard.startAnimation(slideUp)
        plotCard.startAnimation(slideUp)
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
            genreCard.visibility = View.VISIBLE
        } else {
            genreCard.visibility = View.GONE
        }
        
        // Director, Writer, Actors
        if (!movieDirector.isNullOrEmpty() && movieDirector != "N/A") {
            directorTextView.text = movieDirector
            view.findViewById<TextView>(R.id.detail_movie_director_label).visibility = View.VISIBLE
            directorTextView.visibility = View.VISIBLE
        } else {
            view.findViewById<TextView>(R.id.detail_movie_director_label).visibility = View.GONE
            directorTextView.visibility = View.GONE
        }
        
        // Apply similar pattern to other fields
        writerTextView.text = movieWriter ?: "N/A"
        actorsTextView.text = movieActors ?: "N/A"
        languageTextView.text = movieLanguage ?: "N/A"
        countryTextView.text = movieCountry ?: "N/A"
        awardsTextView.text = movieAwards ?: "N/A"
        plotTextView.text = moviePlot

        // Set click listeners
        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        
        watchButton.setOnClickListener {
            Toast.makeText(context, "Watch feature coming soon!", Toast.LENGTH_SHORT).show()
        }
        
        fabFavorite.setOnClickListener {
            fabFavorite.setImageResource(R.drawable.ic_favorite_filled) // Toggle between filled/unfilled
            Toast.makeText(context, "Added to favorites!", Toast.LENGTH_SHORT).show()
        }
    }
}