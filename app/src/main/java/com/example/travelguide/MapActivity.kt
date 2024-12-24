package com.example.travelguide

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.travelguide.database.Trip
import com.example.travelguide.database.TripDatabase
import kotlinx.coroutines.launch

class MapActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private var tripId: Long = -1
    private lateinit var tripDatabase: TripDatabase

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // Ініціалізація WebView
        webView = findViewById(R.id.webView)
        webView.settings.apply {
            javaScriptEnabled = true
            loadWithOverviewMode = true
            useWideViewPort = true
            builtInZoomControls = true
            displayZoomControls = false
        }


        webView.settings.userAgentString =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36"

        // Веб-клієнт для обробки переходів
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                // Мета-тег для масштабування
                view?.evaluateJavascript(
                    """
                var meta = document.createElement('meta');
                meta.name = 'viewport';
                meta.content = 'width=device-width, initial-scale=0.8, maximum-scale=1.0, user-scalable=no';
                document.getElementsByTagName('head')[0].appendChild(meta);
                """,
                    null
                )
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url ?: "")
                return true
            }
        }

        tripDatabase = TripDatabase.getDatabase(this)

        tripId = intent.getLongExtra("TRIP_ID", 0)
        val routeUrl = intent.getStringExtra("ROUTE")

        if (tripId == 0L) {
            val gmapsUrl = "https://www.google.com/maps"
            webView.loadUrl(gmapsUrl)
        } else {
            if (routeUrl != null && routeUrl.isNotBlank()) {
                webView.loadUrl(routeUrl)
            } else {
                Toast.makeText(this, "Маршрут не знайдено!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        val finishButton: Button = findViewById(R.id.finishButton)
        finishButton.setOnClickListener {
            val currentUrl = webView.url
            if (!currentUrl.isNullOrBlank()) {
                lifecycleScope.launch {
                    val trip = tripDatabase.tripDao().getTripById(tripId)
                    if (trip != null) {
                        trip.route = currentUrl
                        tripDatabase.tripDao().update(trip)
                        Toast.makeText(this@MapActivity, "Маршрут збережено!", Toast.LENGTH_SHORT).show()
                    }
                }

                val intent = Intent()
                intent.putExtra("ROUTE", currentUrl)
                setResult(RESULT_OK, intent)
                finish()
            } else {
                Toast.makeText(this, "Маршрут не знайдено!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
