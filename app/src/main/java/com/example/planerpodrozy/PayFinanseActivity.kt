package com.example.planerpodrozy

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.planerpodrozy.databinding.ActivityPayFinanseBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class PayFinanseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPayFinanseBinding

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding= ActivityPayFinanseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val eventId= intent.getStringExtra("eventId")
        val db= Firebase.firestore
        val currentUser= FirebaseAuth.getInstance().currentUser
        val userId= currentUser?.uid
        val finanseId= intent.getStringExtra("finanseId")

        binding.buttonAcceptPay.setOnClickListener{
            if(userId!=null){
                val amountPayNumber= binding.amountPayNumber.text.toString()
                if(amountPayNumber!=""){
                    val payData= hashMapOf(
                        "eventId" to eventId,
                        "userId" to userId,
                        "amountPay" to amountPayNumber,
                        "finanseId" to finanseId
                    )
                    db.collection("finansePay")
                        .add(payData)
                        .addOnSuccessListener { document->

                        }
                        .addOnFailureListener { e->

                        }
                }
            }
        }

        binding.buttonBack.setOnClickListener{
            val intent= Intent(this, FinanseActivity::class.java)
            intent.putExtra("eventId", eventId)
            startActivity(intent)
        }
    }
}