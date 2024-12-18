package com.example.travelguide

import android.content.Intent
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity() {

    private lateinit var gestureDetector: GestureDetector


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Обробка пошуку
        val searchIcon = findViewById<ImageView>(R.id.ic_search)
        searchIcon.setOnClickListener {
            // TODO: Реалізувати пошук
        }

        // Обробка календаря
        val calendarIcon = findViewById<ImageView>(R.id.ic_calendar)
        calendarIcon.setOnClickListener {
            // TODO: Відкрити календар
        }

        // GestureDetector для свайпу
        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                if (e1 != null && e2 != null) {
                    val deltaX = e2.x - e1.x
                    if (deltaX < -100) { // Свайп з права наліво
                        navigateToCreateTrip()
                        return true
                    }
                }
                return false
            }
        })
    }

    private fun navigateToCreateTrip() {
        val intent = Intent(this, CreateTripActivity::class.java)
        startActivity(intent)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return event?.let { gestureDetector.onTouchEvent(it) } == true || super.onTouchEvent(event)
    }

    override fun onResume() {
        super.onResume()
        showHint()
    }

    private fun showHint() {
        val rootView = findViewById<View>(android.R.id.content)
        Snackbar.make(rootView, "Свайпніть вліво для створення нової подорожі", Snackbar.LENGTH_LONG)
            .setAction("OK") { } // Можна додати дію, якщо потрібно
            .show()
    }
}
