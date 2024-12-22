package com.example.travelguide

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.travelguide.database.TripDatabase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var gestureDetector: GestureDetector
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TripAdapter
    private lateinit var tripDatabase: TripDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d("MainActivity", "onCreate: Ініціалізація активності")

        // Ініціалізація бази даних
        tripDatabase = TripDatabase.getDatabase(this)
        Log.d("MainActivity", "onCreate: База даних ініціалізована")

        // Налаштування RecyclerView
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        Log.d("MainActivity", "onCreate: RecyclerView налаштований")

        // Ініціалізація адаптера
        adapter = TripAdapter { selectedTrip ->
            Log.d("MainActivity", "onCreate: Вибрано подорож із ID = ${selectedTrip.id}")
            navigateToCreateTrip(selectedTrip.id)
        }
        recyclerView.adapter = adapter

        // Додати підказку
        showSwipeHint()

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
                        Log.d("MainActivity", "onFling: Виявлено свайп ліворуч")
                        navigateToCreateTrip(0)
                        return true
                    }
                }
                return false
            }
        })

        // Підписка на зміни в базі даних
        observeTrips()
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        Log.d("MainActivity", "updateEmptyState: Список подорожей пустий = $isEmpty")
        findViewById<View>(R.id.empty_state_view).visibility = if (isEmpty) View.VISIBLE else View.GONE
        recyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    private fun observeTrips() {
        lifecycleScope.launch {
            try {
                tripDatabase.tripDao().getAllTrips().collectLatest { trips ->
                    Log.d("MainActivity", "observeTrips: Отримано список подорожей: ${trips.size} елементів")
                    updateEmptyState(trips.isEmpty())
                    adapter.submitList(trips)  // Оновлюємо дані в адаптері
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "observeTrips: Помилка при отриманні подорожей", e)
            }
        }
    }


    private fun navigateToCreateTrip(tripId: Long) {
        Log.d("MainActivity", "navigateToCreateTrip: Переход до CreateTripActivity з TRIP_ID = $tripId")
        val intent = Intent(this, CreateTripActivity::class.java).apply {
            putExtra("TRIP_ID", tripId)
        }
        startActivity(intent)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val result = event?.let { gestureDetector.onTouchEvent(it) } == true || super.onTouchEvent(event)
        Log.d("MainActivity", "onTouchEvent: Подія торкання, результат = $result")
        return result
    }

    private fun showSwipeHint() {
        Log.d("MainActivity", "showSwipeHint: Показуємо підказку для свайпу")
        Toast.makeText(this, "Свайпніть ліворуч, щоб створити нову подорож", Toast.LENGTH_LONG).show()
    }
}
