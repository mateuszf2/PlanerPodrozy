package com.example.planerpodrozy

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.planerpodrozy.databinding.ActivityAddFinanseBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class AddFinanseActivity: AppCompatActivity() {
    lateinit var binding: ActivityAddFinanseBinding

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding= ActivityAddFinanseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val eventId= intent.getStringExtra("eventId")
        val db= Firebase.firestore
        val currentUser= FirebaseAuth.getInstance().currentUser
        val userId= currentUser?.uid




        binding.buttonBack.setOnClickListener{
            intent= Intent(this, FinanseActivity::class.java)
            intent.putExtra("eventId", eventId)
            startActivity(intent)
        }

        binding.buttonAddBudget.setOnClickListener {
            if(eventId!=null && userId!=null){
                val finanseName= binding.finanseNameText.text.toString()
                val amountFinanse= binding.amountNumber.text.toString()
                if(finanseName!= "" && amountFinanse!= ""){
                    val finanseData= hashMapOf(
                        "eventId" to eventId,
                        "finanseName" to finanseName,
                        "amountFinanse" to amountFinanse,
                        "userId" to userId
                    )
                    db.collection("finanse")
                        .add(finanseData)
                        .addOnSuccessListener {
                            db.collection("wydarzenia")
                                .document(eventId!!)
                                .get()
                                .addOnSuccessListener { document->
                                    resetFields()
                                    Toast.makeText(this, "Pomyślnie dodano finanse", Toast.LENGTH_SHORT).show()
                                    var usersNumber= document?.data?.get("usersNumber").toString().toInt()

                                    val dividedAmount= amountFinanse.toFloat()/usersNumber //podzielona kwota stworzonej składki przez ilość członków, po ile każdy się składa

                                    db.collection("wydarzeniaUzytkownicy")
                                        .whereEqualTo("eventId", eventId)
                                        .get()
                                        .addOnSuccessListener { usersDocs ->
                                            for(userDoc in usersDocs){
                                                val docUserId= userDoc.getString("userId") //user aktualnie przetwarzanego członka wydarzenia
                                                if(docUserId!=userId){ //nie potrzebny nam nasz user bo nie jesteśmy sami sobie winni pieniędzy
                                                    db.collection("bilans") // tworzenie tabel bilansu dla każdej pary użytkowników
                                                        .whereEqualTo("userId",userId)
                                                        .whereEqualTo("friendId", docUserId)
                                                        .get()
                                                        .addOnSuccessListener { bilansDocs ->
                                                            Log.i(TAG, "INFOSTO")
                                                            if(bilansDocs.isEmpty){
                                                                val newBilansData= hashMapOf(
                                                                    "userId" to userId,
                                                                    "friendId" to docUserId,
                                                                    "totalBilans" to dividedAmount
                                                                )
                                                                db.collection("bilans")
                                                                    .add(newBilansData)
                                                                    .addOnSuccessListener { documents->

                                                                    }
                                                                    .addOnFailureListener { e->

                                                                    }
                                                            }
                                                            else{
                                                                for(bilansDoc in bilansDocs){ //mimo, że wiem że powinien znaleźć się tylko jeden dokument to i tak trzeba użyć fora, nawet jeśli będzie to tylko jedno wykonanie pętli
                                                                    var currentBilans= bilansDoc.getDouble("totalBilans") ?: 0.0
                                                                    currentBilans+=dividedAmount
                                                                    val newBilansData= hashMapOf<String, Any>(
                                                                        "totalBilans" to currentBilans
                                                                    )
                                                                    db.collection("bilans").document(bilansDoc.id).update(newBilansData)
                                                                        .addOnSuccessListener { documents->
                                                                            //Aktualizacja zakończona sukcesem
                                                                        }
                                                                        .addOnFailureListener { e->
                                                                            //Błąd podczas aktualizacji dokumentu- error
                                                                        }
                                                                }
                                                            }
                                                            db.collection("bilans")
                                                                .whereEqualTo("userId", docUserId)
                                                                .whereEqualTo("friendId", userId)
                                                                .get()
                                                                .addOnSuccessListener { bilansDocs ->
                                                                    if(bilansDocs.isEmpty){
                                                                        val newBilansData= hashMapOf(
                                                                            "userId" to docUserId,
                                                                            "friendId" to userId,
                                                                            "totalBilans" to -dividedAmount
                                                                        )
                                                                        db.collection("bilans")
                                                                            .add(newBilansData)
                                                                            .addOnSuccessListener { documents->

                                                                            }
                                                                            .addOnFailureListener { e->

                                                                            }
                                                                    }
                                                                    else{
                                                                        for(bilansDoc in bilansDocs){ //mimo, że wiem że powinien znaleźć się tylko jeden dokument to i tak trzeba użyć fora, nawet jeśli będzie to tylko jedno wykonanie pętli
                                                                            var currentBilans= bilansDoc.getDouble("totalBilans") ?: 0.0
                                                                            currentBilans-=dividedAmount
                                                                            val newBilansData= hashMapOf<String, Any>(
                                                                                "totalBilans" to currentBilans
                                                                            )
                                                                            db.collection("bilans").document(bilansDoc.id).update(newBilansData)
                                                                        }
                                                                    }
                                                                }
                                                                .addOnFailureListener { e->

                                                                }
                                                        }
                                                        .addOnFailureListener { e->

                                                        }

                                                }
                                            }
                                        }
                                        .addOnFailureListener { e->

                                        }
                                }
                                .addOnFailureListener { e->

                                }

                        }
                        .addOnFailureListener { e ->

                        }
                }
            }
        }
    }
    private fun resetFields() {
        binding.finanseNameText.setText("Budget name")
        binding.amountNumber.setText("Amount of money")
    }
}