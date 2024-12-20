package com.example.travelguide

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MapActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        webView = findViewById(R.id.webView)
        val finishButton: Button = findViewById(R.id.finishButton)

        // Налаштування WebView
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.loadWithOverviewMode = true
        webView.settings.useWideViewPort = true
        webView.settings.userAgentString =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36"

        // Додати мета-тег для правильного масштабування
        webView.loadUrl("https://www.google.com/maps?z=12&ll=50.4501,30.5186")  // Можеш змінити координати на свої

        // Веб-клієнт для обробки переходів
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                // Після завершення завантаження сторінки додати мета-тег для масштабування
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

        // Інтерфейс для отримання URL
        webView.addJavascriptInterface(object {
            @JavascriptInterface
            fun onRouteBuilt(url: String) {
                runOnUiThread {
                    Toast.makeText(this@MapActivity, "Route URL: $url", Toast.LENGTH_LONG).show()
                }
            }
        }, "AndroidBridge")

        // Обробка кнопки "Завершити вибір"
        finishButton.setOnClickListener {
            val currentUrl = webView.url
            if (currentUrl != null) {
                Toast.makeText(this, "Route saved: $currentUrl", Toast.LENGTH_LONG).show()
                // Збереження або передача URL
            } else {
                Toast.makeText(this, "No route found!", Toast.LENGTH_SHORT).show()
            }
        }
    }

}