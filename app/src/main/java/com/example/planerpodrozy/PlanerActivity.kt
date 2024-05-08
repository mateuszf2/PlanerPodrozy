package com.example.planerpodrozy

import PlanerAdapter
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.planerpodrozy.databinding.ActivityFinanseBinding
import com.example.planerpodrozy.databinding.ActivityPlanerBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class PlanerActivity : AppCompatActivity() {
    private lateinit var eventId: String
    private  lateinit var binding: ActivityPlanerBinding
    private lateinit var planerRecyclerView: RecyclerView
    private lateinit var planerAdapter: PlanerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlanerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        eventId = intent.getStringExtra("eventId")!! //na pewno nie null
        val db = Firebase.firestore
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid

        planerRecyclerView= binding.recyclerViewPlaner
        planerAdapter= PlanerAdapter()
        planerRecyclerView.adapter= planerAdapter
        planerRecyclerView.layoutManager= LinearLayoutManager(this)

        if(userId!=null){
            db.collection("planer")
                .whereEqualTo("eventId", eventId)
                .get()
                .addOnSuccessListener { documents->
                    val planerList= mutableListOf<Planer>()
                    for(document in documents){
                        val planerName= document.getString("PlanerNazwa")
                        val planerData= document.getString("PlanerData")
                        val planerHour= document.getString("PlanerGodzina")
                        val planer= Planer(planerData!!, planerHour!!, planerName!!)

                        planerList.add(planer)

                    }
                    planerAdapter.submitList(planerList)
                }
                .addOnFailureListener { e->

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

        binding.buttonMenuBar.setOnClickListener{
            menuRecyclerView.visibility= View.VISIBLE
            binding.buttonMenuBar.visibility= View.GONE
        }

        binding.buttonBack.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.buttonAddPlaner.setOnClickListener {
            val intent= Intent(this, AddPlanerActivity::class.java)
            intent.putExtra("eventId", eventId)
            startActivity(intent)
        }

    }



}
