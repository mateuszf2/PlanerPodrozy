package com.example.planerpodrozy

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.planerpodrozy.databinding.ActivityFinanseBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.firestore

class FinanseActivity : AppCompatActivity(),FinanseAdapter.OnEventClickListener {
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
        finanseAdapter= FinanseAdapter(this)
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
                        val amountFinanse= document.getDouble("amountFinanse")!!
                        val userIdFinanse= document.getString("userId")
                        val finanse= Finanse(amountFinanse, eventId, finanseId, finanseName.toString(), userIdFinanse.toString())

                        finanseList.add(finanse)

                    }
                    finanseAdapter.submitList(finanseList)
                }
                .addOnFailureListener { e->

                }
        }




        val menuRecyclerView= binding.recyclerViewMenuBar
        val options= arrayOf("Basic information", "Members", "Shared finances", "Daily planner", "Go back" ,"Close")
        val menuAdapter= MenuBarAdapter(options) {selectedOption->
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
                "Basic information" -> {
                    val intent = Intent(this, EventActivity::class.java)
                    intent.putExtra("eventId", eventId)
                    startActivity(intent)
                }
                "Daily planner" -> {
                    if (eventId != null) {
                        val intent = Intent(this, PlanerActivity::class.java)
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
        menuRecyclerView.adapter= menuAdapter
        menuRecyclerView.layoutManager= LinearLayoutManager(this)

        binding.buttonMenuBar.setOnClickListener{
            menuRecyclerView.visibility= View.VISIBLE
            binding.buttonMenuBar.visibility= View.GONE
        }



        binding.buttonAddFinanse.setOnClickListener {
            val intent= Intent(this, AddFinanseActivity::class.java)
            intent.putExtra("eventId", eventId)
            startActivity(intent)
        }

        binding.buttonSummary.setOnClickListener {
            val intent= Intent(this, FinanseSummaryActivity::class.java)
            intent.putExtra("eventId", eventId)
            startActivity(intent)
        }


    }

    override fun onFinanseEdit(amountFinanse:String,eventId : String, finanseName:String,userId:String,finanseId:String){
        val intent= Intent(this, EditFinanseActivity::class.java)
        intent.putExtra("amountFinanse", amountFinanse)
        intent.putExtra("eventId", eventId)
        intent.putExtra("finanseName", finanseName)
        intent.putExtra("userId", userId)
        intent.putExtra("finanseId", finanseId)
        startActivity(intent)
    }
    override fun onFinanseDelete(amountFinanse:String,eventId : String, finanseName:String,userId:String,finanseId:String){
        val intent= Intent(this, DeleteFinanseActivity::class.java)
        intent.putExtra("amountFinanse", amountFinanse)
        intent.putExtra("eventId", eventId)
        intent.putExtra("finanseName", finanseName)
        intent.putExtra("userId", userId)
        intent.putExtra("finanseId", finanseId)
        startActivity(intent)
    }



}