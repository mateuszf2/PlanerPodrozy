package com.example.planerpodrozy

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.planerpodrozy.databinding.ActivityMainBinding
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import org.w3c.dom.Document

class MainActivity : AppCompatActivity(), EventAdapter.OnEventClickListener {
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

        eventAdapter.setOnEventClickListener(this)

        val currentUser= FirebaseAuth.getInstance().currentUser
        val userId= currentUser?.uid

        //funkcja pobierająca z bazy wydarzenia i wyświetlająca kafelki z nazwami wydarzeń(generująca listę wydarzeń)
        fun generateEventList(){
            if(userId!=null){
                val db= Firebase.firestore

                db.collection("wydarzeniaUzytkownicy")
                    .whereEqualTo("userId", userId)
                    .get()
                    .addOnSuccessListener { documents->
                        val eventsList = mutableListOf<Event>()
                        val tasks = mutableListOf<Task<DocumentSnapshot>>()

                        for(document in documents)
                        {
                            val eventId = document.getString("eventId") // Pobieramy identyfikator wydarzenia z dokumentu "wydarzeniaUzytkownicy"
                            if(eventId != null) {
                                val task = db.collection("wydarzenia").document(eventId).get()
                                tasks.add(task)
                            }
                        }
                        //Czeka aż wszystkie taski się zakończą
                        Tasks.whenAllSuccess<DocumentSnapshot>(tasks)
                            .addOnSuccessListener { snapshots ->
                                for(snapshot in snapshots) {
                                    val nazwaWydarzenia = snapshot.getString("nazwa_wydarzenia")
                                    val currentEvent = Event(snapshot.id, nazwaWydarzenia)
                                    eventsList.add(currentEvent)
                                }
                                eventAdapter.submitList(eventsList)
                            }
                            .addOnFailureListener { e ->

                            }
                    }
                    .addOnFailureListener{ e->

                    }
            }
        }
        generateEventList()

        //tworzenie wydarzenia- przycisk
        binding.buttonCreateEvent.setOnClickListener{
            val intent = Intent(this, CreateEventActivity::class.java)
            Log.i("TAG", "PRZYCISK")
            startActivity(intent)
        }
        //profil- przycisk
        binding.ibViewProfile.setOnClickListener{
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
        //zaproszenia- przycisk
        binding.buttonInvitations.setOnClickListener{
            val intent = Intent(this, InvitationsActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onEventClick(event: Event) {
        val intent = Intent(this, EventActivity::class.java)
        //metoda putExtra przekazuje dane między komponentami aplikacji, dodaje dodatkowe informacje do obiektu "Intent"
        intent.putExtra("eventId", event.eventId)
        startActivity(intent)
    }

}