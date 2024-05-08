package com.example.planerpodrozy

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class MessageAdapter() : ListAdapter<MessageModel, MessageAdapter.MessageViewHolder>
    (MessageDiffCallback()) {

    private var onEventClickListener: OnEventClickListener? = null


    private val db= Firebase.firestore
    private val left = 0
    private val right  = 1

    fun setOnEventClickListener(listener: OnEventClickListener) {
        onEventClickListener = listener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return if(viewType==right){
            val messageView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_sender,parent,false)
            return MessageViewHolder(messageView)
        }
        else{
            val messageView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_receiver,parent,false)
            return MessageViewHolder(messageView)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if(getItem(position).messageSender==FirebaseAuth.getInstance().currentUser?.email.toString()){
            return right
        }
        else{
            return left
        }
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val currentMessage = getItem(position)
        holder.bind(currentMessage)
    }

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageTextView: TextView = itemView.findViewById(R.id.txtMessage)
        private val timeTextView: TextView = itemView.findViewById(R.id.txtTime)
        private val nameTextView: TextView = itemView.findViewById(R.id.txtName)
        fun bind(message: MessageModel) {
            messageTextView.text=message.message
            timeTextView.text=message.messageTime
            nameTextView.text=message.messageSender
        }
    }

    class MessageDiffCallback : DiffUtil.ItemCallback<MessageModel>() {
        override fun areItemsTheSame(oldItem: MessageModel, newItem: MessageModel): Boolean {
            return true
        }

        override fun areContentsTheSame(oldItem:MessageModel, newItem:MessageModel): Boolean {
            return true
        }
    }

    interface OnEventClickListener{
    }
}


