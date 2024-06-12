package com.example.planerpodrozy

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.planerpodrozy.databinding.ActivityPayFriendBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class PayFriendActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPayFriendBinding

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding= ActivityPayFriendBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db= Firebase.firestore
        val currentUser= FirebaseAuth.getInstance().currentUser
        val userId= currentUser?.uid
        val eventId= intent.getStringExtra("eventId")

        binding.buttonPay.setOnClickListener {
            if(userId!=null){
                val emailToPay= binding.editTextEmailToPay.text.toString()
                val amountToPay= binding.editTextNumberToPay.text.toString()
                if(emailToPay.isNotEmpty() && amountToPay.isNotEmpty()){
                    val paymentData= hashMapOf( //to będzie zapisywać w bazie jako oczekujące do akceptacji płatności
                        "eventId" to eventId,
                        "friendEmail" to emailToPay,
                        "amountToPay" to amountToPay,
                        "userId" to userId
                    )
                    db.collection("paymentsToAccept")
                        .add(paymentData)
                        .addOnSuccessListener { document->
                            showToast("Friend added successfully!")
                        }
                        .addOnFailureListener { e->

                        }
                }
            }
        }

        binding.buttonBack.setOnClickListener {
            val intent= Intent(this, FinanseActivity::class.java)
            intent.putExtra("eventId", eventId)
            startActivity(intent)
        }
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}