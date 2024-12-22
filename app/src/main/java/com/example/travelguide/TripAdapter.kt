package com.example.travelguide

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.travelguide.database.Trip

class TripAdapter(
    private val onTripClick: (Trip) -> Unit
) : ListAdapter<Trip, TripAdapter.TripViewHolder>(TripDiffCallback()) {

    companion object {
        private const val TAG = "TripAdapter"
    }

    class TripViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.card_title)
        val date: TextView = view.findViewById(R.id.card_date)
        val notes: TextView = view.findViewById(R.id.card_notes)

        @SuppressLint("SetTextI18n")
        fun bind(trip: Trip, onClick: (Trip) -> Unit) {
            Log.d(TAG, "Binding trip: ${trip.name}, id: ${trip.id}")
            title.text = trip.name
            date.text = formatDate(trip.date)
            notes.text = "Нотатки: ${trip.notes ?: "Немає"}"
            itemView.setOnClickListener {
                Log.d(TAG, "Trip clicked: ${trip.name}, id: ${trip.id}")
                onClick(trip)
            }
        }

        private fun formatDate(timestamp: Long): String {
            val sdf = java.text.SimpleDateFormat("dd.MM.yyyy", java.util.Locale.getDefault())
            return sdf.format(java.util.Date(timestamp))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.trip_card, parent, false)
        return TripViewHolder(view)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        val trip = getItem(position)  // Отримуємо подорож з позиції
        Log.d(TAG, "Binding ViewHolder at position $position with trip: ${trip.name}, id: ${trip.id}")
        holder.bind(trip, onTripClick)  // Підключаємо обробку кліку
    }


    class TripDiffCallback : DiffUtil.ItemCallback<Trip>() {
        override fun areItemsTheSame(oldItem: Trip, newItem: Trip): Boolean {
            val isSame = oldItem.id == newItem.id
            Log.d(TAG, "areItemsTheSame: ${oldItem.id} == ${newItem.id} => $isSame")
            return isSame
        }

        override fun areContentsTheSame(oldItem: Trip, newItem: Trip): Boolean {
            val isContentSame = oldItem == newItem
            Log.d(TAG, "areContentsTheSame: $isContentSame")
            return isContentSame
        }
    }
}