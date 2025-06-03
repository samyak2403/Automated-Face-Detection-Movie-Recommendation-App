package com.samyak.automatedfacedetectionmovierecommendationapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ProfileFragment : Fragment() {

    private val viewModel: MoodViewModel by activityViewModels()
    private lateinit var moodHistoryRecyclerView: RecyclerView
    private lateinit var moodHistoryAdapter: MoodHistoryAdapter
    private lateinit var clearHistoryButton: Button
    private lateinit var githubButton: Button
    private lateinit var developerImage: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Initialize the ViewModel with context for SharedPreferences
        viewModel.initMoodHistoryManager(requireContext())
        
        // Find views
        moodHistoryRecyclerView = view.findViewById(R.id.mood_history_recycler_view)
        clearHistoryButton = view.findViewById(R.id.clear_history_button)
        githubButton = view.findViewById(R.id.github_button)
        developerImage = view.findViewById(R.id.developer_image)
        
        setupRecyclerView()
        observeViewModel()
        setupClickListeners()
    }
    
    private fun setupClickListeners() {
        clearHistoryButton.setOnClickListener {
            viewModel.clearMoodHistory()
        }
        
        githubButton.setOnClickListener {
            // Open GitHub profile in browser
            val githubUrl = "https://github.com/samyak2403"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(githubUrl))
            startActivity(intent)
        }
    }
    
    private fun setupRecyclerView() {
        moodHistoryAdapter = MoodHistoryAdapter()
        moodHistoryRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = moodHistoryAdapter
        }
    }
    
    private fun observeViewModel() {
        viewModel.moodHistory.observe(viewLifecycleOwner) { moodEntries ->
            moodHistoryAdapter.submitList(moodEntries)
        }
    }
}