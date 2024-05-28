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
import com.google.firebase.firestore.firestore

class PaymentAdapter: ListAdapter<Payment, PaymentAdapter.PaymentViewHolder>(PaymentDiffCallback()){

    private var onEventClickListener: OnEventClickListener?=null

    fun setOnEventClickListener(listener: OnEventClickListener){
        onEventClickListener= listener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        val itemView= LayoutInflater.from(parent.context).inflate(R.layout.item_payment, parent, false)
        return PaymentViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PaymentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val paymentText: TextView = itemView.findViewById(R.id.textView_paymentText)
        private val db= Firebase.firestore
        private val buttonAccept: Button= itemView.findViewById(R.id.buttonAccept)
        private val buttonCancel: Button= itemView.findViewById(R.id.buttonCancel)


        fun bind(payment: Payment) {
            db.collection("idEmail")
                .whereEqualTo("userId", payment.userId)
                .get()
                .addOnSuccessListener { document->
                    for(doc in document){
                        val creatorOfPament= doc.getString("userEmail").toString()
                        paymentText.text= "${creatorOfPament} wpłacił ${payment.amountPayment} zł"
                    }
                }
                .addOnFailureListener{e->

                }

            buttonAccept.setOnClickListener{
                val position= adapterPosition
                if(position!=RecyclerView.NO_POSITION){
                    val event= getItem(position)
                    onEventClickListener?.onEventAccept(event)
                }
            }
            buttonCancel.setOnClickListener{
                val position= adapterPosition
                if(position!=RecyclerView.NO_POSITION){
                    val event= getItem(position)
                    onEventClickListener?.onEventCancel(event)
                }
            }
        }
    }

    class PaymentDiffCallback : DiffUtil.ItemCallback<Payment>() {
        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Payment, newItem: Payment): Boolean {
            return oldItem==newItem
        }

        override fun areItemsTheSame(oldItem: Payment, newItem: Payment): Boolean {
            TODO("Not yet implemented")
        }
    }

    interface OnEventClickListener{
        fun onEventAccept(payment:Payment)
        fun onEventCancel(payment:Payment)
    }
}