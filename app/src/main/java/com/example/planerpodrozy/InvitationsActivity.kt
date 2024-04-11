package com.example.planerpodrozy

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.planerpodrozy.databinding.ActivityInvitationsBinding
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore

class InvitationsActivity : AppCompatActivity(), InvitationAdapter.OnEventClickListener {
    private lateinit var binding: ActivityInvitationsBinding
    private lateinit var eventRecyclerView: RecyclerView
    private lateinit var invitationAdapter: InvitationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
        binding = ActivityInvitationsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //wywołanie funkcji generującej liste
        generateInvitationList()

        binding.buttonBack.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }


    //BARDZO PODOBNE DO TEGO CO W MAIN ACTIVITY, generowanie listy zaproszeń do wydarzeń za pomocą recycler view, korzystamy też z tego samego adaptera(eventAdapter), bo też generujemy listę nazw wydarzeń
    fun generateInvitationList(){
        eventRecyclerView = binding.recyclerViewEvents
        invitationAdapter = InvitationAdapter()
        eventRecyclerView.adapter = invitationAdapter
        eventRecyclerView.layoutManager = LinearLayoutManager(this)

        invitationAdapter.setOnEventClickListener(this)

        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid
        val db= Firebase.firestore
        val eventsCollectionRef = db.collection("wydarzenia")

        if(userId!=null){
            val email = currentUser.email
            db.collection("zaproszenia")
                .whereEqualTo("friendEmail", email)
                .get()
                .addOnSuccessListener { documents ->
                    val eventsList = mutableListOf<Event>()
                    val tasks = mutableListOf<Task<DocumentSnapshot>>()

                    for(document in documents){
                        val eventId = document.getString("eventId")
                        if (eventId != null) {
                            val task = eventsCollectionRef.document(eventId).get()
                            tasks.add(task)
                        }
                    }
                    //Czeka aż wszystkie taski się zakończą- rozwiązanie problemu asynchroniczności funkcji addOnSuccessListener
                    Tasks.whenAllSuccess<DocumentSnapshot>(tasks)
                        .addOnSuccessListener { snapshots ->
                            for(snapshot in snapshots) {
                                val nazwaWydarzenia = snapshot.getString("nazwa_wydarzenia")
                                val currentEvent = Event(snapshot.id, nazwaWydarzenia)
                                eventsList.add(currentEvent)
                            }
                            invitationAdapter.submitList(eventsList)
                        }
                        .addOnFailureListener { e ->

                        }
                }
                .addOnFailureListener { e->

                }
        }
    }

    //Co się dzieje po akceptacji zaproszenia
    override fun onEventAccept(event: Event) {
        val db = Firebase.firestore
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid

        if(userId != null) {
            val wydarzenieUzytkownik= hashMapOf(
                "eventId" to event.eventId,
                "userId" to userId
            )
            db.collection("wydarzeniaUzytkownicy")
                .add(wydarzenieUzytkownik)
                .addOnSuccessListener { documentReference ->
                    db.collection("zaproszenia")
                        .whereEqualTo("eventId", event.eventId)
                        .get()
                        .addOnSuccessListener { documents ->
                            for(invitationDocument in documents) {
                                db.collection("zaproszenia").document(invitationDocument.id).delete()
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
    override fun onEventCancel(event: Event) {
        val db = Firebase.firestore
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid

        if(userId != null) {
            db.collection("zaproszenia")
                .whereEqualTo("eventId", event.eventId)
                .get()
                .addOnSuccessListener { documents ->
                    for(invitationDocument in documents) {
                        db.collection("zaproszenia").document(invitationDocument.id).delete()
                    }
                    generateInvitationList()
                }
                .addOnFailureListener { e ->

                }
        }
    }

}