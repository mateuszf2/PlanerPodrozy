package com.example.planerpodrozy

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.planerpodrozy.databinding.ActivityFinanseBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class FinanseActivity : AppCompatActivity() {
    lateinit var binding: ActivityFinanseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityFinanseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val eventId= intent.getStringExtra("eventId")
        val db= Firebase.firestore

        val menuRecyclerView= binding.recyclerViewMenuBar
        val options= arrayOf("Podstawowe informacje", "Członkowie", "Wspólne finanse", "Kalendarz", "Zamknij")
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
    }
}