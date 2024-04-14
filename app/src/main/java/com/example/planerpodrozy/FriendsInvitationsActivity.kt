package com.example.planerpodrozy

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.planerpodrozy.databinding.ActivityFriendsInvitationsBinding
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore


class FriendsInvitationsActivity : AppCompatActivity(),
    FriendsInvitationsAdapter.OnEventClickListener {
    private lateinit var binding: ActivityFriendsInvitationsBinding
    private lateinit var eventRecyclerView: RecyclerView
    private lateinit var FriendsInvitationsAdapter: FriendsInvitationsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendsInvitationsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        generateInvitationList()


        binding.buttonBackFriendsInvitations.setOnClickListener{
            val intent = Intent(this, FriendsListActivity::class.java)
            startActivity(intent)
        }
    }


    //BARDZO PODOBNE DO TEGO CO W MAIN ACTIVITY, generowanie listy zaproszeń do wydarzeń za pomocą recycler view, korzystamy też z tego samego adaptera(eventAdapter), bo też generujemy listę nazw wydarzeń
    fun generateInvitationList(){
        eventRecyclerView = binding.recyclerViewFriendsInvitations
        FriendsInvitationsAdapter = FriendsInvitationsAdapter()
        eventRecyclerView.adapter = FriendsInvitationsAdapter
        eventRecyclerView.layoutManager = LinearLayoutManager(this)

        FriendsInvitationsAdapter.setOnEventClickListener(this)

        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid
        val db= Firebase.firestore

        if(userId!=null){
            val email = currentUser.email
            db.collection("zaproszeniaZnajomi")
                .whereEqualTo("friendEmail", email)
                .get()
                .addOnSuccessListener { documents ->
                    val friendsList = mutableListOf<Friend>()

                    for(document in documents){
                        val currentFriend = document.getString("userId")?.let {
                            Friend(
                                it,
                                document.get("userEmail").toString(),
                                document.get("friendEmail").toString())
                        }
                        if (currentFriend != null) {
                            friendsList.add(currentFriend)
                        }
                    }
                    FriendsInvitationsAdapter.submitList(friendsList)
                }
                .addOnFailureListener { e->

                }
        }
    }

    //Co się dzieje po akceptacji zaproszenia
    override fun onEventAccept(friend: Friend) {
        val db = Firebase.firestore
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid

        if(userId != null) {
            val znajomy= hashMapOf(
                "userId" to friend.userId ,
                "userEmail" to friend.userEmail ,
                "friendEmail" to friend.friendEmail
            )
            db.collection("znajomi")
                .add(znajomy)
                .addOnSuccessListener { documentReference ->
                    db.collection("zaproszeniaZnajomi")
                        .whereEqualTo("friendEmail", friend.friendEmail)
                        .get()
                        .addOnSuccessListener { documents ->
                            for(invitationDocument in documents) {
                                db.collection("zaproszeniaZnajomi").document(invitationDocument.id).delete()
                            }
                            generateInvitationList() //wywołanie funkcji generującej liste
                        }
                        .addOnFailureListener { e ->

                        }
                }
                .addOnFailureListener { e ->

                }
        }
    }
    //Co się dzieje po odrzuceniu zaproszenia
    override fun onEventCancel(friend: Friend) {
        val db = Firebase.firestore
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid

        if(userId != null) {
            db.collection("zaproszeniaZnajomi")
                .whereEqualTo("friendEmail", friend.friendEmail)
                .get()
                .addOnSuccessListener { documents ->
                    for(invitationDocument in documents) {
                        db.collection("zaproszeniaZnajomi").document(invitationDocument.id).delete()
                    }
                    generateInvitationList()
                }
                .addOnFailureListener { e ->

                }
        }
    }
}