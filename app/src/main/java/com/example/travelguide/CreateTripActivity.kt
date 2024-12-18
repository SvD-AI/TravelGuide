package com.example.travelguide

import android.os.Build
import android.os.Bundle
import android.widget.DatePicker
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import com.airbnb.lottie.LottieAnimationView
import java.util.*

class CreateTripActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_trip)

        val animationView: LottieAnimationView = findViewById(R.id.travelAnimation)

        animationView.setOnClickListener {
            // Анімація збільшення
            animationView.animate().scaleX(1.5f).scaleY(1.5f).setDuration(500).withEndAction {
                // Відновлення початкового стану Lottie-анімації
                animationView.scaleX = 1f
                animationView.scaleY = 1f

                // Перехід з ефектом наближення
                val intent = Intent(this, MapActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.zoom_in, R.anim.fade_out)
            }.start()
        }


        val datePicker = findViewById<DatePicker>(R.id.date_picker)

        // Забороняємо вибір дати у минулому
        val calendar = Calendar.getInstance()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            datePicker.minDate = calendar.timeInMillis
        }


    }
}
