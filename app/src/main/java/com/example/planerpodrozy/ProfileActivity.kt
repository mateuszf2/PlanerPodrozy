package com.example.planerpodrozy

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.planerpodrozy.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import androidx.recyclerview.widget.RecyclerView

//Q import kotlinx.android.synthetic.main.activity_main.*


class ProfileActivity: AppCompatActivity() {
    private lateinit var binding:ActivityProfileBinding
    private lateinit var eventRecyclerView: RecyclerView
    private lateinit var eventAdapter: EventAdapter
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()

        binding= ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //Q setContentView(R.layout.activity_profile)

//        //eventRecyclerView = binding.recyclerViewEvents
//        eventAdapter = EventAdapter()
//        eventRecyclerView.adapter = eventAdapter
//        eventRecyclerView.layoutManager = LinearLayoutManager(this)

        if(firebaseAuth.currentUser != null){
            val user = firebaseAuth.currentUser
            user?.let {
                // Name, email address, and profile photo Url
                val name = it.displayName
                //val email = it.email
                val photoUrl = it.photoUrl

                // Check if user's email is verified
                val emailVerified = it.isEmailVerified

                // The user's ID, unique to the Firebase project. Do NOT use this value to
                // authenticate with your backend server, if you have one. Use
                // FirebaseUser.getIdToken() instead.
                val uid = it.uid
                val email = user.email
                val mTextView = findViewById<TextView>(R.id.tv_email) as TextView
                mTextView.text=email
                setContentView(binding.root)
            }

        }
        binding.buttonBack.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

}