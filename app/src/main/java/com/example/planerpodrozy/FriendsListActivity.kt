package com.example.planerpodrozy

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.planerpodrozy.databinding.ActivityFriendsListBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class FriendsListActivity: AppCompatActivity(),FriendsAdapter.OnEventClickListener {
    private lateinit var binding: ActivityFriendsListBinding
    private lateinit var FriendRecyclerView: RecyclerView
    private lateinit var FriendAdapter: FriendsAdapter
    private lateinit var context: Context


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendsListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        context = this

        FriendRecyclerView = binding.recyclerViewFriends
        FriendAdapter = FriendsAdapter()
        FriendRecyclerView.adapter = FriendAdapter
        FriendRecyclerView.layoutManager = LinearLayoutManager(this)

        FriendAdapter.setOnEventClickListener(this)

        val currentUser= FirebaseAuth.getInstance().currentUser
        val userId= currentUser?.uid

        fun generateEventList(){
            if(userId!=null){
                val db= Firebase.firestore

                val email = currentUser.email
                val friendsList = mutableListOf<Friend>()
                db.collection("znajomi")
                    .where(Filter.or(Filter.equalTo("userId",userId),
                                     Filter.equalTo("friendEmail",email)))
                    .get()
                    .addOnSuccessListener { documents->
                        for (document in documents){
                            val friend = document.getString("userId")?.let {
                                Friend(
                                    it,
                                    document.get("userEmail").toString(),
                                    document.get("friendEmail").toString())
                            }
                            if (friend != null) {
                                friendsList.add(friend)
                            }
                        }
                        FriendAdapter.submitList(friendsList)
                        FriendAdapter.notifyDataSetChanged()

                    }
                    .addOnFailureListener{ e->
                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
        generateEventList()



        binding.buttonBackFriends.setOnClickListener{
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
        binding.buttonFriendsInvitations.setOnClickListener{
            val intent = Intent(this, FriendsInvitationsActivity::class.java)
            startActivity(intent)
        }
        binding.buttonAddFriend.setOnClickListener{
            val intent = Intent(this, CreateFriendActivity::class.java)
            startActivity(intent)
        }
    }

    //tu bedzie przekierowanie do czatu
    override fun onEventClick(friend: Friend) {
        val intent = Intent(this, EventActivity::class.java)
        //metoda putExtra przekazuje dane między komponentami aplikacji, dodaje dodatkowe informacje do obiektu "Intent"
        intent.putExtra("friendEmail", friend.friendEmail)
        startActivity(intent)
    }
}

