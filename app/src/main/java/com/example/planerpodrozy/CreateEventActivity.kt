package com.example.planerpodrozy

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.example.planerpodrozy.databinding.ActivityCreateEventBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class CreateEventActivity:AppCompatActivity() {
    private lateinit var binding: ActivityCreateEventBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db= Firebase.firestore
        val eventsCollectionRef= db.collection("wydarzenia")
        val eventsUsersCollectionRef= db.collection("wydarzeniaUzytkownicy")
        val currentUser= FirebaseAuth.getInstance().currentUser
        val userId= currentUser?.uid

        binding= ActivityCreateEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonBack.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.buttonAccept.setOnClickListener{
            if(userId!=null && binding.eventNameText.text.toString()!="" && binding.locationText.text.toString()!="" && binding.editTextDate.text.toString()!="" ){
                val eventName= binding.eventNameText.text.toString()
                val location= binding.locationText.text.toString()
                val date= binding.editTextDate.text.toString()
                val eventData= hashMapOf(
                    "nazwa_wydarzenia" to eventName,
                    "lokalizacja" to location,
                    "data" to date
                )
                eventsCollectionRef.add(eventData)
                    .addOnSuccessListener {documentReference->
                        val eventsUsersData= hashMapOf(
                        "eventId" to documentReference.id,
                        "userId" to userId
                        )
                        eventsUsersCollectionRef.add(eventsUsersData)
                            .addOnSuccessListener {}
                            .addOnFailureListener{}
                    }
                    .addOnFailureListener { e ->
                    }

            }
        }

    }
}