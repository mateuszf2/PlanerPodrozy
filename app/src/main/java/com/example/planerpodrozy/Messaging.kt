package com.example.planerpodrozy

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.planerpodrozy.databinding.ActivityEventBinding
import com.example.planerpodrozy.databinding.ActivityMessagingBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FieldValue.serverTimestamp
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class Messaging : AppCompatActivity(),MessageAdapter.OnEventClickListener{
    private lateinit var binding: ActivityMessagingBinding
    private lateinit var sendMessageEditText: EditText
    private lateinit var sendMessageButton: FloatingActionButton
    private lateinit var chatroomId:String
    private lateinit var MessageRecyclerView: RecyclerView
    private lateinit var MessageAdapter: MessageAdapter
    private lateinit var context: Context


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db= Firebase.firestore
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid.toString()
        val userEmail = currentUser?.email.toString()
        var i=0
        chatroomId=""

        binding = ActivityMessagingBinding.inflate(layoutInflater)
        sendMessageButton = binding.btSendMessage
        sendMessageEditText = binding.etSendMessage

        setContentView(binding.root)
        context = this
        MessageRecyclerView = binding.messageRecyclerView
        MessageAdapter =MessageAdapter()
        MessageRecyclerView.adapter = MessageAdapter
        MessageRecyclerView.layoutManager = LinearLayoutManager(this)

        MessageAdapter.setOnEventClickListener(this)

        val friendEmail = intent.getStringExtra("friendEmail").toString()

        fun fetchMessages(chatroomId:String){
            val messagesList = mutableListOf<MessageModel>()
            db.collection("chats").document(chatroomId)
                .collection("messages")
                .orderBy("messageTime", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener{ documents->
                    for (document in documents){
                        val message = MessageModel(
                            document.get("message").toString(),
                            document.get("messageSender").toString(),
                            document.get("messageReceiver").toString(),
                            document.get("messageTime").toString()
                        )
                        if (message != null ){
                            messagesList.add(message)
                        }
                    }
                    MessageAdapter.submitList(messagesList)
                    MessageRecyclerView.scrollToPosition(documents.size()-1)
                }

        }
        fun checkIfChatroomExist() :String{
            val friendNameTextView: TextView = findViewById(R.id.textView_friendName)
            val profilePhotoImageView: ImageView =findViewById(R.id.iv_profile_photo)
            db.collection("chats")
                .where(Filter.or(Filter.and(Filter.equalTo("messageSender",userEmail),
                    Filter.equalTo("messageReceiver",friendEmail)),
                                 Filter.and(Filter.equalTo("messageReceiver",userEmail),
                    Filter.equalTo("messageSender",friendEmail))))
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.size() == 1){
                        chatroomId=documents.documents.get(0).id
                    }
                    else{
                        chatroomId=db.collection("chats").document().id
                    }
                    fetchMessages(chatroomId)

                    friendNameTextView.text=friendEmail
                    db.collection("zdjecieProfilowe")
                        .where(Filter.equalTo("userEmail",friendEmail))
                        .addSnapshotListener { value, error ->
                            if (value != null) {
                                for (document in value){
                                        Picasso.get().load(document.getString("zdjecieLink")).error(R.drawable.user).into(profilePhotoImageView)
                                }
                            }
                        }
                }
            return chatroomId
        }
        fun formatTimestamp(timestamp: Timestamp): String {
            Locale.setDefault(Locale("pl", "PL"))
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault() )
            val date = Date((timestamp.seconds * 1000 + timestamp.nanoseconds / 1000000))
            return dateFormat.format(date)
        }
        fun sendMessage(){
            val message = sendMessageEditText.text.toString()
            if(TextUtils.isEmpty(message)){
                sendMessageEditText.error = "Enter some message to send"
            }
            else{
                val formattedDate = formatTimestamp(Timestamp.now())
                val messageObject = MessageModel(
                    message,
                    userEmail,
                    friendEmail,
                    formattedDate
                )
                db.collection("chats").document(chatroomId).set(messageObject)
                db.collection("chats").document(chatroomId)
                    .collection("messages").add(messageObject).addOnSuccessListener {
                    Log.d("onSuccess", "Succesfully sent message")
                    fetchMessages(chatroomId)
                    sendMessageEditText.text.clear()
                }
            }
        }

        checkIfChatroomExist()


        binding.btSendMessage.setOnClickListener(){
            sendMessage()
        }


    }


}
