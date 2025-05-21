package com.example.myapplication_part2

object GameLogic {
    const val ROWS = 6
    const val COLS = 5
    const val CAR_ROW = ROWS - 1

    val board = Array(ROWS) { IntArray(COLS) }
    var carColumn = COLS / 2
    var lives = 3
    var meters = 0
    var coins = 0

    var lastCoinCollected = false

    fun resetGame() {
        for (i in 0 until ROWS) {
            for (j in 0 until COLS) {
                board[i][j] = 0
            }
        }
        carColumn = COLS / 2
        lives = 3
        meters = 0
        coins = 0
        lastCoinCollected = false
    }

    fun moveLeft(): Boolean {
        if (carColumn > 0) carColumn--
        return checkCrash()
    }

    fun moveRight(): Boolean {
        if (carColumn < COLS - 1) carColumn++
        return checkCrash()
    }

    fun tick(): Boolean {
        // הזזת השורות למטה
        for (i in ROWS - 1 downTo 1) {
            board[i] = board[i - 1].copyOf()
        }

        // שורה חדשה עליונה ריקה
        board[0] = IntArray(COLS) { generateRandomObject() }

        meters++

        // בדיקת התנגשות
        return checkCrash()
    }

    private fun generateRandomObject(): Int {
        val chance = (1..100).random()
        return when {
            chance <= 10 -> 1 // מכשול
            chance <= 20 -> 2 // מטבע
            else -> 0 // ריק
        }
    }

    private fun checkCrash(): Boolean {
        return when (board[CAR_ROW][carColumn]) {
            1 -> {
                lastCoinCollected = false
                lives--
                board[CAR_ROW][carColumn] = 0
                true
            }
            2 -> {
                lastCoinCollected = true
                coins++
                board[CAR_ROW][carColumn] = 0
                false
            }
            else -> {
                lastCoinCollected = false
                false
            }
        }
    }

    fun isGameOver(): Boolean = lives <= 0
}
