package com.example.planerpodrozy

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.planerpodrozy.databinding.ActivityInviteToEventBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import java.util.regex.Pattern

class InviteToEventActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInviteToEventBinding

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding= ActivityInviteToEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid
        val db = Firebase.firestore
        val invitationsCollectionRef = db.collection("zaproszenia")
        val eventId= intent.getStringExtra("eventId")

        binding.buttonBack.setOnClickListener{
            val intent= Intent(this, EventActivity::class.java)
            intent.putExtra("eventId", eventId)
            startActivity(intent)
        }

        binding.buttonSendInvitation.setOnClickListener{
            if(userId!=null && binding.editTextEmail.toString() != ""){
                val friendEmail = binding.editTextEmail.text.toString()
                if(isEmailValid(friendEmail)){
                    val invitationData = hashMapOf(
                        "eventCreatorId" to userId,
                        "eventId" to eventId,
                        "friendEmail" to friendEmail
                    )
                    invitationsCollectionRef.add(invitationData)
                        .addOnSuccessListener { documentReference->
                            Log.i("TAG", "UDALO SIĘ")
                            showToast("Wysłano zaproszenie do wyjazdu")
                            resetFields()
                        }
                        .addOnFailureListener{ e->
                            Log.i("TAG", "NIE UDALO SIĘ")
                            showToast("Nie wysłano zaproszenia do wyjazdu")
                        }
                }
            }
        }
    }
    private fun isEmailValid(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        val pattern = Pattern.compile(emailPattern)
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }
    private fun resetFields() {
        binding.editTextEmail.setText("")

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