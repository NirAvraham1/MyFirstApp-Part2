package com.example.myapplication_part2

object HighScoreManager {

    private val scores = mutableListOf<HighScore>()

    fun addScore(score: HighScore) {
        scores.add(score)
        scores.sortByDescending { it.totalScore }
    }

    fun getScores(): List<HighScore> {
        return scores
    }

    fun clearScores() {
        scores.clear()
    }
}
