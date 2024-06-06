package com.example.planerpodrozy

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
    private lateinit var eventId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAcceptPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        eventId= intent.getStringExtra("eventId")!!
        val db= Firebase.firestore
        val currentUser= FirebaseAuth.getInstance().currentUser
        val userId= currentUser?.uid
        val userEmail= currentUser?.email

        paymentRecyclerView=binding. recyclerViewPayments
        paymentAdapter= PaymentAdapter()
        paymentRecyclerView.adapter= paymentAdapter
        paymentRecyclerView.layoutManager= LinearLayoutManager(this)

        paymentAdapter.setOnEventClickListener(this)

        if(userId!=null && userEmail!=null){
            db.collection("paymentsToAccept")
                .whereEqualTo("eventId", eventId)
                .whereEqualTo("friendEmail", userEmail)
                .get()
                .addOnSuccessListener { acceptDocs->
                    Log.i("LOG", "${userEmail}, ${eventId}")
                    val paymentsList= mutableListOf<Payment>()
                    for(acceptDoc in acceptDocs){
                        if(!acceptDocs.isEmpty){
                            Log.i("TAG","KURWA TU COŚ JEST")
                        }
                        Log.i("TAG", "ASDAAS")
                        val paymentUserId= acceptDoc.getString("userId")
                        val paymentAmount= acceptDoc.getString("amountToPay")!!.toDouble()
                        val payment= Payment(eventId, paymentAmount, userEmail!!, paymentUserId!!)

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

        val amountToPay= payment.amountPayment
        val userIdDoc= payment.userId
        db.collection("bilans").document(eventId).collection("bilansPairs")
            .whereEqualTo("userId", userId)
            .whereEqualTo("friendId", userIdDoc)
            .get()
            .addOnSuccessListener { bilansDocs->
                for(bilansDoc in bilansDocs){
                    var totalBilans= bilansDoc.getDouble("totalBilans")!!
                    totalBilans-=amountToPay

                    db.collection("bilans").document(eventId).collection("bilansPairs").document(bilansDoc.id).update("totalBilans", totalBilans)

                    db.collection("bilans").document(eventId).collection("bilansPairs")
                        .whereEqualTo("userId", userIdDoc)
                        .whereEqualTo("friendId", userId)
                        .get()
                        .addOnSuccessListener { bilansDocs2->
                            for(bilansDoc2 in bilansDocs2){
                                var totalBilans= bilansDoc2.getDouble("totalBilans")!!
                                totalBilans+=amountToPay

                                db.collection("bilans").document(eventId).collection("bilansPairs").document(bilansDoc2.id).update("totalBilans", totalBilans)

                                db.collection("paymentsToAccept")
                                    .whereEqualTo("eventId", eventId)
                                    .whereEqualTo("friendEmail", payment.friendEmail)
                                    .whereEqualTo("userId", payment.userId)
                                    .get()
                                    .addOnSuccessListener { deleteDocs->
                                        for(deleteDoc in deleteDocs){
                                            db.collection("paymentsToAccept").document(deleteDoc.id).delete()
                                        }
                                    }
                            }
                        }

                }
            }
            .addOnFailureListener { e->

            }


        Toast.makeText(this, "Potwierdzono płatność", Toast.LENGTH_SHORT)

    }

    //Co się dzieje po odrzuceniu płatności
    override fun onEventCancel(payment: Payment){
        val db= Firebase.firestore
        val currentUser= FirebaseAuth.getInstance().currentUser
        val userId= currentUser?.uid

        db.collection("paymentsToAccept")
            .whereEqualTo("eventId", eventId)
            .whereEqualTo("friendEmail", payment.friendEmail)
            .whereEqualTo("userId", payment.userId)
            .get()
            .addOnSuccessListener { deleteDocs->
                for(deleteDoc in deleteDocs){
                    db.collection("paymentsToAccept").document(deleteDoc.id).delete()
                }
            }
        Toast.makeText(this, "Odrzucono płatność", Toast.LENGTH_SHORT)
    }
}