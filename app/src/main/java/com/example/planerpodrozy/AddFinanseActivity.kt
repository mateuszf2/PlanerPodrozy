package com.example.planerpodrozy

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.planerpodrozy.databinding.ActivityAddFinanseBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class AddFinanseActivity: AppCompatActivity() {
    lateinit var binding: ActivityAddFinanseBinding

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding= ActivityAddFinanseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val eventId= intent.getStringExtra("eventId")
        val db= Firebase.firestore
        val currentUser= FirebaseAuth.getInstance().currentUser
        val userId= currentUser?.uid

        binding.buttonBack.setOnClickListener{
            intent= Intent(this, FinanseActivity::class.java)
            intent.putExtra("eventId", eventId)
            startActivity(intent)
        }

        binding.buttonAddBudget.setOnClickListener {
            if(eventId!=null && userId!=null){
                val finanseName= binding.finanseNameText.text.toString()
                val amountFinanse= binding.amountNumber.text.toString()
                if(finanseName!= "" && amountFinanse!= ""){
                    val finanseData= hashMapOf(
                        "eventId" to eventId,
                        "finanseName" to finanseName,
                        "amountFinanse" to amountFinanse
                    )
                    db.collection("finanse")
                        .add(finanseData)
                        .addOnSuccessListener { document ->

                        }
                        .addOnFailureListener { e ->

                        }
                }
            }
        }
    }
}