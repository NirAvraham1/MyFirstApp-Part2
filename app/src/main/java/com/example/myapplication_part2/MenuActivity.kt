package com.example.myapplication_part2

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MenuActivity : AppCompatActivity() {

    companion object {
        var lat: Double = 32.115139
        var lon: Double = 34.817804
    }

    private val locationcode = 1002
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var locationReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        HighScoreManager.init(applicationContext)
        val slowBtn: Button = findViewById(R.id.btn_slow)
        val fastBtn: Button = findViewById(R.id.btn_fast)
        val sensorBtn: Button = findViewById(R.id.btn_sensor)
        val btnHighScores = findViewById<Button>(R.id.btn_high_scores)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        checkLocationPermission()

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
        if (!locationReady) {
            Toast.makeText(this, "Waiting for location...", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("lat", lat)
        intent.putExtra("lon", lon)
        intent.putExtra("mode", mode)
        startActivity(intent)
    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getLastLocationSafely()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                locationcode
            )
        }
    }

    private fun getLastLocationSafely() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    lat = location.latitude
                    lon = location.longitude
                    locationReady = true
                    Log.d("LOCATION_DEBUG", "✅ Got location: $lat, $lon")
                } else {
                    Log.w("LOCATION_DEBUG", "❌ Location is null – possibly cold start or GPS not ready")
                }
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationcode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocationSafely()
                Toast.makeText(this, "Location permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
