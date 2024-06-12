package com.example.planerpodrozy

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.planerpodrozy.databinding.ActivityPlanerBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Locale

class PlanerActivity : AppCompatActivity() {
    private lateinit var eventId: String
    private lateinit var binding: ActivityPlanerBinding
    private lateinit var dayAdapter: DayAdapter
    private val selectedActivities = mutableSetOf<Planer>()
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlanerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        eventId = intent.getStringExtra("eventId")!!
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid

        if (userId != null) {
            db.collection("planer")
                .whereEqualTo("eventId", eventId)
                .get()
                .addOnSuccessListener { documents ->
                    val planerList = mutableListOf<Planer>()
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

                    for (document in documents) {
                        val planerName = document.getString("PlanerNazwa") ?: ""
                        val planerData = document.getString("PlanerData") ?: ""
                        val planerHour = document.getString("PlanerGodzina") ?: ""

                        val date = dateFormat.parse(planerData)
                        val time = timeFormat.parse(planerHour)

                        val planer = Planer(
                            dateFormat.format(date),
                            timeFormat.format(time),
                            planerName
                        )

                        planerList.add(planer)
                    }

                    val groupedByDay = planerList.groupBy { it.data }
                        .map { PlanerDay(it.key, it.value.sortedBy { activity -> activity.godzina }) }
                        .sortedBy { day -> dateFormat.parse(day.data) }

                    dayAdapter = DayAdapter(groupedByDay, { planer, isChecked ->
                        if (isChecked) {
                            selectedActivities.add(planer)
                        } else {
                            selectedActivities.remove(planer)
                        }
                    }, { planer -> // Dodano ten blok
                        deleteActivity(planer)
                    })

                    binding.recyclerViewPlaner.adapter = dayAdapter
                    binding.recyclerViewPlaner.layoutManager = LinearLayoutManager(this)
                }
                .addOnFailureListener { e ->
                    // Obsłuż błąd
                }
        }

        val menuRecyclerView = binding.recyclerViewMenuBar
        val options = arrayOf("Basic information", "Members", "Shared finances", "Daily planner", "Go back" ,"Close")
        val menuAdapter = MenuBarAdapter(options) { selectedOption ->
            when (selectedOption) {
                "Close" -> {
                    menuRecyclerView.visibility = View.GONE
                    binding.buttonMenuBar.visibility = View.VISIBLE
                }
                "Members" -> {
                    val intent = Intent(this, MembersActivity::class.java)
                    intent.putExtra("eventId", eventId)
                    startActivity(intent)
                }
                "Shared finances" -> {
                    val intent = Intent(this, FinanseActivity::class.java)
                    intent.putExtra("eventId", eventId)
                    startActivity(intent)
                }
                "Basic information" -> {
                    if (eventId != null) {
                        val intent = Intent(this, EventActivity::class.java)
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
        menuRecyclerView.adapter = menuAdapter
        menuRecyclerView.layoutManager = LinearLayoutManager(this)

        binding.buttonMenuBar.setOnClickListener {
            binding.recyclerViewMenuBar.visibility = View.VISIBLE
            binding.buttonMenuBar.visibility = View.GONE
        }



        binding.buttonAddPlaner.setOnClickListener {
            val intent = Intent(this, AddPlanerActivity::class.java)
            intent.putExtra("eventId", eventId)
            startActivity(intent)
        }
    }

    private fun deleteActivity(planer: Planer) {
        db.collection("planer")
            .whereEqualTo("PlanerNazwa", planer.nazwaAktywnosci)
            .whereEqualTo("PlanerData", planer.data)
            .whereEqualTo("PlanerGodzina", planer.godzina)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    db.collection("planer").document(document.id).delete()
                        .addOnSuccessListener {
                            Toast.makeText(this, "Wydarzenie usunięte pomyślnie", Toast.LENGTH_SHORT).show()
                            // Odśwież listę aktywności po usunięciu
                            recreate()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Nie udało się usunąć wydarzenia", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Nie udało się usunąć wydarzenia", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        private const val TAG = "PlanerActivity"
    }
}
