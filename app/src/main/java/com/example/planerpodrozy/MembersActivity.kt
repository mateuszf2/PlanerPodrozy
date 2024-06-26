package com.example.planerpodrozy

import android.content.Intent
import android.os.Bundle
import android.util.Log
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

        //generwoanie listy członków wydarzenia
        if(userId!=null) {
            val membersList= mutableListOf<Friend>()

            db.collection("wydarzeniaUzytkownicy")
                .whereEqualTo("eventId", eventId)
                .get()
                .addOnSuccessListener { documents ->
                    val list= mutableListOf<String>()
                    for(documentEvent in documents){
                        val currentUserId= documentEvent.getString("userId")
                        if(currentUserId!=null){
                            list.add(currentUserId)
                        }
                    }
                    for(currentUser in list){
                        db.collection("idEmail")
                            .whereEqualTo("userId", currentUser)
                            .limit(1) //pobierze tylko jeden dokument, bo jest tylko jeden uzytkownik o konkretnym id, ale w firestore ".whereequalto" zwraca listę dokumentów
                            .get()
                            .addOnSuccessListener { documentsEmail ->
                                for(documentEmail in documentsEmail)
                                {
                                    val friend = documentEmail.getString("userId")?.let {
                                        Friend(
                                            it,
                                            documentEmail.getString("userEmail")!!,
                                            documentEmail.getString("userEmail")!!
                                        )
                                    }
                                    if(friend!=null) {
                                        membersList.add(friend)
                                    }
                                    friendAdapter.submitList(membersList)
                                    friendAdapter.notifyDataSetChanged()
                                }
                            }
                            .addOnFailureListener { e -> }
                    }
                }
                .addOnFailureListener { e -> }
        }

        val recyclerView: RecyclerView= findViewById(R.id.recycler_view_MenuBar)
        val options= arrayOf("Basic information", "Members", "Shared finances", "Daily planner", "Go back" ,"Close")
        val menuAdapter= MenuBarAdapter(options) { selectedOption ->
            when (selectedOption) {
                "Close" -> {
                    recyclerView.visibility = View.GONE
                    binding.buttonMenuBar.visibility = View.VISIBLE
                }
                "Basic information" -> {
                    val intent = Intent(this, EventActivity::class.java)
                    intent.putExtra("eventId", eventId)
                    startActivity(intent)
                }
                "Shared finances" -> {
                    val intent = Intent(this, FinanseActivity::class.java)
                    intent.putExtra("eventId", eventId)
                    startActivity(intent)
                }
                "Daily planner" -> {
                    if (eventId != null) {
                        val intent = Intent(this, PlanerActivity::class.java)
                        intent.putExtra("eventId", eventId)
                        startActivity(intent)
                    } else {
                        Log.e("EventActivity", "eventId is null, cannot start PlanerActivity")
                    }
                }
                "Go back" ->{
                    if(eventId !=null){
                        val intent= Intent(this, MainActivity::class.java)
                        intent.putExtra("eventId", eventId)
                        startActivity(intent)
                    }
                    else{
                        Log.e("EventActivity", "eventId is null, cannot start MainActivity")
                    }
                }
            }
        }
        recyclerView.adapter= menuAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        binding.buttonMenuBar.setOnClickListener{
            recyclerView.visibility= View.VISIBLE
            binding.buttonMenuBar.visibility= View.GONE
        }

    }
}