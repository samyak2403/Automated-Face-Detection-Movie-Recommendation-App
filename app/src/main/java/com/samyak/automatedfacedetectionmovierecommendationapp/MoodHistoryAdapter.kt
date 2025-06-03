package com.samyak.automatedfacedetectionmovierecommendationapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class MoodHistoryAdapter : ListAdapter<MoodHistoryManager.MoodEntry, MoodHistoryAdapter.MoodHistoryViewHolder>(MoodHistoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodHistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mood_history, parent, false)
        return MoodHistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: MoodHistoryViewHolder, position: Int) {
        val moodEntry = getItem(position)
        holder.bind(moodEntry)
    }

    class MoodHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val moodTextView: TextView = itemView.findViewById(R.id.mood_text)
        private val dateTextView: TextView = itemView.findViewById(R.id.date_text)

        fun bind(moodEntry: MoodHistoryManager.MoodEntry) {
            moodTextView.text = moodEntry.mood
            dateTextView.text = moodEntry.getFormattedDate()
        }
    }

    class MoodHistoryDiffCallback : DiffUtil.ItemCallback<MoodHistoryManager.MoodEntry>() {
        override fun areItemsTheSame(oldItem: MoodHistoryManager.MoodEntry, newItem: MoodHistoryManager.MoodEntry): Boolean {
            return oldItem.timestamp == newItem.timestamp
        }

        override fun areContentsTheSame(oldItem: MoodHistoryManager.MoodEntry, newItem: MoodHistoryManager.MoodEntry): Boolean {
            return oldItem == newItem
        }
    }
}