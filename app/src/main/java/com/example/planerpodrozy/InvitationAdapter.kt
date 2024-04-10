package com.example.planerpodrozy
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class InvitationAdapter : ListAdapter<Event, InvitationAdapter.InvitationViewHolder>(InvitationDiffCallback()) {

    private var onEventClickListener: OnEventClickListener? = null

    fun setOnEventClickListener(listener: OnEventClickListener) {
        onEventClickListener = listener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvitationViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_invitation, parent, false)
        return InvitationViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: InvitationViewHolder, position: Int) {
        val currentEvent = getItem(position)
        holder.bind(currentEvent)
    }

    inner class InvitationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val eventNameTextView: TextView = itemView.findViewById(R.id.textView_eventName)
        private val buttonAccept: Button = itemView.findViewById(R.id.buttonAccept)
        private val buttonCancel: Button = itemView.findViewById(R.id.buttonCancel)

        fun bind(event: Event) {
            eventNameTextView.text = event.eventName

            //Obsługa kliknięcia przycisków accept i cancel
            buttonAccept.setOnClickListener {
                val position = adapterPosition
                if(position != RecyclerView.NO_POSITION) {
                    val event = getItem(position)
                    onEventClickListener?.onEventAccept(event)
                }
            }

            buttonCancel.setOnClickListener {
                val position = adapterPosition
                if(position != RecyclerView.NO_POSITION) {
                    val event = getItem(position)
                    onEventClickListener?.onEventCancel(event)
                }
            }
        }
    }

    class InvitationDiffCallback : DiffUtil.ItemCallback<Event>() {
        override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem.eventId == newItem.eventId
        }

        override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem == newItem
        }
    }

    interface OnEventClickListener{
        fun onEventAccept(event: Event)
        fun onEventCancel(event: Event)
    }

}
