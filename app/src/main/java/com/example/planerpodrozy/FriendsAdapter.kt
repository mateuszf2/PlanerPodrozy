package com.example.planerpodrozy
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class FriendsAdapter : ListAdapter<Friend, FriendsAdapter.FriendViewHolder>(FriendDiffCallback()) {

    private var onEventClickListener: OnEventClickListener? = null

    fun setOnEventClickListener(listener: OnEventClickListener) {
        onEventClickListener = listener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_friend, parent, false)
        return FriendViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val currentEvent = getItem(position)
        holder.bind(currentEvent)
        holder.itemView.setOnClickListener {
            val friend = getItem(holder.adapterPosition)
            onEventClickListener?.onEventClick(friend)
        }

    }

    inner class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val friendNameTextView: TextView = itemView.findViewById(R.id.textView_friendName)

        fun bind(friend: Friend) {
            val currentUser= FirebaseAuth.getInstance().currentUser
            if (currentUser?.uid == friend.userId){
                friendNameTextView.text = friend.friendEmail
            }
            else{
                friendNameTextView.text = friend.userEmail
            }

        }
    }

    class FriendDiffCallback : DiffUtil.ItemCallback<Friend>() {
        override fun areItemsTheSame(oldItem: Friend, newItem: Friend): Boolean {
            return oldItem.friendEmail == newItem.friendEmail
        }

        override fun areContentsTheSame(oldItem:Friend, newItem: Friend): Boolean {
            return oldItem == newItem
        }
    }

    interface OnEventClickListener{
        fun onEventClick(friend: Friend)
    }
}
