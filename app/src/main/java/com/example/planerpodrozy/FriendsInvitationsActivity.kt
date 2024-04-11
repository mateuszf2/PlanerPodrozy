package com.example.planerpodrozy

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.planerpodrozy.databinding.ActivityFriendsInvitationsBinding


class FriendsInvitationsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFriendsInvitationsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendsInvitationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonBackFriendsInvitations.setOnClickListener{
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }
}