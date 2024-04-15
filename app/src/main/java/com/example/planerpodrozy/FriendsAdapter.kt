package com.example.planerpodrozy
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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

class FriendsAdapter : ListAdapter<Friend, FriendsAdapter.FriendViewHolder>(FriendDiffCallback()) {

    private var onEventClickListener: OnEventClickListener? = null

    private val db= Firebase.firestore


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
        private val profilePhotoImageView: ImageView = itemView.findViewById(R.id.iv_profile_photo)



        fun bind(friend: Friend) {
            val currentUser= FirebaseAuth.getInstance().currentUser
            db.collection("zdjecieProfilowe")
                .where(
                    Filter.or(
                        Filter.equalTo("userEmail",friend.userEmail),
                        Filter.equalTo("userEmail",friend.friendEmail)))
                .addSnapshotListener { value, error ->
                    if (value != null) {
                        for (document in value){
                            if (document.getString("userEmail")!=currentUser?.email){
                                Picasso.get().load(document.getString("zdjecieLink")).error(R.drawable.user).into(profilePhotoImageView)
                            }
                        }
                    }
                }
                if (friend.userEmail==currentUser?.email){
                    friendNameTextView.text = friend.friendEmail
                }
                else {
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
