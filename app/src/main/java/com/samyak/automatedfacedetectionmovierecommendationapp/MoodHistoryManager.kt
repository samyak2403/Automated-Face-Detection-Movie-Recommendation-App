package com.samyak.automatedfacedetectionmovierecommendationapp

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MoodHistoryManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("mood_history", Context.MODE_PRIVATE)
    private val gson = Gson()
    
    data class MoodEntry(val mood: String, val timestamp: Long) {
        fun getFormattedDate(): String {
            val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }
    }
    
    fun addMoodEntry(mood: String) {
        val entries = getMoodHistory().toMutableList()
        entries.add(0, MoodEntry(mood, System.currentTimeMillis()))
        
        // Keep only last 20 entries
        val limitedEntries = if (entries.size > 20) entries.subList(0, 20) else entries
        
        val json = gson.toJson(limitedEntries)
        prefs.edit().putString("mood_entries", json).apply()
    }
    
    fun getMoodHistory(): List<MoodEntry> {
        val json = prefs.getString("mood_entries", null) ?: return emptyList()
        val type = object : TypeToken<List<MoodEntry>>() {}.type
        return gson.fromJson(json, type)
    }
    
    fun clearHistory() {
        prefs.edit().remove("mood_entries").apply()
    }
}

