package com.example.planerpodrozy
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.planerpodrozy.databinding.ActivityFinanseBinding

import com.google.firebase.Firebase
import com.google.firebase.firestore.Filter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
class DeleteFinanseActivity :AppCompatActivity(){

    lateinit var binding: ActivityFinanseBinding



    override fun onCreate(savedInstanceState: Bundle?){


        val amountFinanse = intent.getStringExtra("amountFinanse")
        val eventId=intent.getStringExtra("eventId")
        val finanseName=intent.getStringExtra("finanseName")
        val userFinanseId=intent.getStringExtra("userId")
        val finanseId=intent.getStringExtra("finanseId")


        val db= Firebase.firestore
        val currentUser= FirebaseAuth.getInstance().currentUser
        val userId= currentUser?.uid




        if(eventId!=null && userId!=null) {


                if (finanseId != null) {
                    db.collection("finanse")
                        .document(finanseId)
                        .get()
                        .addOnSuccessListener { document ->

                            db.collection("finanse").document(finanseId).delete()


                            db.collection("wydarzenia")
                                .document(eventId)
                                .get()
                                .addOnSuccessListener { document ->

                                    var usersNumber =
                                        document?.data?.get("usersNumber").toString().toInt()

                                    val dividedAmount =
                                        amountFinanse?.toDouble()?.div(usersNumber) //podzielona kwota stworzonej składki przez ilość członków, po ile każdy się składa

                                    db.collection("wydarzeniaUzytkownicy")
                                        .whereEqualTo("eventId", eventId)
                                        .get()
                                        .addOnSuccessListener { usersDocs ->
                                            for (userDoc in usersDocs) {
                                                val docUserId =
                                                    userDoc.getString("userId") //user aktualnie przetwarzanego członka wydarzenia
                                                if (docUserId != userId) { //nie potrzebny nam nasz user bo nie jesteśmy sami sobie winni pieniędzy
                                                    db.collection("bilans").document(eventId)
                                                        .collection("bilansPairs") // tworzenie tabel bilansu dla każdej pary użytkowników
                                                        .where(
                                                            Filter.or(
                                                                Filter.and(
                                                                    Filter.equalTo(
                                                                        "userId",
                                                                        userId
                                                                    ),
                                                                    Filter.equalTo(
                                                                        "friendId",
                                                                        docUserId
                                                                    )
                                                                ),
                                                                Filter.and(
                                                                    Filter.equalTo(
                                                                        "friendId",
                                                                        userId
                                                                    ),
                                                                    Filter.equalTo(
                                                                        "userId",
                                                                        docUserId
                                                                    )
                                                                )
                                                            )
                                                        )

                                                        .get()
                                                        .addOnSuccessListener { documents ->
                                                            for (document in documents) {


                                                                val documentId = document.id

                                                                if (document.getString("friendId") == userId) {
                                                                    var helpTotalBilans =
                                                                        (document.get("totalBilans")
                                                                            .toString()
                                                                            .toDouble()) + dividedAmount!!.toDouble()

                                                                    val bilansData = hashMapOf(
                                                                        "friendId" to userId,
                                                                        "totalBilans" to helpTotalBilans,
                                                                        "userId" to docUserId
                                                                    )
                                                                    db.collection("bilans")
                                                                        .document(eventId)
                                                                        .collection("bilansPairs")
                                                                        .document(documentId)
                                                                        .set(bilansData)
                                                                } else {
                                                                    var helpTotalBilans =
                                                                        (document.get("totalBilans")
                                                                            .toString()
                                                                            .toDouble()) - dividedAmount!!.toDouble()

                                                                    val bilansData = hashMapOf(
                                                                        "friendId" to docUserId,
                                                                        "totalBilans" to helpTotalBilans,
                                                                        "userId" to userId
                                                                    )
                                                                    db.collection("bilans")
                                                                        .document(eventId)
                                                                        .collection("bilansPairs")
                                                                        .document(documentId)
                                                                        .set(bilansData)
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

        super.onCreate(savedInstanceState)
        val intent= Intent(this, FinanseActivity::class.java)
        intent.putExtra("eventId", eventId)
        startActivity(intent)
    }


}