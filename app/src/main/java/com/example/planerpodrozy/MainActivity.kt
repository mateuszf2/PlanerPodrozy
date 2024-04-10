package com.example.planerpodrozy

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.planerpodrozy.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity(), EventAdapter.OnEventClickListener {
    private lateinit var binding: ActivityMainBinding;
    private lateinit var eventRecyclerView: RecyclerView
    private lateinit var eventAdapter: EventAdapter
    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        context = this

        eventRecyclerView = binding.recyclerViewEvents
        eventAdapter = EventAdapter(::deleteEvent)
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
                        val list = mutableListOf<String>()
                        for(document in documents)
                        {
                            val eventId = document.getString("eventId") // Pobieramy identyfikator wydarzenia z dokumentu "wydarzeniaUzytkownicy"
                            if(eventId != null) {
                                list.add(eventId)
                            }
                        }
                        for(eventIDD in list){
                            db.collection("wydarzenia")
                                .document(eventIDD)
                                .get()
                                .addOnSuccessListener { eventDocument ->
                                    val nazwaWydarzenia= eventDocument.getString("nazwa_wydarzenia")
                                    if (!nazwaWydarzenia.isNullOrBlank()) {
                                        val eventId = eventIDD
                                        val evento = Event(eventId, nazwaWydarzenia)
                                        Log.i("TAG", "$evento")
                                        eventsList.add(evento)
                                    }
                                    Log.i("TAG", "Nazwa wydarzenia $nazwaWydarzenia, o id $eventIDD")
                                    Log.i("TAG", eventsList.toString())
                                    eventAdapter.submitList(eventsList)
                                    eventAdapter.notifyDataSetChanged()

                                }
                                .addOnFailureListener{ e-> }
                        }

                    }
                    .addOnFailureListener{ e->
                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
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

    private fun deleteEvent(event: Event) {
        val db = FirebaseFirestore.getInstance()
        db.collection("wydarzenia")
            .document(event.eventId)
            .delete()
            .addOnSuccessListener {
                db.collection("wydarzeniaUzytkownicy")
                    .whereEqualTo("eventId", event.eventId)
                    .get()
                    .addOnSuccessListener{documents->
                        for(documentUsersEvents in documents)
                        {
                            db.collection("wydarzeniaUzytkownicy").document(documentUsersEvents.id).delete()
                        }
                    }
                val newList = eventAdapter.currentList.toMutableList()
                newList.remove(event)
                eventAdapter.submitList(newList)

                eventAdapter.notifyDataSetChanged()
                Toast.makeText(context, "Event deleted successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                // Użycie kontekstu do wywołania Toast.makeText()
                Toast.makeText(context, "Error deleting event: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

}