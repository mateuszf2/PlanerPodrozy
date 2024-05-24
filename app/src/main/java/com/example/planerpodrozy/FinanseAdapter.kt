package com.example.planerpodrozy

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class FinanseAdapter(private val listener: OnEventClickListener): ListAdapter<Finanse, FinanseAdapter.FinanseViewHolder>(FinanseDiffCallback()) {
    private var onEventClickListener: FinanseAdapter.OnEventClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FinanseViewHolder {
        val itemView= LayoutInflater.from(parent.context).inflate(R.layout.item_finanse, parent, false)
        return FinanseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FinanseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class FinanseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val finanseName: TextView = itemView.findViewById(R.id.textView_finanseName)
        private val userFinanse: TextView = itemView.findViewById(R.id.textView_userFinanse)
        private val amountFinanse: TextView= itemView.findViewById(R.id.textView_amountFinanse)
        private val amountDisplay: TextView= itemView.findViewById(R.id.textView_amountDisplay)
        private val db= Firebase.firestore

        private val buttonEdit: Button = itemView.findViewById(R.id.buttonEdit)
        private val buttonDelete: Button = itemView.findViewById(R.id.buttonDelete)

        val currentUser= FirebaseAuth.getInstance().currentUser
        val userId= currentUser?.uid


        fun bind(finanse: Finanse) {
            finanseName.text= finanse.finanseName
            db.collection("idEmail")
                .whereEqualTo("userId", finanse.userId)
                .get()
                .addOnSuccessListener { userEmails ->
                    for(userEmail in userEmails){
                        userFinanse.text= userEmail.getString("userEmail")
                    }
                }
            if (finanse.amountFinanse.toString().contains('.')){
                val help =finanse.amountFinanse.toString().substring(0,finanse.amountFinanse.toString().indexOf('.'))
                amountFinanse.text= "Całkowita kwota składki: ${help} zł"
            }


            var usersCount: Int
            db.collection("wydarzenia").document(finanse.eventId)
                .get()
                .addOnSuccessListener { usersNumberDoc->
                    usersCount= usersNumberDoc.data?.get("usersNumber").toString().toInt()
                    val amountPerUser= (finanse.amountFinanse/usersCount).toString()

                    val currentUser= FirebaseAuth.getInstance().currentUser
                    val userId= currentUser?.uid

                    db.collection("finansePay")
                        .whereEqualTo("finanseId", finanse.finanseId)
                        .whereEqualTo("userId", userId)
                        .get()
                        .addOnSuccessListener { paymentDocuments ->
                            if (amountPerUser.substring(amountPerUser.indexOf('.',0)+1).length>2){
                                var sub1 = amountPerUser.substring(0,amountPerUser.indexOf('.',0)+1)
                                var sub2 = amountPerUser.substring(amountPerUser.indexOf('.',0)+1).take(2)
                                var help = sub1+sub2+" zł"
                                amountDisplay.text= "Kwota na osobe: ${help}"
                            }
                            else{
                                amountDisplay.text= "Kwota na osobe: ${amountPerUser} zł"
                            }
                        }
                        .addOnFailureListener { e->

                        }
                }
                .addOnFailureListener { e->

                }


            buttonEdit.setOnClickListener {
                if (userId!=finanse.userId){
                    Toast.makeText(itemView.context, "Nie jesteś stwórcą składki!", Toast.LENGTH_SHORT).show()
                }
                else{
                    listener.onFinanseEdit(finanse.amountFinanse.toString(),finanse.eventId,
                        finanse.finanseName,finanse.userId,finanse.finanseId)
                }

            }

            buttonDelete.setOnClickListener {
                if (userId!=finanse.userId){
                    Toast.makeText(itemView.context, "Nie jesteś stwórcą składki!", Toast.LENGTH_SHORT).show()
                }
                else{
                    listener.onFinanseDelete(finanse.amountFinanse.toString(),finanse.eventId,
                        finanse.finanseName,finanse.userId,finanse.finanseId)
                }
            }

        }


    }

    class FinanseDiffCallback : DiffUtil.ItemCallback<Finanse>() {
        override fun areItemsTheSame(oldItem: Finanse, newItem:Finanse): Boolean {
            return oldItem.finanseName == newItem.finanseName
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Finanse, newItem: Finanse): Boolean {
            return oldItem==newItem
        }
    }


    interface OnEventClickListener{
        fun onFinanseEdit(amountFinanse:String,eventId : String, finanseName:String,userId:String,finanseId:String)
        fun onFinanseDelete(amountFinanse:String,eventId : String, finanseName:String,userId:String,finanseId:String)
    }



}