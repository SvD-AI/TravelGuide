package com.example.travelguide

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieAnimationView
import com.example.travelguide.database.Trip
import com.example.travelguide.database.TripDatabase
import kotlinx.coroutines.launch
import java.util.*

class CreateTripActivity : AppCompatActivity() {

    private lateinit var tripNameEditText: EditText
    private lateinit var datePicker: DatePicker
    private lateinit var saveButton: Button
    private lateinit var notesEditText: EditText
    private lateinit var travelAnimation: LottieAnimationView
    private var tripId: Long = 0
    private var temporaryTrip: Trip? = null

    private val tripDao by lazy {
        TripDatabase.getDatabase(this).tripDao()
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_trip)

        tripNameEditText = findViewById(R.id.trip_name)
        datePicker = findViewById(R.id.date_picker)
        saveButton = findViewById(R.id.saveButton)
        notesEditText = findViewById(R.id.notesEditText)
        travelAnimation = findViewById(R.id.travelAnimation)

        tripId = intent.getLongExtra("TRIP_ID", 0)
        if (tripId != 0L) {
            loadTripData()
        } else {
            temporaryTrip = Trip(name = "", date = System.currentTimeMillis(), notes = "", route = null)
        }

        val calendar = Calendar.getInstance()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            datePicker.minDate = calendar.timeInMillis
        }

        saveButton.setOnClickListener {
            saveTripData()
        }

        travelAnimation.setOnClickListener {
            val route = temporaryTrip?.route
            val intent = Intent(this, MapActivity::class.java)
            intent.putExtra("TRIP_ID", tripId)
            route?.let { intent.putExtra("ROUTE", it) }
            startActivityForResult(intent, 1)
        }
    }

    private fun loadTripData() {
        lifecycleScope.launch {
            val trip = tripDao.getTripById(tripId)
            trip?.let {
                tripNameEditText.setText(it.name)
                notesEditText.setText(it.notes)
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = it.date
                datePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
                temporaryTrip = it
            }
        }
    }

    private fun saveTripData() {
        val tripName = tripNameEditText.text.toString()
        if (tripName.isBlank()) {
            Toast.makeText(this, "Назва подорожі не може бути порожньою!", Toast.LENGTH_SHORT).show()
            return
        }

        val currentDate = Calendar.getInstance().timeInMillis
        val calendar = Calendar.getInstance()
        calendar.set(datePicker.year, datePicker.month, datePicker.dayOfMonth)
        val tripDate = calendar.timeInMillis

        if (tripDate < currentDate) {
            Toast.makeText(this, "Дата не може бути менша за поточну!", Toast.LENGTH_SHORT).show()
            return
        }

        val tripNotes = notesEditText.text.toString()

        val updatedTrip = temporaryTrip?.copy(
            name = tripName,
            date = tripDate,
            notes = tripNotes
        ) ?: Trip(name = tripName, date = tripDate, notes = tripNotes)

        lifecycleScope.launch {
            try {
                tripDao.insertOrUpdate(updatedTrip)
                Log.d("TripDao", "Додано або оновлено подорож: $updatedTrip")
                Toast.makeText(this@CreateTripActivity, "Подорож збережена!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("CreateTripActivity", "Error saving trip data", e)
                Toast.makeText(this@CreateTripActivity, "Помилка збереження подорожі. Спробуйте ще раз.", Toast.LENGTH_SHORT).show()
            }
            finish()
        }
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            val updatedRoute = data?.getStringExtra("ROUTE")
            if (updatedRoute != null) {
                temporaryTrip?.route = updatedRoute
                Toast.makeText(this, "Маршрут оновлено!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}