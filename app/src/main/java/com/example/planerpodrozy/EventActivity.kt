package com.example.planerpodrozy

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.planerpodrozy.databinding.ActivityEventBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class EventActivity:AppCompatActivity() {
    private lateinit var binding: ActivityEventBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db= Firebase.firestore
        val eventsCollectionRef= db.collection("wydarzenia")
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid
        //odczytanie id wydarzenia z intentu(id_wydarzenia czyli id dokumentu wydarzenia z firebase)
        val eventId = intent.getStringExtra("eventId")

        binding = ActivityEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonBack.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.buttonAddFriend.setOnClickListener{
            val intent= Intent(this, InviteToEventActivity::class.java)
            intent.putExtra("eventId", eventId)
            startActivity(intent)
        }

        if(userId!=null){
            //pobranie intentu, która uruchomił aktywność
            val intent = intent

            if(eventId!=null){
                eventsCollectionRef.document(eventId)
                    .get()
                    .addOnSuccessListener { document ->
                        if(document != null){
                            val documentData = document.data
                            if(documentData != null){
                                val eventName = documentData["nazwa_wydarzenia"]
                                val eventNameTextView: TextView = findViewById<TextView>(R.id.textView_eventName)
                                eventNameTextView.text = eventName.toString()
                            }
                        }
                    }
                    .addOnFailureListener{ exception->
                        Log.d("TAG", "Błąd podczas pobierania dokumentu: ", exception)
                    }
            }
        }


    }
}