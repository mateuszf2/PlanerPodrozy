package com.example.planerpodrozy

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.planerpodrozy.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream
import java.lang.ref.PhantomReference


class ProfileActivity: AppCompatActivity() {
    private lateinit var binding:ActivityProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var fstore: FirebaseFirestore

    private lateinit var profileImage : ImageView
    private lateinit var profileAdd : ImageView
    private lateinit var userid : String
    private lateinit var image : ByteArray
    private lateinit var storageReference : StorageReference
    val register = registerForActivityResult(ActivityResultContracts.TakePicturePreview()){
        uploadImage(it)
    }
    val db=Firebase.firestore
    val eventsCollectionRef= db.collection("zdjecieProfilowe")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        fstore = FirebaseFirestore.getInstance()

        binding= ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userid = firebaseAuth.currentUser!!.uid
        storageReference = FirebaseStorage.getInstance().reference.child("$userid/profilePhoto")

        if(firebaseAuth.currentUser != null){
            val user = firebaseAuth.currentUser
            user?.let {
                // Name, email address, and profile photo Url
                val name = it.displayName
                val email = it.email
                val photoUrl = it.photoUrl

                // Check if user's email is verified
                val emailVerified = it.isEmailVerified

                // The user's ID, unique to the Firebase project. Do NOT use this value to
                // authenticate with your backend server, if you have one. Use
                // FirebaseUser.getIdToken() instead.
                val uid = it.uid
                val mTextView = findViewById<TextView>(R.id.tv_email) as TextView
                mTextView.text=email
            }
        }

        profileImage = findViewById(R.id.profile_image)
        profileAdd = findViewById(R.id.profile_add)

        lateinit var photoLink : String
        db.collection("zdjecieProfilowe")
            .whereEqualTo("userId", userid)
            .get()
            .addOnSuccessListener{ documents ->
                for (document in documents){
                    photoLink = document.getString("zdjecieLink")!!
                }
                Picasso.get().setLoggingEnabled(true)
                Picasso.get().load(photoLink).error(R.drawable.user).into(profileImage)
            }
//            .addSnapshotListener{ value, error ->
//            Log.i("TAG","AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
//
//            Picasso.get().setLoggingEnabled(true)
//            Picasso.get().load(value.query.get("zdjecieLink")).error(R.drawable.user).into(profileImage)
//          }


        binding.buttonBack.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        profileAdd.setOnClickListener(){
            capturePhoto()
        }

    }

    private fun capturePhoto() {
        register.launch(null)
    }

    private fun uploadImage(it: Bitmap?) {
        val baos = ByteArrayOutputStream()
        it?.compress(Bitmap.CompressFormat.JPEG, 50, baos)
        image = baos.toByteArray()
        storageReference.putBytes(image).addOnSuccessListener{
            storageReference.downloadUrl.addOnSuccessListener {
                val obj = mutableMapOf<String,String>()
                obj["zdjecieLink"] = it.toString()
                obj["userId"] = userid
                eventsCollectionRef.add(obj)
            }
        }
    }

}