package com.example.planerpodrozy

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.planerpodrozy.databinding.ActivityFinanseBinding
import com.example.planerpodrozy.databinding.ActivityFinanseSummaryBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import java.text.DecimalFormat

class FinanseSummaryActivity :AppCompatActivity() {
    private lateinit var binding: ActivityFinanseSummaryBinding
    private lateinit var finanseSummaryRecyclerView: RecyclerView
    private lateinit var finanseSummaryAdapter: FinanseSummaryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityFinanseSummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val db= Firebase.firestore
        val currentUser= FirebaseAuth.getInstance().currentUser
        val userId= currentUser?.uid
        val eventId= intent.getStringExtra("eventId")


        finanseSummaryRecyclerView= binding.recyclerViewFinanse
        finanseSummaryAdapter= FinanseSummaryAdapter()
        finanseSummaryRecyclerView.adapter= finanseSummaryAdapter
        finanseSummaryRecyclerView.layoutManager= LinearLayoutManager(this)


        fun fetchFriends(){
            val friendsList = mutableListOf<Bilans>()
            if(eventId!=null){
                db.collection("bilans").document(eventId).collection("bilansPairs")
                    .where(Filter.equalTo("friendId",userId))
                    .get()
                    .addOnSuccessListener(){ documents->
                        if(documents!=null) {
                            for (document in documents) {
                                val friend =Bilans(
                                    document.get("friendId").toString(),
                                    String.format("%.10f",document.get("totalBilans")),
                                    document.get("userId").toString()
                                )
                                if (friend != null) {
                                    friendsList.add(friend)
                                }
                            }

                        }
                        finanseSummaryAdapter.submitList(friendsList)
                    }
            }
        }

        fetchFriends()


        binding.buttonBack.setOnClickListener{
            val intent = Intent(this, FinanseActivity::class.java)
            intent.putExtra("eventId", eventId)
            startActivity(intent)
        }

        binding.buttonMakePayment.setOnClickListener {
            val intent= Intent(this, PayFriendActivity::class.java)
            intent.putExtra("eventId", eventId)
            startActivity(intent)
        }

    }

}
