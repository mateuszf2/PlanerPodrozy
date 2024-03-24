package com.example.planerpodrozy

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.planerpodrozy.R
import com.example.planerpodrozy.databinding.ActivityCreateEventBinding
import com.example.planerpodrozy.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val currentUser= FirebaseAuth.getInstance().currentUser
        val userId= currentUser?.uid

        binding.buttonCreateEvent.setOnClickListener{
            val intent = Intent(this, CreateEventActivity::class.java)
            startActivity(intent)
        }

        if(userId!=null){
            val db= Firebase.firestore

            db.collection("wydarzenia")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { documents->
                for(document in documents)
                    {
                        val nazwaWydarzenia= document.getString("nazwa_wydarzenia")
                        Log.i("TAG", "Nazwa wydarzenia $nazwaWydarzenia")
                    }
                }
                .addOnFailureListener{ e->

                }
        }
    }

}