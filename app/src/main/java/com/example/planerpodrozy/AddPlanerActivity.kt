package com.example.planerpodrozy

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.planerpodrozy.databinding.ActivityAddPlanerBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import java.util.*

class AddPlanerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddPlanerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPlanerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val eventId = intent.getStringExtra("eventId")
        val db = Firebase.firestore
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid

        binding.buttonBack.setOnClickListener {
            val intent = Intent(this, PlanerActivity::class.java)
            intent.putExtra("eventId", eventId)
            startActivity(intent)
        }

        binding.dataplaner.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    selectedDate.also { binding.dataplaner.text = it }
                },
                year,
                month,
                dayOfMonth
            )
            datePickerDialog.show()
        }

        binding.godzinawydarzenia.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(
                this,
                { _, selectedHourOfDay, selectedMinute ->
                    val selectedTime = String.format("%02d:%02d", selectedHourOfDay, selectedMinute)
                    selectedTime.also { binding.godzinawydarzenia.text = it }
                },
                hourOfDay,
                minute,
                true
            )
            timePickerDialog.show()
        }

        binding.buttonAddPlaner.setOnClickListener {
            val planerdata = binding.dataplaner.text.toString()
            val planergodzina = binding.godzinawydarzenia.text.toString()
            val planernazwa = binding.nazwaplaner.text.toString()
            Log.e("AddPlanerActivity", "Kliknieto dodaj")

            if (eventId != null && userId != null && planerdata.isNotEmpty() && planergodzina.isNotEmpty() && planernazwa.isNotEmpty()) {
                Log.e("AddPlanerActivity", "Wchodzi do ifa")
                val planerData = hashMapOf(
                    "eventId" to eventId,
                    "PlanerData" to planerdata,
                    "PlanerGodzina" to planergodzina,
                    "PlanerNazwa" to planernazwa
                )

                db.collection("planer")
                    .add(planerData)
                    .addOnSuccessListener { documentReference ->
                        Log.e("AddPlanerActivity", "Sukcesss")

                        showToast("Pomyślnie dodano aktywność!")
                        resetFields()
                    }
                    .addOnFailureListener { e ->
                        Log.e("AddPlanerActivity", "Niesukcess")

                        showToast("Błąd podczas dodawania aktywności: ${e.message}")
                    }
            } else {
                showToast("Wprowadź wszystkie dane!")
                Log.e("AddPlanerActivity", "Nie ma danych")

            }

        }
    }
    private fun resetFields() {
        binding.dataplaner.text = "Date"
        binding.godzinawydarzenia.text = "Time"
        binding.nazwaplaner.setText("")
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}
