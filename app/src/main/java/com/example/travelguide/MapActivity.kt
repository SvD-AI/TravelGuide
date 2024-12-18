package com.example.travelguide

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.util.BoundingBox

class MapActivity : AppCompatActivity() {
    private lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(this, this.getPreferences(MODE_PRIVATE))
        setContentView(R.layout.activity_map)

        mapView = findViewById(R.id.mapView)
        mapView.setMultiTouchControls(true)

        // Встановлення меж для єдиної мапи світу
        val worldBounds = BoundingBox(85.0, 180.0, -85.0, -180.0)
        mapView.setScrollableAreaLimitDouble(worldBounds)

        // Початкова позиція
        val startPoint = GeoPoint(0.0, 0.0)
        mapView.controller.setZoom(2.0)
        mapView.controller.setCenter(startPoint)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }
}
