package com.example.myapplication_part2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var latitude = 32.114121
    private var longitude = 34.817744

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)


        latitude = intent.getDoubleExtra("latitude", 32.114121)
        longitude = intent.getDoubleExtra("longitude", 34.817744)

        if (latitude == 0.0 && longitude == 0.0) {
            latitude = 32.114121
            longitude = 34.817744
        }


        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val location = LatLng(latitude, longitude)
        mMap.addMarker(MarkerOptions().position(location).title("High Score Location"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16f))

    }

}
