package com.example.myapplication_part2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) !=
            android.content.pm.PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
        val slowBtn: Button = findViewById(R.id.btn_slow)
        val fastBtn: Button = findViewById(R.id.btn_fast)
        val sensorBtn: Button = findViewById(R.id.btn_sensor)
        val btnHighScores = findViewById<Button>(R.id.btn_high_scores)

        slowBtn.setOnClickListener {
            startGameWithMode("slow")
        }

        fastBtn.setOnClickListener {
            startGameWithMode("fast")
        }

        sensorBtn.setOnClickListener {
            startGameWithMode("sensor")
        }

        btnHighScores.setOnClickListener {
            val intent = Intent(this, HighScoresActivity::class.java)
            startActivity(intent)
        }


    }

    private fun startGameWithMode(mode: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("mode", mode)
        startActivity(intent)
    }
}
