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

class FriendsInvitationsAdapter : ListAdapter<Friend, FriendsInvitationsAdapter.FriendsInvitationViewHolder>(InvitationDiffCallback()) {

    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid
    private var onEventClickListener: OnEventClickListener? = null

    fun setOnEventClickListener(listener: OnEventClickListener) {
        onEventClickListener = listener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsInvitationViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_friends_invitation, parent, false)
        return FriendsInvitationViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FriendsInvitationViewHolder, position: Int) {
        val currentEvent = getItem(position)
        holder.bind(currentEvent)
    }

    inner class FriendsInvitationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val friendTextView: TextView = itemView.findViewById(R.id.textView_friendEmail)
        private val buttonAccept: Button = itemView.findViewById(R.id.buttonAcceptFriend)
        private val buttonCancel: Button = itemView.findViewById(R.id.buttonCancelFriend)

        fun bind(friend: Friend) {
            if (friend.userId==userId){
                friendTextView.text = friend.friendEmail
            }
            else friendTextView.text = friend.userEmail


            //Obsługa kliknięcia przycisków accept i cancel
            buttonAccept.setOnClickListener {
                val position = adapterPosition
                if(position != RecyclerView.NO_POSITION) {
                    val friend = getItem(position)
                    onEventClickListener?.onEventAccept(friend)
                }
            }

            buttonCancel.setOnClickListener {
                val position = adapterPosition
                if(position != RecyclerView.NO_POSITION) {
                    val friend = getItem(position)
                    onEventClickListener?.onEventCancel(friend)
                }
            }
        }
    }

    class InvitationDiffCallback : DiffUtil.ItemCallback<Friend>() {

        override fun areItemsTheSame(oldItem: Friend, newItem: Friend): Boolean {
            return oldItem.friendEmail == newItem.friendEmail
        }

        override fun areContentsTheSame(oldItem: Friend, newItem: Friend): Boolean {
            return oldItem == newItem
        }


    }

    interface OnEventClickListener{
        fun onEventAccept(friend: Friend)
        fun onEventCancel(friend: Friend)
    }

}