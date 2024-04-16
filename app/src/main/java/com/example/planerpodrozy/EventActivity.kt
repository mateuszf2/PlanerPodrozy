package com.example.planerpodrozy

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.planerpodrozy.databinding.ActivityEventBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.selects.select


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

        val recyclerView: RecyclerView= findViewById(R.id.recycler_view_MenuBar)
        val options= arrayOf("Podstawowe informacje", "Członkowie", "Wspólne finanse", "Kalendarz", "Zamknij")
        val adapter= MenuBarAdapter(options) { selectedOption ->
            if(selectedOption == "Zamknij") {
                recyclerView.visibility = View.GONE
                binding.buttonMenuBar.visibility= View.VISIBLE
            }
            else if(selectedOption == "Członkowie") {
                val intent= Intent(this, MembersActivity::class.java)
                intent.putExtra("eventId", eventId)
                startActivity(intent)
            }
        }
        recyclerView.adapter= adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        binding.buttonMenuBar.setOnClickListener{
            recyclerView.visibility= View.VISIBLE
            binding.buttonMenuBar.visibility= View.GONE
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
                                val location = documentData["lokalizacja"]
                                val data = documentData["data"]
                                val eventNameTextView: TextView = findViewById<TextView>(R.id.textView_eventName)
                                val locationTextView: TextView = findViewById<TextView>(R.id.textView_location)
                                val dateTextView: TextView = findViewById<TextView>(R.id.textView_date)
                                eventNameTextView.text = eventName.toString()
                                locationTextView.text = location.toString()
                                dateTextView.text = data.toString()
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