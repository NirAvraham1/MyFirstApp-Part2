package com.example.myapplication_part2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import android.widget.Button


class HighScoresActivity : AppCompatActivity(), OnHighScoreSelectedListener, OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private var mapReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_high_scores)

        val scores = HighScoreManager.getScores()

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_scores)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = HighScoreAdapter(scores, this)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment)
        if (mapFragment is SupportMapFragment) {
            mapFragment.getMapAsync(this)
        }
        findViewById<Button>(R.id.btn_back_to_menu).setOnClickListener {
            finish()
        }


    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        mapReady = true

        val scores = HighScoreManager.getScores()
        val defaultLocation = LatLng(32.114121, 34.817744) // Afeka College
        val location = if (scores.isNotEmpty() && scores.first().latitude != 0.0 && scores.first().longitude != 0.0) {
            LatLng(scores.first().latitude, scores.first().longitude)
        } else {
            defaultLocation
        }

        updateMap(location.latitude, location.longitude)
    }


    override fun onHighScoreSelected(lat: Double, lon: Double) {
        if (mapReady) {
            updateMap(lat, lon)
        }
    }

    private fun updateMap(lat: Double, lon: Double) {
        val location = LatLng(lat, lon)
        googleMap.clear()
        googleMap.addMarker(
            MarkerOptions().position(location).title("Selected Score")
        )
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 14f))
    }
}
