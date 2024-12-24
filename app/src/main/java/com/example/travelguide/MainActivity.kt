package com.example.travelguide

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.travelguide.database.Trip
import com.example.travelguide.database.TripDatabase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var gestureDetector: GestureDetector
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TripAdapter
    private lateinit var tripDatabase: TripDatabase
    private var isSearchBarVisible = false
    private var currentSearchQuery: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d("MainActivity", "onCreate: Ініціалізація активності")
        tripDatabase = TripDatabase.getDatabase(this)

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = TripAdapter(
            onTripClick = { selectedTrip ->
                Log.d("MainActivity", "onCreate: Вибрано подорож із ID = ${selectedTrip.id}")
                navigateToCreateTrip(selectedTrip.id)
            },
            onTripLongPress = { selectedTrip ->
                Log.d("MainActivity", "onCreate: Видалення подорожі із ID = ${selectedTrip.id}")
                deleteTrip(selectedTrip)
            }
        )
        recyclerView.adapter = adapter

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
                    if (deltaX < -100) {
                        Log.d("MainActivity", "onFling: Виявлено свайп ліворуч")
                        navigateToCreateTrip(0)
                        return true
                    }
                }
                return false
            }
        })

        setupSearchAnimation()

        observeTrips()
    }

    private fun setupSearchAnimation() {
        val searchIcon = findViewById<ImageView>(R.id.ic_search)
        val searchBar = findViewById<EditText>(R.id.search_bar)

        searchIcon.setOnClickListener {
            if (!isSearchBarVisible) {
                searchBar.visibility = View.VISIBLE
                searchBar.requestFocus()
                isSearchBarVisible = true
            } else {
                searchBar.visibility = View.GONE
                searchBar.text.clear()
                currentSearchQuery = null
                isSearchBarVisible = false
                observeTrips()
            }
        }

        searchBar.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && isSearchBarVisible) {
                searchBar.visibility = View.GONE
                isSearchBarVisible = false
            }
        }

        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                currentSearchQuery = charSequence.toString()
                observeTrips()
            }

            override fun afterTextChanged(editable: Editable?) {
            }
        })

    }

    private fun updateEmptyState(isEmpty: Boolean) {
        findViewById<View>(R.id.empty_state_view).visibility = if (isEmpty) View.VISIBLE else View.GONE
        recyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    private fun observeTrips() {
        lifecycleScope.launch {
            try {
                val tripsFlow = if (currentSearchQuery.isNullOrEmpty()) {
                    tripDatabase.tripDao().getAllTrips()
                } else {
                    tripDatabase.tripDao().searchTrips("%${currentSearchQuery}%")
                }

                tripsFlow.collectLatest { trips ->
                    updateEmptyState(trips.isEmpty())
                    adapter.submitList(trips)
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "observeTrips: Помилка при отриманні подорожей", e)
            }
        }
    }

    private fun navigateToCreateTrip(tripId: Long) {
        val intent = Intent(this, CreateTripActivity::class.java).apply {
            putExtra("TRIP_ID", tripId)
        }
        startActivity(intent)
    }

    private fun deleteTrip(trip: Trip) {
        lifecycleScope.launch {
            try {
                tripDatabase.tripDao().deleteTrip(trip)
                Toast.makeText(this@MainActivity, "Подорож видалено", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("MainActivity", "deleteTrip: Не вдалося видалити подорож", e)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return event?.let { gestureDetector.onTouchEvent(it) } == true || super.onTouchEvent(event)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.let { gestureDetector.onTouchEvent(it) } == true) {
            return true
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun showSwipeHint() {
        Toast.makeText(this, "Свайпніть ліворуч, щоб створити нову подорож", Toast.LENGTH_LONG).show()
    }
}
