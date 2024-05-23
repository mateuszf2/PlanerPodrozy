package com.example.planerpodrozy

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class FinanseAdapter: ListAdapter<Finanse, FinanseAdapter.FinanseViewHolder>(FinanseDiffCallback()) {

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
        private val amountMinusPayment: TextView= itemView.findViewById(R.id.textView_amountMinusPayment)
        private val db= Firebase.firestore

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
            amountFinanse.text= "Całkowita kwota składki: ${finanse.amountFinanse.toString()}"

            var usersCount: Int
            var amountLeft: Double
            db.collection("wydarzenia").document(finanse.eventId)
                .get()
                .addOnSuccessListener { usersNumberDoc->
                    usersCount= usersNumberDoc.data?.get("usersNumber").toString().toInt()
                    amountLeft= finanse.amountFinanse/usersCount

                    val currentUser= FirebaseAuth.getInstance().currentUser
                    val userId= currentUser?.uid

                    db.collection("finansePay")
                        .whereEqualTo("finanseId", finanse.finanseId)
                        .whereEqualTo("userId", userId)
                        .get()
                        .addOnSuccessListener { paymentDocuments ->
                            for(pd in paymentDocuments){
                                amountLeft-= pd.getString("amountPay")!!.toDouble()
                            }
                            amountMinusPayment.text= "Musisz jeszcze zapłacić: ${amountLeft.toString()}"
                        }
                        .addOnFailureListener { e->

                        }
                }
                .addOnFailureListener { e->

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
}