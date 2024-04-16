package com.example.planerpodrozy

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.planerpodrozy.databinding.ActivityMembersBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class MembersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMembersBinding
    private lateinit var friendRecyclerView: RecyclerView //friend=member, korzystam ze stworzonego już friend adaptera bo pasuje również na listę członków wydarzenia
    private lateinit var friendAdapter: FriendsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMembersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        friendRecyclerView= binding.recyclerViewMembers
        friendAdapter= FriendsAdapter()
        friendRecyclerView.adapter= friendAdapter
        friendRecyclerView.layoutManager= LinearLayoutManager(this)

        val eventId= intent.getStringExtra("eventId")
        val db= Firebase.firestore
        val currentUser= FirebaseAuth.getInstance().currentUser
        val userId= currentUser?.uid


        if(userId!=null) {
            val membersList= mutableListOf<Friend>()
            db.collection("wydarzeniaUzytkownicy")
                .whereEqualTo("eventId", eventId)
                .get()
                .addOnSuccessListener { documents ->
                    for(document in documents){
                        val friend = document.getString("userId")?.let {
                            Friend(
                                it,
                                "rafal.skolimowski127@gmail.com",
                                "Not important"
                            )
                        }
                        if(friend!=null) {
                            membersList.add(friend)
                        }
                    }
                    friendAdapter.submitList(membersList)
                    friendAdapter.notifyDataSetChanged()
                }
                .addOnFailureListener { e -> }
        }

        val recyclerView: RecyclerView= findViewById(R.id.recycler_view_MenuBar)
        val options= arrayOf("Podstawowe informacje", "Członkowie", "Wspólne finanse", "Kalendarz", "Zamknij")
        val adapter= MenuBarAdapter(options) { selectedOption ->
            if(selectedOption == "Zamknij") {
                recyclerView.visibility = View.GONE
                binding.buttonMenuBar.visibility= View.VISIBLE
            }
            else if(selectedOption == "Podstawowe informacje") {
                val intent= Intent(this, EventActivity::class.java)
                intent.putExtra("eventId", eventId)
                startActivity(intent)
            }
        }
        recyclerView.adapter= adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        binding.buttonMenuBar.setOnClickListener{
            recyclerView.visibility= View.VISIBLE
            binding.buttonMenuBar.visibility= View.GONE
        }
    }
}