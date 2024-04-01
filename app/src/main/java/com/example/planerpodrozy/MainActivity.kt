package com.example.planerpodrozy

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.planerpodrozy.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding;
    private lateinit var eventRecyclerView: RecyclerView
    private lateinit var eventAdapter: EventAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        eventRecyclerView = binding.recyclerViewEvents
        eventAdapter = EventAdapter()
        eventRecyclerView.adapter = eventAdapter
        eventRecyclerView.layoutManager = LinearLayoutManager(this)

        val currentUser= FirebaseAuth.getInstance().currentUser
        val userId= currentUser?.uid

        //pobieranie z bazy wydarzeń i wyświetlanie kafelków z nazwami wydarzeń
        if(userId!=null){
            val db= Firebase.firestore

            db.collection("wydarzenia")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { documents->
                    val eventsList = mutableListOf<Event>()
                for(document in documents)
                    {
                        val eventId = document.id
                        val nazwaWydarzenia= document.getString("nazwa_wydarzenia")
                        eventsList.add(Event(eventId, nazwaWydarzenia))
                        Log.i("TAG", "Nazwa wydarzenia $nazwaWydarzenia, o id $eventId")
                    }
                    eventAdapter.submitList(eventsList)
                }
                .addOnFailureListener{ e->

                }
        }
        //tworzenie wydarzenia- przycisk
        binding.buttonCreateEvent.setOnClickListener{
            val intent = Intent(this, CreateEventActivity::class.java)
            Log.i("TAG", "PRZYCISK")
            startActivity(intent)
        }
    }

}