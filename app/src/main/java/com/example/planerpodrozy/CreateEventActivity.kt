package com.example.planerpodrozy

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.planerpodrozy.databinding.ActivityCreateEventBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class CreateEventActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateEventBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = Firebase.firestore
        val eventsCollectionRef = db.collection("wydarzenia")
        val eventsUsersCollectionRef = db.collection("wydarzeniaUzytkownicy")
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid

        binding = ActivityCreateEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.buttonAccept.setOnClickListener {
            if (userId != null && binding.eventNameText.text.toString() != "" && binding.locationText.text.toString() != "" && binding.editTextDate.text.toString() != "") {
                val eventName = binding.eventNameText.text.toString()
                val location = binding.locationText.text.toString()
                val date = binding.editTextDate.text.toString()
                val eventData = hashMapOf(
                    "nazwa_wydarzenia" to eventName,
                    "lokalizacja" to location,
                    "data" to date,
                    "usersNumber" to 1 // Po utworzeniu wydarzenia, początkowo jest 1 uczestnik
                )
                eventsCollectionRef.add(eventData)
                    .addOnSuccessListener { documentReference ->
                        val eventsUsersData = hashMapOf(
                            "eventId" to documentReference.id,
                            "userId" to userId
                        )
                        eventsUsersCollectionRef.add(eventsUsersData)
                            .addOnSuccessListener {
                                showToast("Pomyślnie utworzono wydarzenie")
                                binding.eventNameText.setText("")
                                binding.locationText.setText("")
                                binding.editTextDate.setText("")
                            }
                            .addOnFailureListener {
                                showToast("Nie udało się utworzyć wydarzenia")
                            }
                    }
                    .addOnFailureListener { e ->
                        showToast("Nie udało się utworzyć wydarzenia")
                    }
            }
        }

        binding.editTextDate.setOnClickListener {
            showDatePickerDialog()
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            binding.editTextDate.text = selectedDate
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
