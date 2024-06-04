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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlanerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        eventId = intent.getStringExtra("eventId")!!
        val db = Firebase.firestore
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
                            planerName,
                            eventId
                        )

                        planerList.add(planer)
                    }


                    val groupedByDay = planerList.groupBy { it.data }
                        .map { PlanerDay(it.key, it.value.sortedBy { activity -> activity.godzina }) }
                        .sortedBy { day -> dateFormat.parse(day.data) }

                    dayAdapter = DayAdapter(groupedByDay) { planer, isChecked ->
                        if (isChecked) {
                            selectedActivities.add(planer)
                        } else {
                            selectedActivities.remove(planer)
                        }
                    }

                    binding.recyclerViewPlaner.adapter = dayAdapter
                    binding.recyclerViewPlaner.layoutManager = LinearLayoutManager(this)
                }
                .addOnFailureListener { e ->
                }
        }

        val menuRecyclerView= binding.recyclerViewMenuBar
        val options= arrayOf("Podstawowe informacje", "Członkowie", "Wspólne finanse", "Planer Dnia", "Zamknij")
        val menuAdapter= MenuBarAdapter(options) {selectedOption->
            if(selectedOption == "Zamknij"){
                menuRecyclerView.visibility= View.GONE
                binding.buttonMenuBar.visibility= View.VISIBLE
            }
            else if(selectedOption == "Podstawowe informacje"){
                val intent= Intent(this, EventActivity::class.java)
                intent.putExtra("eventId", eventId)
                startActivity(intent)
            }
            else if(selectedOption == "Członkowie"){
                val intent= Intent(this, MembersActivity::class.java)
                intent.putExtra("eventId", eventId)
                startActivity(intent)
            }
            else if(selectedOption == "Wspólne finanse"){
                val intent= Intent(this, FinanseActivity::class.java)
                intent.putExtra("eventId", eventId)
                startActivity(intent)
            }
        }
        menuRecyclerView.adapter= menuAdapter
        menuRecyclerView.layoutManager= LinearLayoutManager(this)

        binding.buttonMenuBar.setOnClickListener {
            binding.recyclerViewMenuBar.visibility = View.VISIBLE
            binding.buttonMenuBar.visibility = View.GONE
        }

        binding.buttonBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.buttonAddPlaner.setOnClickListener {
            val intent = Intent(this, AddPlanerActivity::class.java)
            intent.putExtra("eventId", eventId)
            startActivity(intent)
        }

        binding.buttonDelete.setOnClickListener {
            deleteSelectedActivities()
        }
    }
    companion object {
        private const val TAG = "PlanerActivity"
    }
    private fun deleteSelectedActivities() {
        if (selectedActivities.isEmpty()) {
            Toast.makeText(this, "Nie wybrano żadnych wydarzeń do usunięcia.", Toast.LENGTH_SHORT).show()
            return
        }

        val db = Firebase.firestore
        val batch = db.batch()

        selectedActivities.forEach { planer ->
            val query = db.collection("planer")
                .whereEqualTo("eventId", planer.eventId)
                .whereEqualTo("PlanerNazwa", planer.nazwaAktywnosci)
                .whereEqualTo("PlanerData", planer.data)
                .whereEqualTo("PlanerGodzina", planer.godzina)

            query.get().addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d(TAG, "Dokument do usunięcia: ${document.id}")
                    batch.delete(document.reference)
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(this, "Wystąpił błąd podczas pobierania dokumentów do usunięcia: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }

        batch.commit().addOnSuccessListener {
            Toast.makeText(this, "Wybrane wydarzenia zostały usunięte.", Toast.LENGTH_SHORT).show()
            recreate()
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Wystąpił błąd podczas usuwania wydarzeń: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }

}
