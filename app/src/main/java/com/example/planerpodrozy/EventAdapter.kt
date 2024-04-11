package com.example.planerpodrozy
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class EventAdapter(private val onDeleteClickListener: (Event) -> Unit) : ListAdapter<Event, EventAdapter.EventViewHolder>(EventDiffCallback()) {

    private var onEventClickListener: OnEventClickListener? = null

    fun setOnEventClickListener(listener: OnEventClickListener) {
        onEventClickListener = listener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return EventViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val currentEvent = getItem(position)
        holder.bind(currentEvent)
            holder.itemView.setOnClickListener {
            val event = getItem(holder.adapterPosition)
            onEventClickListener?.onEventClick(event)
        }

    }

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val eventNameTextView: TextView = itemView.findViewById(R.id.textView_eventName)
        private val deleteButton: Button = itemView.findViewById(R.id.buttonDeleteEvent)

        fun bind(event: Event) {
            eventNameTextView.text = event.eventName
            if (event.eventName.isNullOrBlank()) {
                deleteButton.visibility = View.GONE
            } else {
                deleteButton.visibility = View.VISIBLE
                deleteButton.setOnClickListener {
                    onDeleteClickListener.invoke(event)
                }
            }
        }
    }

    class EventDiffCallback : DiffUtil.ItemCallback<Event>() {
        override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem.eventId == newItem.eventId
        }

        override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem == newItem
        }
    }

    interface OnEventClickListener{
        fun onEventClick(event: Event)
    }
}
