package com.example.planerpodrozy

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.planerpodrozy.databinding.ActivityFinanseBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class FinanseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFinanseBinding
    private lateinit var finanseRecyclerView: RecyclerView
    private lateinit var finanseAdapter: FinanseAdapter
    private lateinit var eventId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityFinanseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        eventId= intent.getStringExtra("eventId")!! //na pewno nie null
        val db= Firebase.firestore
        val currentUser= FirebaseAuth.getInstance().currentUser
        val userId= currentUser?.uid


        finanseRecyclerView= binding.recyclerViewFinanse
        finanseAdapter= FinanseAdapter(::payFinanse)
        finanseRecyclerView.adapter= finanseAdapter
        finanseRecyclerView.layoutManager= LinearLayoutManager(this)

        if(userId!=null){
            db.collection("finanse")
                .whereEqualTo("eventId", eventId)
                .get()
                .addOnSuccessListener { documents->
                    val finanseList= mutableListOf<Finanse>()
                    for(document in documents){
                        val finanseId= document.id
                        val finanseName= document.getString("finanseName")
                        val amountFinanse= document.getString("amountFinanse")
                        val finanse= Finanse(amountFinanse!!.toDouble(), eventId, finanseId, finanseName.toString())

                        finanseList.add(finanse)
                    }
                    finanseAdapter.submitList(finanseList)
                }
                .addOnFailureListener { e->

                }
        }

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

        binding.buttonAddFinanse.setOnClickListener {
            val intent= Intent(this, AddFinanseActivity::class.java)
            intent.putExtra("eventId", eventId)
            startActivity(intent)
        }

    }

    private fun payFinanse(finanse: Finanse){
        val intent= Intent(this, PayFinanseActivity::class.java)
        intent.putExtra("finanseId", finanse.finanseId)
        intent.putExtra("eventId", eventId)
        startActivity(intent)
    }
}