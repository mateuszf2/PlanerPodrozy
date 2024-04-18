package com.example.planerpodrozy

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.planerpodrozy.databinding.ActivityCreateFriendBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class CreateFriendActivity:AppCompatActivity() {
    private lateinit var binding: ActivityCreateFriendBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fstore = FirebaseFirestore.getInstance()
        val firebaseAuth = FirebaseAuth.getInstance()
        val currentUser= FirebaseAuth.getInstance().currentUser
        val userId= firebaseAuth.currentUser!!.uid
        val userEmail= firebaseAuth.currentUser!!.email
        val friendsCollectionRef=fstore.collection("zaproszeniaZnajomi")

        binding= ActivityCreateFriendBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonBack.setOnClickListener{
            val intent = Intent(this, FriendsListActivity::class.java)
            startActivity(intent)
        }

        binding.buttonAcceptFriend.setOnClickListener{
            if(userId!=null && binding.friendNameText.text.toString()!=""){
                val friendName= binding.friendNameText.text.toString()
                val friendData= hashMapOf(
                    "userId" to userId,
                    "userEmail" to userEmail,
                    "friendEmail" to friendName
                )
                friendsCollectionRef.add(friendData as Map<String,String>)
            }
            showToast("Wysłano zaproszenie do znajomych")
        }

    }
    private fun showToast(message: String) {
        val toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        toast.show()


        val czasTrwaniaToast = 1500
        Handler().postDelayed({
            toast.cancel()
        }, czasTrwaniaToast.toLong())
    }
}