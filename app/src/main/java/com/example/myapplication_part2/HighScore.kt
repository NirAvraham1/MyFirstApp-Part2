package com.example.myapplication_part2

data class HighScore(
    val totalScore: Int,
    val latitude: Double,
    val longitude: Double,
    val date: String,
    val mode: String,
    val playerName: String
)
