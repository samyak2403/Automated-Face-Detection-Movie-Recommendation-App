package com.samyak.automatedfacedetectionmovierecommendationapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RecommendationsFragment : Fragment() {

    private val viewModel: MoodViewModel by activityViewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var moodTitleTextView: TextView
    private lateinit var movieAdapter: MovieAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recommendations, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.movies_recycler_view)
        loadingIndicator = view.findViewById(R.id.loading_indicator)
        moodTitleTextView = view.findViewById(R.id.mood_title)

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        movieAdapter = MovieAdapter(object : MovieAdapter.OnMovieClickListener {
            override fun onMovieClick(movie: Movie) {
                // Open movie detail fragment
                val detailFragment = MovieDetailFragment.newInstance(movie)
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, detailFragment)
                    .addToBackStack(null)
                    .commit()
            }
        })
        recyclerView.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = movieAdapter
        }
    }

    private fun observeViewModel() {
        // Observe current mood
        viewModel.currentMood.observe(viewLifecycleOwner) { mood ->
            if (mood != null) {
                moodTitleTextView.text = "Your Mood: ${mood.capitalize()}"
            }
        }

        // Observe movie recommendations
        viewModel.movieRecommendations.observe(viewLifecycleOwner) { movies ->
            movieAdapter.submitList(movies)
            if (movies.isEmpty()) {
                // If we're not loading and have no movies, show a message
                if (viewModel.isLoading.value == false) {
                    Toast.makeText(context, "No movies found. Try a different mood.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            loadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observe error state
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            }
        }
    }
}