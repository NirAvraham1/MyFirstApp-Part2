package com.example.myapplication_part2

import com.example.myapplication_part2.utilities.SharedPreferencesManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object HighScoreManager {

    private const val KEY_SCORES = "high_scores_list"

    private val gson = Gson()
    private var scores: MutableList<HighScore> = mutableListOf()

    fun init(context: android.content.Context) {
        SharedPreferencesManager.init(context)
        loadScores()
    }

    fun addScore(score: HighScore) {
        scores.add(score)
        scores.sortByDescending { it.totalScore }
        if (scores.size > 10) {
            scores = scores.take(10).toMutableList()
        }
        saveScores()
    }

    fun getScores(): List<HighScore> = scores

    private fun saveScores() {
        val json = gson.toJson(scores)
        SharedPreferencesManager.getInstance().putString(KEY_SCORES, json)
    }

    private fun loadScores() {
        val json = SharedPreferencesManager.getInstance().getString(KEY_SCORES, "")
        if (json.isNotEmpty()) {
            val type = object : TypeToken<MutableList<HighScore>>() {}.type
            scores = gson.fromJson(json, type)
        }
    }
}
