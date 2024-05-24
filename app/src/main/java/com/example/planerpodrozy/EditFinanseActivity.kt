package com.example.planerpodrozy

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.planerpodrozy.databinding.ActivityEditFinanseBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.Filter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.delay

class EditFinanseActivity:AppCompatActivity() {

    lateinit var binding: ActivityEditFinanseBinding




    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding= ActivityEditFinanseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val amountFinanse = intent.getStringExtra("amountFinanse")
        val eventId=intent.getStringExtra("eventId")
        val finanseName=intent.getStringExtra("finanseName")
        val userFinanseId=intent.getStringExtra("userId")
        val finanseId=intent.getStringExtra("finanseId")


        val db= Firebase.firestore
        val currentUser= FirebaseAuth.getInstance().currentUser
        val userId= currentUser?.uid


        binding.finanseNameText.setText(finanseName)
        binding.amountNumber.setText(amountFinanse)

        val oldAmountNumber = amountFinanse



        binding.buttonBack.setOnClickListener{
            intent= Intent(this, FinanseActivity::class.java)
            intent.putExtra("eventId", eventId)
            startActivity(intent)
        }

        binding.buttonEditBudget.setOnClickListener {
            lateinit var documentId : String



            if(eventId!=null && userId!=null){
                val finanseName2= binding.finanseNameText.text.toString()
                val amountFinanse2= binding.amountNumber.text.toString()
                if(finanseName2!= "" && amountFinanse2!= ""){

                    if (finanseId != null) {
                        db.collection("finanse")
                            .document(finanseId)
                            .get()
                            .addOnSuccessListener {document->

                                documentId = document.id

                                val finanseData= hashMapOf(
                                    "eventId" to eventId,
                                    "finanseName" to finanseName2,
                                    "amountFinanse" to amountFinanse2.toDouble(),
                                    "userId" to userId
                                )
                                db.collection("finanse").document(documentId).set(finanseData)


                                db.collection("wydarzenia")
                                    .document(eventId)
                                    .get()
                                    .addOnSuccessListener { document->
                                        resetFields()
                                        Toast.makeText(this, "Pomyślnie edytowano finanse", Toast.LENGTH_SHORT).show()

                                        var usersNumber= document?.data?.get("usersNumber").toString().toInt()

                                        val dividedAmount= amountFinanse2.toFloat()/usersNumber //podzielona kwota stworzonej składki przez ilość członków, po ile każdy się składa

                                        db.collection("wydarzeniaUzytkownicy")
                                            .whereEqualTo("eventId", eventId)
                                            .get()
                                            .addOnSuccessListener { usersDocs ->
                                                for(userDoc in usersDocs){
                                                    val docUserId= userDoc.getString("userId") //user aktualnie przetwarzanego członka wydarzenia
                                                    if(docUserId!=userId){ //nie potrzebny nam nasz user bo nie jesteśmy sami sobie winni pieniędzy
                                                        db.collection("bilans").document(eventId).collection("bilansPairs") // tworzenie tabel bilansu dla każdej pary użytkowników
                                                            .where(Filter.or(Filter.and(Filter.equalTo("userId",userId),Filter.equalTo("friendId",docUserId)),
                                                                    Filter.and(Filter.equalTo("friendId",userId),Filter.equalTo("userId",docUserId))))

                                                            .get()
                                                            .addOnSuccessListener {documents->
                                                                for (document in documents){
                                                                    val difference =
                                                                        oldAmountNumber?.toDouble()
                                                                            ?.minus(amountFinanse2.toDouble())

                                                                    val documentId = document.id

                                                                    var helpTotalBilans = ((document.get("totalBilans").toString().toDouble())+(difference!!)/usersNumber)

                                                                    if (document.getString("friendId")==userId){
                                                                        var helpTotalBilans = ((document.get("totalBilans").toString().toDouble())+(difference!!)/usersNumber)

                                                                        val bilansData= hashMapOf(
                                                                            "friendId" to userId,
                                                                            "totalBilans" to helpTotalBilans,
                                                                            "userId" to docUserId
                                                                        )
                                                                        db.collection("bilans").document(eventId).collection("bilansPairs")
                                                                            .document(documentId).set(bilansData)
                                                                    }

                                                                    else{
                                                                        var helpTotalBilans = ((document.get("totalBilans").toString().toDouble())-(difference!!)/usersNumber)

                                                                        val bilansData= hashMapOf(
                                                                            "friendId" to docUserId,
                                                                            "totalBilans" to helpTotalBilans,
                                                                            "userId" to userId
                                                                        )
                                                                        db.collection("bilans").document(eventId).collection("bilansPairs")
                                                                            .document(documentId).set(bilansData)
                                                                    }


                                                                }

                                                            }


                                                    }
                                                }
                                            }

                                    }


                            }
                    }


                }
            }

            Thread.sleep(500)
            val intent= Intent(this, FinanseActivity::class.java)
            intent.putExtra("eventId", eventId)
            startActivity(intent)
        }



    }

    private fun resetFields() {
        binding.finanseNameText.setText("")
        binding.amountNumber.setText("")
    }

}