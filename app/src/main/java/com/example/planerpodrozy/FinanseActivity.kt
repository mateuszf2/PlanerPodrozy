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

        var sumAll: Double = 0.0
        var sumUser: Double = 0.0

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
                        sumAll+= amountFinanse //sumuje wszystkie składki dla tego wydarzenia
                        binding.textViewSumAll.text= "Suma składek\n${sumAll} zł" //wyświetla sumę wszystkich składek tego wydarzenia
                    }
                    finanseAdapter.submitList(finanseList)
                }
                .addOnFailureListener { e->

                }
        }

        //sumuje wszystkie wpłaty użytkownika
        if(userId!=null){
            db.collection("finansePay")
                .whereEqualTo("eventId", eventId)
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { documents ->
                    for(document in documents){
                        val currentAmount= document.getString("amountPay")!!.toDouble()
                        sumUser+=currentAmount
                        binding.textViewSumUser.text= "Suma wpłat\n${sumUser.toString()}" //wyświetlanie na sumy wszystki wpłat użytkownika
                    }
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
            else if(selectedOption == "Planer Dnia"){
                val intent= Intent(this, PlanerActivity::class.java)
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
            val intent = Intent(this, EventActivity::class.java)
            intent.putExtra("eventId", eventId)
            startActivity(intent)
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