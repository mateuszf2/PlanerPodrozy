package com.example.planerpodrozy

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
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
            if(userId!=null && binding.eventNameText.text.toString()!="" && binding.locationText.text.toString()!="" && binding.editTextDate.text.toString()!="" ) {
                val eventName = binding.eventNameText.text.toString()
                val location = binding.locationText.text.toString()
                val date = binding.editTextDate.text.toString()
                val eventData = hashMapOf(
                    "nazwa_wydarzenia" to eventName,
                    "lokalizacja" to location,
                    "data" to date,
                    "usersNumber" to 1 //Po utworzeniu wydarzenia, początkowo jest 1 uczestnik, zwiększamy uczestników po akceptacji zaproszenia do wydarzenia
                )
                eventsCollectionRef.add(eventData)
                    .addOnSuccessListener { documentReference ->
                        val eventsUsersData = hashMapOf(
                            "eventId" to documentReference.id,
                            "userId" to userId
                        )
                        eventsUsersCollectionRef.add(eventsUsersData)
                            .addOnSuccessListener {showToast("Pomyślnie utworzono wydarzenie")}
                            .addOnFailureListener {showToast("Nie udało się utworzyć wydarzenia")}
                    }
                    .addOnFailureListener { e ->
                    }
            }

        }

    }
    private fun showToast(message: String) {
        val toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        toast.show()


        val czasTrwaniaToast = 2000
        Handler().postDelayed({
            toast.cancel()
        }, czasTrwaniaToast.toLong())
    }
}