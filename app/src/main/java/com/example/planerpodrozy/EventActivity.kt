package com.example.planerpodrozy

import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
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
import java.io.IOException

class EventActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityEventBinding
    private lateinit var myMap: GoogleMap
    private lateinit var location: String
    private lateinit var eventName: String

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
                        eventName = document.getString("nazwa_wydarzenia").toString()
                        location = document.getString("lokalizacja")!!
                        val date = document.getString("data")

                        val eventNameTextView: TextView = findViewById(R.id.textView_eventName)
                        val locationTextView: TextView = findViewById(R.id.textView_location)
                        val dateTextView: TextView = findViewById(R.id.textView_date)

                        geocodeLocation(location)

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
            intent.putExtra("eventName",eventName)
            startActivity(intent)
        }

        binding.buttonUpdate.setOnClickListener {
            if(eventId!=null){
                eventsCollectionRef.document(eventId)
                    .get()
                    .addOnSuccessListener { eventDocument ->
                        val eventNameTextView: TextView= findViewById(R.id.textView_eventName)
                        val locationTextView: TextView= findViewById(R.id.textView_location)
                        val dateTextView: TextView= findViewById(R.id.textView_date)

                        val newEventName= eventNameTextView.text.toString()
                        val newLocation= locationTextView.text.toString()
                        val newDate= dateTextView.text.toString()

                        val newEventData= hashMapOf<String, Any>(
                            "nazwa_wydarzenia" to newEventName,
                            "lokalizacja" to newLocation,
                            "data" to newDate
                        )
                        eventsCollectionRef.document(eventId).update(newEventData)

                        geocodeLocation(newLocation)
                    }
                    .addOnFailureListener { e->

                    }
            }
        }


    }
    //MAPA GOOGLE- do konfiguracji
    override fun onMapReady(googleMap: GoogleMap) {
        myMap = googleMap
        // Tutaj można dodać konfiguracje mapy


    }

    //Geokoduje i dodaje na mapę znacznik
    private fun geocodeLocation(locationName: String) {
        val geocoder = Geocoder(this)
        try {
            val addresses = geocoder.getFromLocationName(locationName, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val address = addresses[0]
                val latLng = LatLng(address.latitude, address.longitude)
                // Dodanie znacznika na mapę
                myMap.addMarker(MarkerOptions().position(latLng).title(locationName))
                // Przesunięcie kamery na znacznik
                myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
            } else {
                // Adres nie został znaleziony
                Toast.makeText(applicationContext, "Adres nie został znaleziony!", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            // Błąd podczas geokodowania
            Toast.makeText(applicationContext, "Błąd podczas geokodowania!", Toast.LENGTH_SHORT).show()
        }
    }
}
