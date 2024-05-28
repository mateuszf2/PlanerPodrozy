package com.example.planerpodrozy

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.example.planerpodrozy.databinding.ActivityAcceptPaymentBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class AcceptPaymentActivity: AppCompatActivity(), PaymentAdapter.OnEventClickListener {
    private lateinit var binding: ActivityAcceptPaymentBinding
    private lateinit var paymentRecyclerView: RecyclerView
    private lateinit var paymentAdapter: PaymentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAcceptPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val eventId= intent.getStringExtra("eventId")
        val db= Firebase.firestore
        val currentUser= FirebaseAuth.getInstance().currentUser
        val userId= currentUser?.uid
        val userEmail= currentUser?.uid

        paymentRecyclerView=binding. recyclerViewPayments
        paymentAdapter= PaymentAdapter()
        paymentRecyclerView.adapter= paymentAdapter
        paymentRecyclerView.layoutManager= LinearLayoutManager(this)

        paymentAdapter.setOnEventClickListener(this)

        if(userId!=null){
            db.collection("paymentsToAccept")
                .whereEqualTo("friendEmail", userEmail)
                .whereEqualTo("eventId", eventId)
                .get()
                .addOnSuccessListener { acceptDocs->
                    val paymentsList= mutableListOf<Payment>()
                    for(acceptDoc in acceptDocs){
                        val paymentUserId= acceptDoc.getString("userId")
                        val paymentAmount= acceptDoc.getString("amountToPay")!!.toDouble()
                        val payment= Payment(eventId!!, paymentAmount, userEmail!!, paymentUserId!!)
                        paymentsList.add(payment)
                        paymentAdapter.submitList(paymentsList)
                    }
                }
                .addOnFailureListener{  e->

                }
        }

        binding.buttonBack.setOnClickListener{
            val intent= Intent(this, FinanseSummaryActivity::class.java)
            intent.putExtra("eventId", eventId)
            startActivity(intent)

        }
    }

    //Co się dzieje po zaakceptowaniu płatności
    override fun onEventAccept(payment: Payment){
        val db= Firebase.firestore
        val currentUser= FirebaseAuth.getInstance().currentUser
        val userId= currentUser?.uid
        val userEmail= currentUser?.email

        Toast.makeText(this, "Potwierdzono płatność", Toast.LENGTH_SHORT)

    }

    //Co się dzieje po odrzuceniu płatności
    override fun onEventCancel(payment: Payment){
        Toast.makeText(this, "Odrzucono płatność", Toast.LENGTH_SHORT)
    }
}