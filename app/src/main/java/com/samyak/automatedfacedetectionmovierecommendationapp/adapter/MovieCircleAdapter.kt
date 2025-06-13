package com.samyak.automatedfacedetectionmovierecommendationapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.samyak.automatedfacedetectionmovierecommendationapp.Movie
import com.samyak.automatedfacedetectionmovierecommendationapp.R

class MovieCircleAdapter(
    private var movies: List<Movie> = emptyList(),
    private val onMovieClicked: (Movie) -> Unit
) : RecyclerView.Adapter<MovieCircleAdapter.MovieCircleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieCircleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_movie_circle, parent, false)
        return MovieCircleViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieCircleViewHolder, position: Int) {
        val movie = movies[position]
        holder.bind(movie)
        
        // Set click listener on the entire item view
        holder.itemView.setOnClickListener {
            onMovieClicked(movie)
        }
    }

    override fun getItemCount(): Int = movies.size

    fun updateMovies(newMovies: List<Movie>) {
        movies = newMovies
        notifyDataSetChanged()
    }

    inner class MovieCircleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val posterImageView: ImageView = itemView.findViewById(R.id.movie_poster_circle)
        private val titleTextView: TextView = itemView.findViewById(R.id.movie_title_circle)

        fun bind(movie: Movie) {
            titleTextView.text = movie.title

            // Load poster image with Glide
            if (movie.poster.isNotEmpty() && movie.poster != "N/A") {
                Glide.with(itemView.context)
                    .load(movie.poster)
                    .placeholder(R.drawable.ic_movie_placeholder)
                    .error(R.drawable.ic_error_placeholder)
                    .centerCrop()
                    .into(posterImageView)
            } else {
                posterImageView.setImageResource(R.drawable.ic_movie_placeholder)
            }
        }
    }
} 