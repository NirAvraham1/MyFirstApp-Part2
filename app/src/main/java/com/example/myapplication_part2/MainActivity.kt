package com.example.myapplication_part2

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.myapplication_part2.utilities.SignalManager

class MainActivity : AppCompatActivity(), SensorMovementListener {

    private val logic = GameLogic

    private lateinit var grid: GridLayout
    private lateinit var leftBtn: ExtendedFloatingActionButton
    private lateinit var rightBtn: ExtendedFloatingActionButton
    private lateinit var startBtn: Button
    private lateinit var restartBtn: ExtendedFloatingActionButton
    private lateinit var menuBtn: ExtendedFloatingActionButton
    private lateinit var endButtonsLayout: LinearLayout
    private lateinit var metersText: TextView
    private lateinit var coinsText: TextView
    private lateinit var livesLayout: LinearLayout
    private lateinit var pauseBtn: ExtendedFloatingActionButton
    private var playerLat: Double = 32.114121
    private var playerLon: Double = 34.817744
    private var gameRunning = false
    private var gamePaused = false
    private var tickDelay: Long = 1000L
    private var sensorMode = false

    private var crashSound: MediaPlayer? = null
    private var coinSound: MediaPlayer? = null

    private var sensorController: SensorMovementController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        SignalManager.init(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playerLat = intent.getDoubleExtra("lat", 32.114121)
        playerLon = intent.getDoubleExtra("lon", 34.817744)

        val mode = intent.getStringExtra("mode") ?: "slow"
        tickDelay = when (mode) {
            "fast" -> 500L
            "slow" -> 1000L
            "sensor" -> {
                sensorMode = true
                1000L
            }
            else -> 1000L
        }

        crashSound = MediaPlayer.create(this, R.raw.crash_sound)
        coinSound = MediaPlayer.create(this, R.raw.coin_sound)

        grid = findViewById(R.id.grid)
        leftBtn = findViewById(R.id.left_btn)
        rightBtn = findViewById(R.id.right_btn)
        startBtn = findViewById(R.id.start_btn)
        restartBtn = findViewById(R.id.restart_btn)
        menuBtn = findViewById(R.id.menu_btn)
        endButtonsLayout = findViewById(R.id.end_game_buttons)
        metersText = findViewById(R.id.meters_text)
        coinsText = findViewById(R.id.coins_text)
        livesLayout = findViewById(R.id.lives_layout)
        pauseBtn = findViewById(R.id.pause_btn)

        if (sensorMode) {
            leftBtn.visibility = View.GONE
            rightBtn.visibility = View.GONE
        }

        pauseBtn.setOnClickListener {
            pauseGame()
            AlertDialog.Builder(this)
                .setTitle("Pause")
                .setMessage("Do you want to resume or stop the game?")
                .setPositiveButton("Stop") { _, _ ->
                    showNameInputDialog()
                }
                .setNegativeButton("Resume") { dialog, _ ->
                    resumeGame()
                    dialog.dismiss()
                }
                .setCancelable(false)
                .show()
        }

        leftBtn.setOnClickListener {
            if (!gameRunning) return@setOnClickListener
            val crashed = logic.moveLeft()
            if (crashed) vibrateAndToast("Crash!")
            updateUI()
        }

        rightBtn.setOnClickListener {
            if (!gameRunning) return@setOnClickListener
            val crashed = logic.moveRight()
            if (crashed) vibrateAndToast("Crash!")
            updateUI()
        }

        startBtn.setOnClickListener {
            startBtn.visibility = View.GONE
            logic.resetGame()
            gameRunning = true
            updateUI()
            if (sensorMode) {
                sensorController = SensorMovementController(
                    context = this,
                    listener = this,
                    getDelay = { tickDelay },
                    setDelay = { newDelay -> tickDelay = newDelay }
                )
                sensorController?.start()
            }
            startGameLoop()
        }

        restartBtn.setOnClickListener {
            endButtonsLayout.visibility = View.GONE
            logic.resetGame()
            gameRunning = true
            grid.post { updateUI() }
            if (sensorMode) {
                sensorController?.start()
            } else {
                leftBtn.visibility = View.VISIBLE
                rightBtn.visibility = View.VISIBLE
            }
            startGameLoop()
        }

        menuBtn.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        updateUI()
    }

    private fun startGameLoop() {
        lifecycleScope.launch {
            while (gameRunning && !logic.isGameOver() && !gamePaused) {
                delay(tickDelay)
                val crashed = logic.tick()
                if (logic.lastCoinCollected) {
                    coinSound?.let {
                        if (it.isPlaying) {
                            it.pause()
                            it.seekTo(0)
                        }
                        it.start()
                    }
                }

                if (crashed) vibrateAndToast("Crash!")
                updateUI()
            }

            if (logic.isGameOver()) {
                gameRunning = false
                sensorController?.stop()
                promptPlayerNameAndThenShowOptions()
            }
        }
    }

    private fun pauseGame() {
        gamePaused = true
        sensorController?.stop()
    }

    private fun resumeGame() {
        gamePaused = false
        if (sensorMode) sensorController?.start()
        startGameLoop()
    }

    private fun showNameInputDialog() {
        val input = EditText(this).apply { hint = "Enter your name" }
        AlertDialog.Builder(this)
            .setTitle("New High Score")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                val name = input.text.toString().ifBlank { "Unknown" }
                saveHighScore(name)
                goToHighScoresScreen()
            }
            .setCancelable(false)
            .show()
    }

    private fun promptPlayerNameAndThenShowOptions() {
        val input = EditText(this)
        input.hint = "Enter your name"

        AlertDialog.Builder(this)
            .setTitle("Game Over - New High Score")
            .setView(input)
            .setCancelable(false)
            .setPositiveButton("Save") { _, _ ->
                val name = input.text.toString().ifBlank { "Unknown" }
                val totalScore = logic.meters + logic.coins * 10

                HighScoreManager.addScore(
                    HighScore(
                        totalScore = totalScore,
                        latitude = playerLat,
                        longitude = playerLon,
                        date = getCurrentDate(),
                        mode = if (sensorMode) "sensor" else if (tickDelay == 500L) "fast" else "slow",
                        playerName = name
                    )
                )

                showEndGameOptions()
            }
            .show()
    }

    private fun saveHighScore(playerName: String) {
        val totalScore = logic.meters + logic.coins * 10
        HighScoreManager.addScore(
            HighScore(
                totalScore = totalScore,
                latitude = playerLat,
                longitude = playerLon,
                date = getCurrentDate(),
                mode = if (sensorMode) "sensor" else if (tickDelay == 500L) "fast" else "slow",
                playerName = playerName
            )
        )
    }

    private fun goToHighScoresScreen() {
        val intent = Intent(this, HighScoresActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showEndGameOptions() {
        leftBtn.visibility = View.GONE
        rightBtn.visibility = View.GONE
        endButtonsLayout.visibility = View.VISIBLE
    }

    private fun updateUI() {
        grid.removeAllViews()
        val cellHeight = grid.height / GameLogic.ROWS
        val cellWidth = grid.width / GameLogic.COLS

        for (i in 0 until GameLogic.ROWS) {
            for (j in 0 until GameLogic.COLS) {
                val layout = FrameLayout(this).apply {
                    layoutParams = GridLayout.LayoutParams().apply {
                        width = cellWidth
                        height = cellHeight
                    }
                }

                val value = logic.board[i][j]

                val obstacle = ImageView(this).apply {
                    setImageResource(R.drawable.ic_obstacle)
                    visibility = if (value == 1) View.VISIBLE else View.INVISIBLE
                }

                val coin = ImageView(this).apply {
                    setImageResource(R.drawable.ic_coin)
                    visibility = if (value == 2) View.VISIBLE else View.INVISIBLE
                }

                val car = ImageView(this).apply {
                    setImageResource(R.drawable.car)
                    visibility = if (i == GameLogic.CAR_ROW && j == logic.carColumn) View.VISIBLE else View.INVISIBLE
                }

                layout.addView(obstacle)
                layout.addView(coin)
                layout.addView(car)
                grid.addView(layout)
            }
        }

        metersText.text = "Meters: ${logic.meters}"
        coinsText.text = "Coins: ${logic.coins}"

        livesLayout.removeAllViews()
        repeat(logic.lives) {
            val heart = ImageView(this)
            heart.setImageResource(R.drawable.ic_heart)
            heart.layoutParams = LinearLayout.LayoutParams(100, 100)
            livesLayout.addView(heart)
        }
    }

    private fun vibrateAndToast(message: String) {
        SignalManager.getInstance().toast(message)
        SignalManager.getInstance().vibrate()
        crashSound?.let {
            if (it.isPlaying) {
                it.pause()
                it.seekTo(0)
            }
            it.start()
        }
    }

    override fun onMoveLeft() {
        if (!gameRunning) return
        val crashed = logic.moveRight()
        if (crashed) vibrateAndToast("Crash!")
        runOnUiThread { updateUI() }
    }

    override fun onMoveRight() {
        if (!gameRunning) return
        val crashed = logic.moveLeft()
        if (crashed) vibrateAndToast("Crash!")
        runOnUiThread { updateUI() }
    }

    override fun onTiltForward() {
        tickDelay = (tickDelay - 200).coerceAtLeast(200)
    }

    override fun onTiltBackward() {
        tickDelay = (tickDelay + 200).coerceAtMost(2000)
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorController?.stop()
        crashSound?.release()
        coinSound?.release()
        crashSound = null
        coinSound = null
    }

    private fun getCurrentDate(): String {
        val formatter = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
        return formatter.format(java.util.Date())
    }
}
