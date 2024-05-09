package com.example.planerpodrozy

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.planerpodrozy.databinding.ActivityEventBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EventActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityEventBinding
    private lateinit var myMap: GoogleMap
    private lateinit var location: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = FirebaseFirestore.getInstance()
        val eventsCollectionRef = db.collection("wydarzenia")
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid
        val eventId = intent.getStringExtra("eventId")

        binding = ActivityEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //MAPA GOOGLE
        val mapFragment= supportFragmentManager.findFragmentById(R.id.id_map) as SupportMapFragment
        mapFragment.getMapAsync(this) // Tutaj this odnosi się do klasy MainActivity, która implementuje OnMapReadyCallback

        binding.buttonBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.buttonAddFriend.setOnClickListener {
            val intent = Intent(this, InviteToEventActivity::class.java)
            intent.putExtra("eventId", eventId)
            startActivity(intent)
        }

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view_MenuBar)
        val options = arrayOf("Podstawowe informacje", "Członkowie", "Wspólne finanse", "Planer Dnia", "Zamknij")
        val adapter = MenuBarAdapter(options) { selectedOption ->
            when (selectedOption) {
                "Zamknij" -> {
                    recyclerView.visibility = View.GONE
                    binding.buttonMenuBar.visibility = View.VISIBLE
                }
                "Członkowie" -> {
                    val intent = Intent(this, MembersActivity::class.java)
                    intent.putExtra("eventId", eventId)
                    startActivity(intent)
                }
                "Wspólne finanse" -> {
                    val intent = Intent(this, FinanseActivity::class.java)
                    intent.putExtra("eventId", eventId)
                    startActivity(intent)
                }
                "Planer Dnia" -> {
                    if (eventId != null) {
                        val intent = Intent(this, PlanerActivity::class.java)
                        intent.putExtra("eventId", eventId)
                        startActivity(intent)
                    } else {
                        Log.e("EventActivity", "eventId is null, cannot start PlanerActivity")
                    }
                }
            }
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        binding.buttonMenuBar.setOnClickListener {
            recyclerView.visibility = View.VISIBLE
            binding.buttonMenuBar.visibility = View.GONE
        }

        if (eventId != null) {
            eventsCollectionRef.document(eventId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val eventName = document.getString("nazwa_wydarzenia")
                        location = document.getString("lokalizacja")!!
                        val date = document.getString("data")

                        val eventNameTextView: TextView = findViewById(R.id.textView_eventName)
                        val locationTextView: TextView = findViewById(R.id.textView_location)
                        val dateTextView: TextView = findViewById(R.id.textView_date)

                        eventNameTextView.text = eventName
                        locationTextView.text = location
                        dateTextView.text = date
                    } else {
                        Log.e("EventActivity", "Document for eventId: $eventId does not exist")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("EventActivity", "Error getting document for eventId: $eventId", exception)
                }
        }

        binding.btGroupChat.setOnClickListener{
            val intent = Intent(this, GroupMessagingActivity::class.java)
            intent.putExtra("eventId",eventId)
            startActivity(intent)
        }


    }
    //MAPA GOOGLE
    override fun onMapReady(googleMap: GoogleMap) {
        myMap = googleMap
        // Tutaj można dodać konfiguracje mapy

        val warsawLatLng = LatLng(52.2297, 21.0122)

        // znacznik na mapę dla Warszawy
        myMap.addMarker(MarkerOptions().position(warsawLatLng).title("Warszawa"))

        // przesunięcie kamery na znacznik
        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(warsawLatLng, 10f))
    }
}
