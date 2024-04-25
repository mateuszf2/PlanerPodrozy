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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        val itemView= LayoutInflater.from(parent.context).inflate(R.layout.item_payment, parent, false)
        return PaymentViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PaymentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val paymentText: TextView = itemView.findViewById(R.id.textView_nameAmount)
        private val db= Firebase.firestore

        fun bind(payment: Payment) {
            paymentText.text= "${payment.userId} wpłacił/wpłaciła na składkę ${payment.amountPayment} zł."
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
}