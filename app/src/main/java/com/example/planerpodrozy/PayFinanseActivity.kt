package com.example.planerpodrozy

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.example.planerpodrozy.databinding.ActivityPayFinanseBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class PayFinanseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPayFinanseBinding
    private lateinit var paymentRecyclerView: RecyclerView
    private lateinit var paymentAdapter: PaymentAdapter

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding= ActivityPayFinanseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val eventId= intent.getStringExtra("eventId")
        val db= Firebase.firestore
        val currentUser= FirebaseAuth.getInstance().currentUser
        val userId= currentUser?.uid
        val finanseId= intent.getStringExtra("finanseId")

        paymentRecyclerView= binding.recyclerViewPayments
        paymentAdapter= PaymentAdapter()
        paymentRecyclerView.adapter= paymentAdapter
        paymentRecyclerView.layoutManager= LinearLayoutManager(this)

        if(userId!=null){
            db.collection("finansePay")
                .whereEqualTo("finanseId", finanseId)
                .get()
                .addOnSuccessListener { documents->
                    val paymentList= mutableListOf<Payment>()
                    for(document in documents){
                        val paymentUserId= document.getString("userId")!!
                        val amountPayment= document.getString("amountPay")!!.toDouble()
                        db.collection("idEmail")
                            .whereEqualTo("userId", paymentUserId)
                            .get()
                            .addOnSuccessListener { emailDocument->
                                for(ed in emailDocument){
                                    val paymentEmail= ed.getString("userEmail")!!
                                    val payment= Payment(paymentEmail, amountPayment, "tenplikdowywalenia", "boStary")
                                    paymentList.add(payment)
                                    paymentAdapter.submitList(paymentList)
                                }
                            }
                            .addOnFailureListener { e->

                            }
                    }
                }
                .addOnFailureListener { e->

                }
        }


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