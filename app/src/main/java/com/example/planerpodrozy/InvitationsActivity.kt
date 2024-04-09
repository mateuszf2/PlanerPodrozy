package com.example.planerpodrozy

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.planerpodrozy.databinding.ActivityInvitationsBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class InvitationsActivity : AppCompatActivity(){
    private lateinit var binding: ActivityInvitationsBinding
    private lateinit var eventRecyclerView: RecyclerView
    private lateinit var invitationsAdapter: InvitationsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
        binding = ActivityInvitationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid
        val db= Firebase.firestore

        binding.buttonBack.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}